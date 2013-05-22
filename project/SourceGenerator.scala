import sbt._
import Keys._


object SourceGenerator {
  import ScaloidSettings._

  def recursiveListFiles(dir: File, filter: FileFilter): Seq[File] = {
    def step(f: File, files: List[File] = Nil): List[File] =
      if (! f.isDirectory)
        if (filter.accept(f)) f :: files else files
      else f.listFiles.map(step(_, files)).flatten.toList

    step(dir).toSeq
  }

  def generateTask = (baseDirectory, sourceDirectory in Compile, extract in Scaloid, streams) map {
    (baseDir, srcDir, androidClasses, s) =>
      import NameFilter._

      val log = s.log

      val stGroupsDir = baseDir / "project" / "st"
      val templateDir = srcDir / "st"
      val relativePath = Path.relativeTo(templateDir)
      val scalaTemplates = recursiveListFiles(templateDir, (s: String) => s.endsWith(".scala"))

      val ver = 8 // TODO as SettingKey
      val stg = new StringTemplateSupport(ver, stGroupsDir / "base.scala.stg")

      scalaTemplates.map { (file: File) =>
        val outFile = srcDir / "scala" / relativePath(file).get
        val source = IO.read(file)
        val params = androidClasses
        IO.write(outFile, stg.render(source, params))
        log.info("Generating " + outFile)
        outFile
      }
  }

}
