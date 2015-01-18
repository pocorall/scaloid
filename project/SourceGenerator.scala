import sbt._
import Keys._
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

  def generateTask =
    (moduleName, baseDirectory, sourceDirectory in Compile, extract in Scaloid, apiVersion in Scaloid, scalaVersion,streams) map {
      (mName, baseDir, srcDir, androidClasses, androidApiVersion, scalaVersion, s) =>
        import NameFilter._

        if (mName == "parent") Nil
        else {
          val stGroupsDir = baseDir / ".." / "project" / "st"
          val templateDir = srcDir / "st"
          val relativePath = Path.relativeTo(templateDir)
          val scalaTemplates = recursiveListFiles(templateDir, (s: String) => s.endsWith(".scala"))

          scalaTemplates.map { (file: File) =>
            val outFile = srcDir / "scala" / relativePath(file).get
            s.log.info("Generating: " + outFile)

            val stg = new StringTemplateSupport(androidApiVersion, file, s.log)
            val params = androidClasses
            val generatedCode = stg.render(file, params)
            IO.write(outFile, generatedCode)

            s.log.info("Formatting: "+ outFile)
            try {
              val formattedCode = formatCode(generatedCode, scalaVersion)
              if (generatedCode != formattedCode) {
                s.log.info("Reformatted: "+ outFile)
                IO.write(outFile, formattedCode)
              }
            } catch {
              case e: Throwable =>
                s.log.error("Failed to generate "+ outFile)
                s.log.trace(e)
            }

            outFile
          }
        }
    }

  private val scalariformPreferences = {
    FormattingPreferences()
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
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
