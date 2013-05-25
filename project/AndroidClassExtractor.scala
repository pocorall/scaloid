import sbt._
import Keys._

import java.beans.{Introspector, PropertyDescriptor, ParameterDescriptor, IndexedPropertyDescriptor}
import java.lang.reflect.{Array => JavaArray, _}
import java.util.jar.JarFile
import java.net.URI
import org.reflections._
import org.reflections.scanners._
import org.reflections.util._
import org.reflections.ReflectionUtils._
import com.google.common.base.Predicates
import scala.collection.JavaConversions._


object AndroidClassExtractor extends JavaConversionHelpers {

  private val sourceJar = {
    val binUrl = getClass.getClassLoader.getResources("android").toList.head.toString
    val binFile = new File(binUrl.split("/|!").tail.dropRight(2).mkString("/", "/", ""))
    val basePath = binFile.getParentFile.getParentFile
    new JarFile(basePath / "srcs" / (binFile.base +"-sources.jar"))
  }

  private def extractSource(cls: Class[_]) = {
    val fileName = cls.getName.split('$').head.replace(".", "/") + ".java"
    val is = sourceJar.getInputStream(sourceJar.getJarEntry(fileName))
    val bytes = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    new String(bytes)
  }

  private def fixClassParamedType(tpe: ScalaType) =
    tpe.copy(params = tpe.params.map { t =>
      if (t.isVar && t.bounds.head.name == "Any") {
        t.copy(bounds = Seq(ScalaType("AnyRef")))
      } else t
    })

