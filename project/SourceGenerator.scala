import sbt._
import Keys._


object SourceGenerator {

  import ScaloidSettings._

  private def recursiveListFiles(dir: File, filter: FileFilter): Seq[File] = {
    def step(f: File, files: List[File] = Nil): List[File] =
      if (!f.isDirectory)
        if (filter.accept(f)) f :: files else files
      else f.listFiles.map(step(_, files)).flatten.toList

    step(dir).toSeq
  }

  def generateTask =
    (moduleName, baseDirectory, sourceDirectory in Compile, extract in Scaloid, apiVersion in Scaloid, streams) map {
      (mName, baseDir, srcDir, androidClasses, ver, s) =>
        import NameFilter._

        if (mName == "parent") Nil
        else {
          val stGroupsDir = baseDir / ".." / "project" / "st"
          val templateDir = srcDir / "st"
          val relativePath = Path.relativeTo(templateDir)
          val scalaTemplates = recursiveListFiles(templateDir, (s: String) => s.endsWith(".scala"))
          val stg = new StringTemplateSupport(ver, stGroupsDir / "base.scala.stg")

          scalaTemplates.map { (file: File) =>
            val outFile = srcDir / "scala" / relativePath(file).get
            val params = androidClasses
            IO.write(outFile, stg.render(file, params))
            s.log.info("Generating " + outFile)
            outFile
          }
        }
    }

}
