import sbt._
import Keys._

import java.beans.{Introspector, PropertyDescriptor, ParameterDescriptor, IndexedPropertyDescriptor}
import java.lang.reflect._
import java.util.jar.JarFile
import java.net.URI
import org.reflections._
import org.reflections.ReflectionUtils._
import org.reflections.scanners._
import com.google.common.base.Predicates
import scala.collection.JavaConversions._


object AndroidClassExtractor {

  def toScalaType(_tpe: Type): ScalaType = {
    def step(tpe: Type, level: Int): ScalaType = {
      val nextLevel = level + 1

      if (level > 5)
        ScalaType("_")
      else
        tpe match {
          case null => throw new Error("Property cannot be null")
          case t: GenericArrayType =>
            ScalaType("Array", Seq(step(t.getGenericComponentType, nextLevel)))
          case t: ParameterizedType =>
            ScalaType(
              step(t.getRawType, nextLevel).name,
              t.getActualTypeArguments.map(step(_, nextLevel)).toSeq
            )
          case t: TypeVariable[_] =>
            ScalaType(t.getName, Nil, bounds = t.getBounds.map(step(_, nextLevel)).toSeq, isVar = true)
          case t: WildcardType =>
            val bs = t.getUpperBounds.map(step(_, nextLevel)).toSeq.filter(_.name != "Any")
            ScalaType("_", Nil, bounds = bs)
          case t: Class[_] => {
            if (t.isArray) {
              ScalaType("Array", Seq(step(t.getComponentType, nextLevel)))
            } else if (t.isPrimitive) {
              ScalaType(t.getName match {
                case "void" => "Unit"
                case n => n.capitalize
              })
            } else if (t == classOf[java.lang.Object]) {
              ScalaType("Any")
            } else {
              ScalaType(
                t.getName.replace("$", "."),
                t.getTypeParameters.map(step(_, nextLevel)).toSeq
              )
            }
          }
          case _ =>
            throw new Error("Cannot find type of " + tpe.getClass + " ::" + tpe.toString)
        }
    }
    step(_tpe, 0)
  }

  private def isAbstract(m: Member): Boolean = Modifier.isAbstract(m.getModifiers)
  private def isAbstract(c: Class[_]): Boolean = Modifier.isAbstract(c.getModifiers)
  private def isFinal(m: Member): Boolean = Modifier.isFinal(m.getModifiers)
  private def isFinal(c: Class[_]): Boolean = Modifier.isFinal(c.getModifiers)

  private def isOverride(m: Method): Boolean =
    m.getDeclaringClass.getSuperclass match {
      case null => false
      case c =>
        getAllMethods(c,
          withName(m.getName),
          withParameters(m.getParameterTypes: _*)
        ).nonEmpty
    }

  private def methodSignature(m: Method): String = List(
    m.getName,
    m.getReturnType.getName,
    "["+m.getParameterTypes.map(_.getName).toList.mkString(",")+"]"
  ).mkString(":")

  private def propDescSignature(pdesc: PropertyDescriptor): String = List(
    pdesc.getName,
    pdesc.getPropertyType
  ).mkString(":")

  val sourceJar = {
    val binUrl = getClass.getClassLoader.getResources("android").toList.head.toString
    val binFile = new File(binUrl.split("/|!").tail.dropRight(2).mkString("/", "/", ""))
    val basePath = binFile.getParentFile.getParentFile
    val srcJar = new JarFile(basePath / "srcs" / (binFile.base +"-sources.jar"))
    val docJar = new JarFile(basePath / "docs" / (binFile.base +"-javadoc.jar"))

    srcJar
  }

  def extractSource(cls: Class[_]) = {
    val fileName = cls.getName.split('$').head.replace(".", "/") + ".java"
    val is = sourceJar.getInputStream(sourceJar.getJarEntry(fileName))
    val bytes = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    new String(bytes)
  }

  def fixClassParamedType(tpe: ScalaType) =
    tpe.copy(params = tpe.params.map { t =>
      if (t.isVar && t.bounds.head.name == "Any") {
        t.copy(bounds = Seq(ScalaType("AnyRef")))
      } else t
    })

  private def toAndroidClass(cls: Class[_]) = {

    val source = extractSource(cls)

    val superClass = Option(cls.getSuperclass)

    val fullName = cls.getName

    val name = fullName.split('.').last
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

      def isImplicit(a: Argument) = {
        a.tpe.simpleName == "Context"
      }

      def arrayNotation(implicit isLast: Boolean) = if (isLast && cons.isVarArgs) "..." else "[]"

      def toTypeStr(t: Type)(implicit isLast: Boolean = false): String = t match {
        case t: GenericArrayType =>
          toTypeStr(t.getGenericComponentType) + arrayNotation
        case t: ParameterizedType =>
          t.toString.replace("$", ".")
        case t: WildcardType => "?"
        case t: Class[_] =>
          if (t.isArray) {
            toTypeStr(t.getComponentType) + arrayNotation
          } else {
            t.getName.replace("$", ".")
          }
        case _ =>
          t.toString
      }

      val javaTypes = cons.getGenericParameterTypes.toList
      val typeStrs = javaTypes.reverse match {
        case Nil => Nil
        case last :: init => (toTypeStr(last)(true) :: init.map(toTypeStr)).reverse
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
      else getHierarchy(c.getSuperclass, c.getName.split('.').last :: accu)

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
                        .filter(c => name != "HeaderViewListAdapter")
                        .toSeq

    val isA = getHierarchy(cls).toSet

    AndroidClass(name, pkg, tpe, parentType, constructors, props, listeners, isA, isAbstract(cls), isFinal(cls))
  }

  def extractTask = (streams) map { s =>

    s.log.info("Extracting class info from Android...")

    val r = new Reflections("android", new SubTypesScanner(false), new TypeElementsScanner(), new TypeAnnotationsScanner())
    val clss: Set[Class[_]] = asScalaSet(r.getSubTypesOf(classOf[java.lang.Object])).toList.toSet
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