  private def toAndroidClass(cls: Class[_]) = {

    val source = extractSource(cls)

    val superClass = Option(cls.getSuperclass)

    val fullName = cls.getName

    val name = simpleClassName(fullName)
    val tpe = fixClassParamedType(toScalaType(cls))
    val pkg = fullName.split('.').init.mkString
    val parentType = Option(cls.getGenericSuperclass)
                        .map(toScalaType)
                        .filter(_.name.startsWith("android"))

    val superProps: Set[String] =
      superClass.map {
        Introspector.getBeanInfo(_)
          .getPropertyDescriptors
          .map(propDescSignature).toSet[String]
      }.getOrElse(Set())

    val superMethods: Set[String] = 
      superClass.map {
        _.getMethods.map(methodSignature).toSet
      }.getOrElse(Set())

    def toAndroidMethod(m: Method): AndroidMethod = {
      val name = m.getName
      val retType = AndroidClassExtractor.toScalaType(m.getGenericReturnType)
      val argTypes = Option(m.getGenericParameterTypes)
                      .flatten
                      .toSeq
                      .map(AndroidClassExtractor.toScalaType(_))
      val paramedTypes = (retType +: argTypes).filter(_.isVar).distinct

      AndroidMethod(name, retType, argTypes, paramedTypes, isAbstract(m), isOverride(m))
    }

    def isValidProperty(pdesc: PropertyDescriptor): Boolean =
      ! (pdesc.isInstanceOf[IndexedPropertyDescriptor] || superProps(propDescSignature(pdesc)))

    def isListenerSetterOrAdder(m: Method): Boolean = {
      val name = m.getName
      name.matches("^(set|add).+Listener$") && !superMethods(methodSignature(m))
    }

    def isCallbackMethod(m: Method): Boolean =
      ! m.getName.startsWith("get")

    def extractMethodsFromListener(callbackCls: Class[_]): List[AndroidMethod] =
      callbackCls.getMethods.view
        .filter(isCallbackMethod)
        .map(toAndroidMethod)
        .toList

    def getPolymorphicSetters(method: Method): Seq[AndroidMethod] = {
      val name = method.getName
      cls.getMethods.view
        .filter { m => 
          !isAbstract(m) && m.getName == name && 
            m.getParameterTypes.length == 1 && 
            !superMethods(methodSignature(m)) 
        }
        .map(toAndroidMethod)
        .toSeq
    }

    def toAndroidProperty(pdesc: PropertyDescriptor): Option[AndroidProperty] = {
      val name = pdesc.getDisplayName
      var nameClashes = false

      try {
        cls.getMethod(name)
        nameClashes = true
      } catch {
        case e: NoSuchMethodException => // does nothing
      }

      val readMethod = Option(pdesc.getReadMethod) 
      val writeMethod = Option(pdesc.getWriteMethod)

      val getter = readMethod
                      .filter(m => ! superMethods(methodSignature(m)))
                      .map(toAndroidMethod)

      val setters = writeMethod
                      .map(getPolymorphicSetters)
                      .toSeq.flatten
                      .sortBy(_.argTypes(0).name)

      (getter, setters) match {
        case (None, Nil) => None
        case (g, ss) =>
          val tpe = getter.map(_.retType).getOrElse(setters.first.argTypes.first)
          val switch = if (name.endsWith("Enabled")) Some(name.replace("Enabled", "").capitalize) else None
          Some(AndroidProperty(name, tpe, getter, setters, switch, nameClashes))
      }
    }

    def toAndroidListeners(method: Method): Seq[AndroidListener] = {
      val setter = method.getName
      val setterArgTypes = method.getGenericParameterTypes().toSeq.map(toScalaType)
      val callbackClassName = setterArgTypes(0).name
      val callbackMethods   = extractMethodsFromListener(method.getParameterTypes()(0))

      callbackMethods.map { cm =>
        AndroidListener(
          cm.name,
          callbackMethods.find(_.name == cm.name).get.retType,
          cm.argTypes,
          cm.argTypes.nonEmpty,
          setter,
          setterArgTypes,
          callbackClassName,
          callbackMethods.map { icm =>
            AndroidCallbackMethod(
              icm.name,
              icm.retType,
              icm.argTypes,
              icm.name == cm.name
            )
          }
        )
      }.filter(_.isSafe)
    }

    def resolveListenerDuplication(listeners: Seq[AndroidListener]) =
      listeners map { l =>
        if (listeners.filter(l2 => l.name == l2.name && l.setterArgTypes == l2.setterArgTypes).length > 1) {
          val t = "(^set|^add|Listener$)".r.replaceAllIn(l.setter, "")
          l.copy(name = t.head.toLowerCase + t.tail)
        } else l
      }

    val constructorNames: Map[List[String], List[String]] = {
      val constRegex = ("public +"+ name +"""(?:\<[\w\<\>\[\]]+)? *\(([^)]*)\) *(?:\{?|[^;])""").r
      val argRegex = """(.+?) +([a-z][^\[,. ]*)(?:,|$)""".r

      constRegex.findAllIn(source).matchData.map(_.group(1)).filter(_.nonEmpty).map { cGroup =>
        argRegex.findAllIn(cGroup).matchData.map { pMatch =>
          val List(tpe, name) = List(1, 2).map(pMatch.group(_).trim)
          (tpe, name)
        }.toList.unzip
      }.toMap
    }

    def toScalaConstructor(cons: Constructor[_]): ScalaConstructor = {

      val isVarArgs = cons.isVarArgs

      def isImplicit(a: Argument) = {
        a.tpe.simpleName == "Context"
      }

      val javaTypes = cons.getGenericParameterTypes.toList
      val typeStrs = javaTypes.reverse match {
        case Nil => Nil
        case last :: init => (toTypeStr(last, isVarArgs, true) :: init.map(toTypeStr(_, isVarArgs, false))).reverse
      }

      val args = typeStrs match {
        case Nil => Nil
        case _ =>
          val paramNames = constructorNames(typeStrs)
          val types = javaTypes.map(toScalaType)

          paramNames.zip(types).map { case (n, t) => Argument(n, t) }
      }

      val (implicits, explicits) = args.partition(isImplicit)
      val paramedTypes = (tpe.params ++ args.map(_.tpe)).filter(_.isVar).distinct

      ScalaConstructor(args, implicits, explicits, paramedTypes, cons.isVarArgs)
    }

    def getHierarchy(c: Class[_], accu: List[String] = Nil): List[String] =
      if (c == null) accu
      else getHierarchy(c.getSuperclass, simpleClassName(c) :: accu)

    val props = Introspector.getBeanInfo(cls).getPropertyDescriptors.toSeq
                  .filter(isValidProperty)
                  .map(toAndroidProperty)
                  .flatten
                  .sortBy(_.name)

    val listeners = resolveListenerDuplication(
                      cls.getMethods.view
                        .filter(isListenerSetterOrAdder)
                        .map(toAndroidListeners)
                        .flatten
                        .sortBy(_.name)
                        .toSeq)

    val constructors = cls.getConstructors
                        .map(toScalaConstructor)
                        .toSeq
                        .sortBy(_.explicitArgs.length)

    val isA = getHierarchy(cls).toSet

    val hasBlankConstructor = constructors.exists(_.explicitArgs.length == 0)

    AndroidClass(name, pkg, tpe, parentType, constructors, props, listeners, isA, isAbstract(cls), isFinal(cls), hasBlankConstructor)
  }

