import sbt._
import Keys._


object ScaloidSettings {
  import SourceGenerator._
  import AndroidClassExtractor._
  import Dependencies._

  val Scaloid = config("scaloid") extend (Compile)

  lazy val generate = TaskKey[Seq[File]]("generate")
  lazy val extract  = TaskKey[Map[String, AndroidClass]]("extract-android-classes")

  lazy val scaloidSettings = Seq(
    generate in Scaloid <<= generateTask,
    extract in Scaloid <<= extractTask,
    libraryDependencies += android
  )

}
