import sbt._
import Keys._

object ScaloidBuild extends Build {
  import ScaloidSettings._
  import Dependencies._

  lazy val basicSettings = Seq(
    version               := "2.0-8-RC1",
    organization          := "org.scaloid",
    organizationHomepage  := Some(new URL("http://blog.scaloid.org")),
    description           := "Rapid Android Development with Scala",
    startYear             := Some(2012),
    scalaVersion          := "2.10.1",
    resolvers             ++= Dependencies.resolutionRepos,
    scalacOptions         := Seq(
      "-target:jvm-1.6", "-deprecation", "-feature"
    ),
    javacOptions          ++= Seq(
      "-source", "1.6",
      "-target", "1.6"))

  // configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + "> " }
  }


  // root project
//  lazy val parent = Project("parent", file("."))
//    .settings(scaloidSettings: _*)
//    .settings(publish := {}, publishLocal := {})
//    .aggregate(common, support_v4)

  lazy val common = Project("scaloid", file("scaloid-common"))
    .settings(name := "scaloid", exportJars := true)
    .settings(basicSettings: _*)
    .settings(scaloidSettings: _*)

  lazy val support_v4 = Project("scaloid-support-v4", file("scaloid-support-v4"))
    .settings(name := "scaloid-support-v4", exportJars := true)
    .settings(basicSettings: _*)
    .settings(scaloidSettings: _*)
    .settings(libraryDependencies += android_support_v4)
    .dependsOn(common)
}

