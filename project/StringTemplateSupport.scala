import sbt._
import org.stringtemplate.v4._
import org.stringtemplate.v4.misc._
import scala.collection.JavaConversions._

class StringTemplateSupport(version: Int, baseGroupFile: File) {

  private val verDic = mapAsJavaMap(generateVersionRangeDictionary(version))

  private val errorListener = new STErrorListener(){
    import org.stringtemplate.v4.misc.STMessage
    override def compileTimeError(msg: STMessage) = msg.cause
    override def runTimeError(msg: STMessage) = msg.cause
    override def IOError(msg: STMessage) = msg.cause
    override def internalError(msg: STMessage) = msg.cause
  }

  private def initGroup(g: STGroup) = {
    g.registerRenderer(classOf[String], new StringRenderer())
    g.defineDictionary("ver", verDic)
    g.setListener(errorListener)
    g
  }

  private def baseGroup = initGroup(new STGroupFile(baseGroupFile.getAbsolutePath, '$', '$'))

  private def withCompanionGroup(file: File)(f: STGroup => String) = {
    val g = baseGroup
    Option(new File(file.absolutePath + ".stg")) filter (_.exists) foreach { cf =>
      val cg = initGroup(new STGroupFile(cf.absolutePath, '$', '$'))
      g.importTemplates(cg)
    }
    f(g)
  }

  def render(file: File, parameters: Map[String, Any]) = withCompanionGroup(file) { g: STGroup =>
    val st = new ST(g, IO.read(file))
    val p = toJava(expandToPackageMap(parameters)).asInstanceOf[java.util.Map[String, Any]]
    p.foreach { case (k, v) =>
      st.add(k, v)
    }
    st.render
  }

  private def generateVersionRangeDictionary(ver: Int): Map[String, Object] =
    (1 to 32).flatMap { v =>
      def kv(prod: Boolean, keys: String*) = keys.map(k => (k +"_"+ v) -> prod.asInstanceOf[Object])
      (kv(ver == v, "eq", "gte", "lte") ++ kv(ver > v, "gt") ++ kv(ver < v, "lt"))
    }.toMap

  private def expandToPackageMap(pkg: Map[String, Any]): Map[String, Any] = {
    def expand(lmap: Map[List[String], Any], level: Int = 0): Map[String, Any] = {
      lmap
        .groupBy(_._1.head) 
        .mapValues(_.map { case (k, v) => k.tail -> v })
        .mapValues { m: Map[List[String], Any] =>
          val (leaves, branches) = m.partition(_._1.length == 1)
          leaves.map { case (k, v) => k.head -> v } ++ expand(branches, level + 1)
        }
        .map(identity)
    }

    val listKeyMap = pkg.map { case (k, v) => k.split('.').toList -> v }
    expand(listKeyMap)
  }

  private def toJava(cc: Any): Any = cc match {
    case s: java.lang.String => s
    case xs: Seq[_] => xs.map(toJava).toArray
    case o: Option[_] => o.map(toJava).getOrElse(null)
    case m: Map[_, _] => mapAsJavaMap(m.mapValues(toJava))
    case s: Set[_] => mapAsJavaMap(s.map(toJava).zip(Stream.continually(true)).toMap)
    case p: Product =>
      // this covers tuples as well as case classes, so there may be a more specific way
      val map = p.getClass.getDeclaredFields.map { f =>
        f.setAccessible(true)
        f.getName -> toJava(f.get(cc))
      }.toMap
      mapAsJavaMap(map)
    case x => x
  }

  private class StringRenderer extends AttributeRenderer {
    import java.util._

    def toString(value: String): String = value 

    override def toString(value: Any, formatName: String, locale: Locale): String = {
      val formats = Option(formatName).getOrElse("").split(",").map(_.trim)
      formats.foldLeft(value.toString)(format)
    }

    def decapitalize(s: String) = if (s.isEmpty) s else s(0).toLower + s.substring(1)
    def simpleName(s: String) = s.split('.').last
    def toJavaConst(s: String) = (s.head +: "[A-Z]".r.replaceAllIn(s.tail, m => "_"+m.group(0))).toUpperCase
    def managerToService(s: String) = {
      val jc = toJavaConst(s.replace("DropBox", "Dropbox"))
      (if (jc.endsWith("MANAGER")) jc.split('_').init.mkString("_")
       else jc) + "_SERVICE"
    }

    private val reservedKeywordsNotInJava = 
      Seq("def", "extends", "implicit", "import", "match", "lazy", "object", "package", 
        "requires", "sealed", "trait", "type", "val", "var", "with", "yield")
    def safeIdentifier(s: String) = if (s.matches("^[0-9].*") || reservedKeywordsNotInJava.contains(s)) "`"+s+"`" else s

    def span(s: String, i: Int) = s.padTo(i, " ").mkString
    val Span = """span(\d+)""".r

    def format(value: String, formatName: String): String = formatName match {
      case "upper"    | "uppercase"    => value.toUpperCase
      case "lower"    | "lowercase"    => value.toLowerCase
      case "cap"      | "capitalize"   => value.capitalize
      case "decap"    | "decapitalize" => decapitalize(value)
      case "simple"   | "simplename"   => simpleName(value)
      case "javaconst"                 => toJavaConst(value)
      case "manager-to-service"        => managerToService(value) // TODO make proper case class for manager instead of this trick
      case "safe-ident"                => safeIdentifier(value)
      case Span(i)                     => span(value, i.toInt)
      case _                           => value
    }
  }

}
