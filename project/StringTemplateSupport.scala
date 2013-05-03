import sbt._
import org.stringtemplate.v4._
import org.stringtemplate.v4.misc._
import scala.collection.JavaConversions._

class StringTemplateSupport(version: Int, baseGroupFile: File) {

  val group = {
    val g = new STGroupFile(baseGroupFile.getAbsolutePath, '$', '$')
    g.registerRenderer(classOf[String], new StringRenderer())
    g.defineDictionary("ver", mapAsJavaMap(generateVersionRangeDictionary(version)))
    g.setListener(new STErrorListener(){
      import org.stringtemplate.v4.misc.STMessage
      override def compileTimeError(msg: STMessage) = msg.cause
      override def runTimeError(msg: STMessage) = msg.cause
      override def IOError(msg: STMessage) = msg.cause
      override def internalError(msg: STMessage) = msg.cause
    })
    g
  }

  def render(template: String, parameters: Map[String, Any]) = {
    val st = new ST(group, template)
    parameters.foreach { case (k, v) =>
      val map = toJavaMap(v)
      st.add(k, map)
    }
    st.render
  }

  private def generateVersionRangeDictionary(ver: Int): Map[String, Object] =
    (1 to 32).flatMap { v =>
      def kv(prod: Boolean, keys: String*) = keys.map(_+"_"+v -> prod.asInstanceOf[Object])
      (kv(ver == v, "eq", "gte", "lte") ++ kv(ver > v, "gt") ++ kv(ver < v, "lt"))
    }.toMap
  

  private def toJavaMap(cc: Any, level: Int = 0): java.util.Map[String, Any] = {
    val map = (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f.setAccessible(true)
      val value = f.get(cc) match {
        case s: java.lang.String => s
        case o: Option[_] => o.getOrElse(null)
        case xs: Seq[_] =>
          if (f.getGenericType.toString.contains("java.lang.String")) // TODO compare parameterized types properly
            xs.toArray[Any]
          else
            xs.map(toJavaMap(_, level + 1)).toArray
        // this covers tuples as well as case classes, so there may be a more specific way
        case caseClassInstance: Product => toJavaMap(caseClassInstance)
        case x => x
      }
      a + (f.getName -> value)
    }
    mapAsJavaMap(map)
  }
  

  private def decapitalize(s: String) = if (s.isEmpty) s else s(0).toLower + s.substring(1)

  private class StringRenderer extends AttributeRenderer {
    import java.util._

    def toString(value: String): String = value 

    override def toString(value: Any, formatName: String, locale: Locale): String = {
      val formats = Option(formatName).getOrElse("").split(",").map(_.trim)
      formats.foldLeft(value.toString)(format)
    }

    def format(value: String, formatName: String): String = formatName match {
      case "upper"    | "uppercase"    => value.toUpperCase
      case "lower"    | "lowercase"    => value.toLowerCase
      case "cap"      | "capitalize"   => value.capitalize
      case "decap"    | "decapitalize" => decapitalize(value)
      case _                           => value
    }
  }

}
