import java.util.Locale

import org.stringtemplate.v4._
import org.stringtemplate.v4.misc.STNoSuchPropertyException

import sbt._
import scala.collection.JavaConversions._


class StringTemplateSupport(version: Int, templateFile: File, logger: sbt.Logger) {
  import StringTemplateSupport._

  def render(file: File, parameters: Map[String, Any]) = {
    val st = new ST(baseGroup, IO.read(file))
    expandToPackageMap(parameters).foreach {
      case (k, v: AndroidClass) =>
        st.add(k, v)

      case (k, v: Map[String, Any]) =>
        st.add(k, AndroidPackage(v))

      case (k, _) =>
        throw new Error("Unexpected parameter: "+ k)
    }
    st.render
  }

  private val companionTemplate = {
    val maybeSTG =
      Option(new File(templateFile.absolutePath + ".stg")).filter(_.exists).map { cf =>
        new STGroupFile(cf.absolutePath, '$', '$')
      }
    new STCompanionTemplate(maybeSTG)
  }

  private val verDic = mapAsJavaMap(generateVersionRangeDictionary(version))

  private val errorListener = new STErrorListener(){
    import org.stringtemplate.v4.misc.STMessage
    override def compileTimeError(msg: STMessage) = handle(msg)
    override def runTimeError(msg: STMessage) = handle(msg)
    override def IOError(msg: STMessage) = handle(msg)
    override def internalError(msg: STMessage) = handle(msg)

    private def handle(msg: STMessage) = {
      logger.error("ERROR: "+ templateFile.getAbsolutePath)
      logger.error("Message: "+ msg.toString)
      logger.trace(msg.cause)
    }
  }

  private def baseGroup = {
    val g = new STGroup('$', '$')
    g.defineTemplate("license", ScaloidCodeGenerator.license)
    g.registerRenderer(classOf[AndroidClass], new AndroidClassRenderer(companionTemplate))
    g.registerRenderer(classOf[AndroidPackage], new AndroidPackageRenderer(companionTemplate))
    g.registerRenderer(classOf[String], new StringRenderer())
    g.registerModelAdaptor(classOf[AndroidPackage], new AndroidPackageAdaptor)
    g.defineDictionary("ver", verDic)
    g.setListener(errorListener)
    g
  }

  private def generateVersionRangeDictionary(ver: Int): Map[String, Object] =
    (1 to 32).flatMap { v =>
      def kv(prod: Boolean, keys: String*) = keys.map(k => (k +"_"+ v) -> prod.asInstanceOf[Object])
      kv(ver == v, "eq", "gte", "lte") ++ kv(ver > v, "gt", "gte") ++ kv(ver < v, "lt", "lte")
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

}

object StringTemplateSupport {

  class AndroidPackageRenderer(ct: STCompanionTemplate) extends AttributeRenderer {
    override def toString(o: scala.Any, s: String, locale: Locale): String = {
      val wrapped = o.asInstanceOf[AndroidPackage]
      val classes = wrapped.pkg.values.toList.collect { case c: AndroidClass => c }
      s match {
        case null | "" | "wrap-all-classes" => classes.map(new ScaloidCodeGenerator(_, ct).wholeClassDef).mkString("\n\n")
        case "package-implicit-conversions" => classes.map(new ScaloidCodeGenerator(_, ct).implicitConversion).mkString("\n")
        case _ => throw new Error("Invalid format: "+ s)
      }
    }
  }


  class AndroidClassRenderer(ct: STCompanionTemplate) extends AttributeRenderer {

    override def toString(o: scala.Any, s: String, locale: Locale): String = {
      val cls = o.asInstanceOf[AndroidClass]
      val wrapped = new ScaloidCodeGenerator(cls, ct)
      s match {
        case null | "" | "whole" => wrapped.wholeClassDef
        case "rich" => wrapped.richClassDef
        case "system-service" => wrapped.systemServiceHead
        case "implicit-conversion" => wrapped.implicitConversion
        case _ => throw new Error("Invalid format: "+ s)
      }
    }

  }

  case class AndroidPackage(pkg: Map[String, Any]) {
    def get(key: String): Option[Any] = pkg.get(key)
  }

  class AndroidPackageAdaptor extends ModelAdaptor {

    override def getProperty(interpreter: Interpreter, self: ST, o: scala.Any, property: scala.Any, propertyName: String): AnyRef = {
      o.asInstanceOf[AndroidPackage].get(propertyName) match {
        case Some(cls: AndroidClass) => cls
        case Some(pkg: Map[String, Any]) => AndroidPackage(pkg)
        case _ => throw new STNoSuchPropertyException(null, o, propertyName);
      }
    }
  }

  class STCompanionTemplate(stGroup: Option[STGroup]) extends ScaloidCodeGenerator.CompanionTemplate {

    def get(name: String): Option[String] = stGroup.flatMap { stg =>
      stg.lookupTemplate(name) match {
        case null => None
        case compiledST => Some(compiledST.template)
      }
    }

    def safeRender(name: String): String = this.get(name).getOrElse("")
  }

}
