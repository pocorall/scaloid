import sbt._
import Keys._

object ScaloidGenerator {

  val scaloidGeneratorSettings = Seq(
    sourceGenerators in Compile <+= scaloidGenerate
  )

  def scaloidGenerate = (sourceDirectory in Compile, sourceManaged in Compile, streams) map { (srcDir, outDir, s) => 
    import NameFilter._

    val log = s.log

    val templateDir = srcDir / "st"
    val scalaTemplates = templateDir listFiles ((name: String) => name.endsWith(".scala"))
    val relativePath = Path.relativeTo(templateDir)

    scalaTemplates.map { (file: File) =>
      val outFile = outDir / "scala" / relativePath(file).get
      IO.write(outFile, "")
      log.info("Generating " + outFile)
      outFile
    }.toSeq
  }


}
