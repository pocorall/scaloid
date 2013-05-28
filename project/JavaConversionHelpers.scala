import java.beans.{Introspector, PropertyDescriptor, ParameterDescriptor, IndexedPropertyDescriptor}
import java.lang.reflect.{Array => JavaArray, _}
import org.reflections.ReflectionUtils._
import scala.collection.JavaConversions._


trait JavaConversionHelpers {

  def getSuperclass(cls: Class[_]): Option[Class[_]] =
    cls.getSuperclass match {
      case c: Class[_] => Some(c)
      case _ => cls.getInterfaces.headOption
    }

  def isAbstract(m: Member): Boolean = Modifier.isAbstract(m.getModifiers)
  def isAbstract(c: Class[_]): Boolean = Modifier.isAbstract(c.getModifiers)
  def isFinal(m: Member): Boolean = Modifier.isFinal(m.getModifiers)
  def isFinal(c: Class[_]): Boolean = Modifier.isFinal(c.getModifiers)
  def isInterface(c: Class[_]): Boolean = Modifier.isInterface(c.getModifiers)
  def isStatic(c: Class[_]): Boolean = Modifier.isStatic(c.getModifiers)
  def isConcrete(c: Class[_]): Boolean = ! (isInterface(c) || isStatic(c))
  def isOverride(m: Method): Boolean =
    getSuperclass(m.getDeclaringClass) match {
      case Some(c) =>
        getAllMethods(c,
          withName(m.getName),
          withParameters(m.getParameterTypes: _*)
        ).nonEmpty
      case None => false
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
