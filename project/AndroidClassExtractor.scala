import sbt._
import Keys._

import java.beans.{Introspector, PropertyDescriptor, ParameterDescriptor, IndexedPropertyDescriptor}
import java.lang.reflect._
import org.reflections._
import org.reflections.ReflectionUtils._
import org.reflections.scanners._
import com.google.common.base.Predicates
import scala.collection.JavaConversions._


object AndroidClassExtractor {

  def toScalaType(tpe: Type): ScalaType =
    tpe match {
      case null => throw new Error("Property cannot be null")
      case t: GenericArrayType =>
        ScalaType("Array", Seq(toScalaType(t.getGenericComponentType)))
      case t: ParameterizedType =>
        ScalaType(
          toScalaType(t.getRawType).name,
          t.getActualTypeArguments.map(toScalaType).toSeq
        )
      case t: TypeVariable[_] =>
        ScalaType(t.getName, t.getBounds.map(toScalaType).toSeq, true)
      case t: WildcardType => ScalaType("_")
      case t: Class[_] => {
        if (t.isArray) {
          ScalaType("Array", Seq(toScalaType(t.getComponentType)))
        } else if (t.isPrimitive) {
          ScalaType(t.getName match {
            case "void" => "Unit"
            case n => n.capitalize
          })
        } else {
          ScalaType(
            t.getName.replace("$", "."),
            t.getTypeParameters.take(1).map(_ => ScalaType("_")).toSeq
          )
        }
      }
      case _ =>
        throw new Error("Cannot find type of " + tpe.getClass + " ::" + tpe.toString)
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

  private def toAndroidClass(cls: Class[_]) = {

    val superClass = Option(cls.getSuperclass)

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

    def toScalaConstructor(constructor: Constructor[_]) = {
      def isImplicit(t: ScalaType) = {
        t.simpleName == "Context"
      }

      val args = constructor.getGenericParameterTypes.map(toScalaType).toSeq
      val (implicits, explicits) = args.partition(isImplicit)
      ScalaConstructor(
        args,
        implicits,
        explicits
      )
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

    val fullName = cls.getName

    val name = fullName.split('.').last
    val tpe = toScalaType(cls)
    val pkg = fullName.split('.').init.mkString
    val parentType = Option(cls.getGenericSuperclass)
                        .map(toScalaType)
                        .filter(_.name.startsWith("android"))

    val constructors =
      getConstructors(cls)
        .map(toScalaConstructor)
        .toSeq
        .sortBy(_.argTypes.length)

    val isA = getHierarchy(cls).toSet

    AndroidClass(name, pkg, tpe, parentType, constructors, props, listeners, isA, isAbstract(cls), isFinal(cls))
  }

  def extractTask = (streams) map { s =>

    s.log.info("Extracting class info from Android...")

    val r = new Reflections("android", new SubTypesScanner(false), new TypeElementsScanner(), new TypeAnnotationsScanner())
    val clss: Set[Class[_]] = asScalaSet(r.getSubTypesOf(classOf[java.lang.Object])).toList.toSet
    val res = clss.toList
                .map(toAndroidClass)
                .filter {
                  s.log.info("Excluding inner classes for now - let's deal with it later")
                  ! _.name.contains("$")
                }
                .map(c => c.tpe.name -> c)
                .toMap

    val values = res.values.toList
    s.log.info("Done.")
    s.log.info("Classes: "+ values.length)
    s.log.info("Properties: "+ values.map(_.properties).flatten.length)
    s.log.info("Listeners: "+ values.map(_.listeners).flatten.length)
    res
  }
}

