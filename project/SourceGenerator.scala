import sbt._
import Keys._
import sbt.internal.util.ManagedLogger
import scalariform.formatter.ScalaFormatter
import scalariform.formatter.preferences._


object SourceGenerator {

  import ScaloidSettings._

  private def recursiveListFiles(dir: File, filter: FileFilter): Seq[File] = {
    def step(f: File, files: List[File] = Nil): List[File] =
      if (f.isDirectory) f.listFiles.map(step(_, files)).flatten.toList
      else if (filter.accept(f)) f :: files
      else files

    step(dir).toSeq
  }

  def generateTask(mName: String, baseDir: File, srcDir: File, androidClasses: Map[String, AndroidClass], androidApiVersion: Int, scalaVersion: String, log: ManagedLogger): Seq[File] = {
        import NameFilter._

        if (mName == "parent") Nil
        else {
          val stGroupsDir = baseDir / ".." / "project" / "st"
          val templateDir = srcDir / "st"
          val relativePath = Path.relativeTo(templateDir)
          val scalaTemplates = recursiveListFiles(templateDir, (s: String) => s.endsWith(".scala"))

          scalaTemplates.map { (file: File) =>
            val outFile = srcDir / "scala" / relativePath(file).get
            log.info("Generating: " + outFile)

            val stg = new StringTemplateSupport(androidApiVersion, file, log)
            val params = androidClasses
            val generatedCode = stg.render(file, params)
            IO.write(outFile, generatedCode)

            log.info("Formatting: "+ outFile)
            try {
              val formattedCode = formatCode(generatedCode, scalaVersion)
              if (generatedCode != formattedCode) {
                log.info("Reformatted: "+ outFile)
                IO.write(outFile, formattedCode)
              }
            } catch {
              case e: Throwable =>
                log.error("Failed to generate "+ outFile)
                log.trace(e)
            }

            outFile
          }
        }
    }

  private val scalariformPreferences = {
    FormattingPreferences()
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
  }

  private def formatCode(code: String, scalaVersion: String): String = {
    ScalaFormatter.format(
      code,
      scalariformPreferences,
      scalaVersion = pureScalaVersion(scalaVersion)
    )
  }

  private def pureScalaVersion(scalaVersion: String): String = scalaVersion.split("-").head

}