  def extractTask = (streams) map { s =>

    s.log.info("Extracting class info from Android...")

    val classLoaders = List(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader())

    val r = new Reflections(new ConfigurationBuilder()
      .setScanners(new SubTypesScanner(false), new ResourcesScanner())
      .setUrls(ClasspathHelper.forClassLoader(classLoaders: _*))
      .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("android"))))

    val clss = asScalaSet(r.getSubTypesOf(classOf[java.lang.Object]))
    val res = clss.toList
                .filter {
                  s.log.info("Excluding inner classes for now - let's deal with it later")
                  ! _.getName.contains("$")
                }
                .map(toAndroidClass)
                .map(c => c.tpe.name -> c)
                .toMap

    val values = res.values.toList
    s.log.info("Done.")
    s.log.info("Classes: "+ values.length)
    s.log.info("Properties: "+ values.map(_.properties).flatten.length)
    s.log.info("Listeners: "+ values.map(_.listeners).flatten.length)
    s.log.info("Constructors: "+ values.map(_.constructors).flatten.length)
    res
  }
}


trait JavaConversionHelpers {

  def isAbstract(m: Member): Boolean = Modifier.isAbstract(m.getModifiers)
  def isAbstract(c: Class[_]): Boolean = Modifier.isAbstract(c.getModifiers)
  def isFinal(m: Member): Boolean = Modifier.isFinal(m.getModifiers)
  def isFinal(c: Class[_]): Boolean = Modifier.isFinal(c.getModifiers)
  def isInterface(c: Class[_]): Boolean = Modifier.isInterface(c.getModifiers)
  def isStatic(c: Class[_]): Boolean = Modifier.isStatic(c.getModifiers)
  def isConcrete(c: Class[_]): Boolean = ! (isInterface(c) || isStatic(c))
  def isOverride(m: Method): Boolean =
    m.getDeclaringClass.getSuperclass match {
      case null => false
      case c =>
        getAllMethods(c,
          withName(m.getName),
          withParameters(m.getParameterTypes: _*)
        ).nonEmpty
    }

  def methodSignature(m: Method): String = List(
    m.getName,
    m.getReturnType.getName,
    "["+m.getParameterTypes.map(_.getName).toList.mkString(",")+"]"
  ).mkString(":")

  def propDescSignature(pdesc: PropertyDescriptor): String = List(
    pdesc.getName,
    pdesc.getPropertyType
  ).mkString(":")

  def simpleClassName(s: String): String = s.split(Array('.', '#')).last
  def simpleClassName(c: Class[_]): String = simpleClassName(c.getName)

  private def innerClassDelim(c: Class[_]) = if (isConcrete(c)) "#" else "."

  def toScalaType(_tpe: Type): ScalaType = {
    def step(tpe: Type, level: Int): ScalaType = {
      val nextLevel = level + 1

      if (level > 5)
        ScalaType("_")
      else
        tpe match {
          case null => throw new Error("Property cannot be null")
          case ga: GenericArrayType =>
            ScalaType("Array", Seq(step(ga.getGenericComponentType, nextLevel)))
          case p: ParameterizedType =>
            ScalaType(
              step(p.getRawType, nextLevel).name,
              p.getActualTypeArguments.map(step(_, nextLevel)).toSeq
            )
          case t: TypeVariable[_] =>
            ScalaType(t.getName, Nil, bounds = t.getBounds.map(step(_, nextLevel)).toSeq, isVar = true)
          case w: WildcardType =>
            val bs = w.getUpperBounds.map(step(_, nextLevel)).toSeq.filter(_.name != "Any")
            ScalaType("_", Nil, bounds = bs)
          case c: Class[_] => {
            if (c.isArray) {
              ScalaType("Array", Seq(step(c.getComponentType, nextLevel)))
            } else if (c.isPrimitive) {
              ScalaType(c.getName match {
                case "void" => "Unit"
                case n => n.capitalize
              })
            } else if (c == classOf[java.lang.Object]) {
              ScalaType("Any")
            } else {
              ScalaType(
                c.getName.replace("$", innerClassDelim(c)),
                c.getTypeParameters.map(step(_, nextLevel)).toSeq
              )
            }
          }
          case _ =>
            throw new Error("Cannot find type of " + tpe.getClass + " ::" + tpe.toString)
        }
    }
    step(_tpe, 0)
  }

  def toTypeStr(_tpe: Type, isVarArgs: Boolean, isLast: Boolean): String = {
    def step(tpe: Type): String = {
      val arrayNotation = if (isLast && isVarArgs) "..." else "[]"

      tpe match {
        case ga: GenericArrayType =>
          step(ga.getGenericComponentType) + arrayNotation
        case p: ParameterizedType =>
          p.toString.replace("$", ".")
        case _: WildcardType => "?"
        case c: Class[_] =>
          if (c.isArray) {
            step(c.getComponentType) + arrayNotation
          } else {
            c.getName.replace("$", innerClassDelim(c))
          }
        case _ =>
          tpe.toString
      }
    }
    step(_tpe)
  }

}
