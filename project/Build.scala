import sbt._
import Keys._

object BuildSettings {
  lazy val basicSettings = Seq(
    version               := "1.2_8-SNAPSHOT",
    organization          := "org.scaloid",
    organizationHomepage  := Some(new URL("http://blog.scaloid.org")),
    description           := "Rapid Android Development with Scala",
    startYear             := Some(2012),
    scalaVersion          := "2.10.1",
    resolvers             ++= Dependencies.resolutionRepos,
    scalacOptions         := Seq(
      "-target:jvm-1.6"
    ),
    javacOptions          ++= Seq(
      "-source", "1.6",
      "-target", "1.6"))
}

object Dependencies {
  val resolutionRepos = Seq(
//    "typesafe releases" at "http://repo.typesafe.com/typesafe/releases",
//    "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
//    "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
//    "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  val android = "com.google.android" % "android" % "2.2.1"
}

object ScaloidBuild extends Build {
  import BuildSettings._
  import Dependencies._

  // configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + "> " }
  }

  // root project
  lazy val scaloid = Project("scaloid", file("."))
    .settings(name := "scaloid", exportJars := true)
    .settings(basicSettings: _*)
    .settings(libraryDependencies ++= provided(android))

}

