import sbt._
import Keys._

object ScaloidSettings {

  import SourceGenerator._
  import AndroidClassExtractor._
  import Dependencies._

  val Scaloid = config("scaloid") extend Compile

  lazy val generate = taskKey[Seq[File]]("Generate Scaloid source code from templates")
  lazy val extract = taskKey[Map[String, AndroidClass]]("Extract android classes")
  lazy val apiVersion = settingKey[Int]("Target Android API Version")

  lazy val scaloidSettings = Seq(
    Scaloid / generate := generateTask(moduleName.value, baseDirectory.value, (Compile / sourceDirectory).value, (Scaloid / extract).value, (Scaloid / apiVersion).value, scalaVersion.value, streams.value.log),
    Scaloid / extract := extractTask(moduleName.value, baseDirectory.value, streams.value.log),
    Scaloid / apiVersion := 16,
    libraryDependencies += android
  )

}
