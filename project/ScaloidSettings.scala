import sbt._
import Keys._


object ScaloidSettings {
  import SourceGenerator._
  import AndroidClassExtractor._
  import Dependencies._

  val Scaloid = config("scaloid") extend (Compile)

  lazy val generate = taskKey[Seq[File]]("Generate Scaloid source code from templates")
  lazy val extract  = taskKey[Map[String, AndroidClass]]("Extract android classes")
  lazy val apiVersion = settingKey[Int]("Target Android API Version")

  lazy val scaloidSettings = Seq(
    generate in Scaloid <<= generateTask,
    extract in Scaloid <<= extractTask,
    apiVersion in Scaloid := 8,
    libraryDependencies += android
  )

}
