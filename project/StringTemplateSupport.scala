import sbt._
import org.stringtemplate.v4._
import org.stringtemplate.v4.misc._
import scala.collection.JavaConversions._

class StringTemplateSupport(version: Int, baseGroupFile: File) {

  val group = {
    val g = new STGroupFile(baseGroupFile.getAbsolutePath, '$', '$')
    g.registerRenderer(classOf[String], new StringRenderer())
    g.registerModelAdaptor(classOf[Object], new ScalaObjectAdaptor())
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
      st.add(k, v)
    }
    st.render
  }

  private def generateVersionRangeDictionary(ver: Int): Map[String, Object] =
    (1 to 32).flatMap { v =>
      def kv(prod: Boolean, keys: String*) = keys.map(_+"_"+v -> prod.asInstanceOf[Object])
      (kv(ver == v, "eq", "gte", "lte") ++ kv(ver > v, "gt") ++ kv(ver < v, "lt"))
    }.toMap
  

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

  private class ScalaObjectAdaptor extends ObjectModelAdaptor {
    
    @throws(classOf[STNoSuchPropertyException])
    override def getProperty(interp: Interpreter, self: ST, o: Object, property: Object, propertyName: String): Object = {
      var value: Object = null
      var c = o.getClass
      if (property == null)
        return throwNoSuchProperty(c, propertyName, new Exception("property is null"))

      var member = classAndPropertyToMemberCache.get(c, propertyName)
      if (member == null)
        member = Misc.getMethod(c, propertyName)
      if (member == null)
        return toJava(super.getProperty(interp, self, o, property, propertyName))

      try {
        member match {
          case m: java.lang.reflect.Method => toJava(Misc.invokeMethod(m, o, value))
          case f: java.lang.reflect.Field => toJava(f.get(o))
        }
      } catch {
        case e => throwNoSuchProperty(c, propertyName, new Exception(e))
      }
    }

    def toJava(o: Object): Object = o match {
      case l: List[_] =>
        l.asInstanceOf[List[Object]].map(toJava).toArray
      case m: Map[_, _] =>
        val om = m mapValues (v => toJava(v.asInstanceOf[Object]))
        mapAsJavaMap(om)
      case s: Set[_] => s.asInstanceOf[Set[Object]].map(toJava).toArray
      case _ => o
    }

  }

}
