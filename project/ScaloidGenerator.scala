import sbt._
import Keys._

object ScaloidGenerator {
  import AndroidClassExtractor._

  val scaloidGenerateKey = TaskKey[Seq[File]]("scaloid-generate")

  val scaloidGeneratorSettings = Seq(
    scaloidGenerateKey in Compile <<= scaloidGenerateTask,
    sourceGenerators in Compile <+= scaloidGenerateTask,
    extractKey in Compile <<= extractTask
  )

  def scaloidGenerateTask = (sourceDirectory in Compile, sourceManaged in Compile, extractKey in Compile, streams) map { (srcDir, outDir, androidClasses, s) => 
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
      val params = androidClasses
      IO.write(outFile, stg.render(source, params))
      log.info("Generating " + outFile)
      outFile
    }.toSeq
  }


}
