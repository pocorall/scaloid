import sbt._
import Keys._

object ScaloidGenerator {
  import AndroidClassExtractor._

  val scaloidGeneratorSettings = Seq(
    sourceGenerators in Compile <+= scaloidGenerate,
    extractKey := extract
  )

  def scaloidGenerate = (sourceDirectory in Compile, sourceManaged in Compile, extractKey, streams) map { (srcDir, outDir, androidClasses, s) => 
    import NameFilter._

    val log = s.log

    val templateDir = srcDir / "template"
    val scalaTemplates = templateDir listFiles ((n: String) => n.endsWith(".scala"))
    val relativePath = Path.relativeTo(templateDir)

    val ver = 8 // TODO as SettingKey
    val stg = new StringTemplateSupport(ver, templateDir / "_helpers.scala.stg")

    scalaTemplates.map { (file: File) =>
      val outFile = outDir / "scala" / relativePath(file).get
      val source = IO.read(file)
      val params = Map(
        "version" -> ver,
        "androidClass" -> androidClasses
      )
      IO.write(outFile, stg.render(source, params))
      log.info("Generating " + outFile)
      outFile
    }.toSeq
  }


}
