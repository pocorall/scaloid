import sbt._
import Keys._

object Dependencies {
  val resolutionRepos = Seq(
    //    "typesafe releases" at "http://repo.typesafe.com/typesafe/releases",
    //    "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
    //    "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
    //    "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

  val android = "com.google.android" % "android" % "2.2.1" % "provided"
  val android_support_v4 = "com.google.android" % "support-v4" % "r7" % "provided"
  val scaloidVersion = "3.3-8"
  val scaloid = "org.scaloid" %% "scaloid" % scaloidVersion
}


object ScaloidBuild extends Build {

  import ScaloidSettings._
  import Dependencies._


  lazy val basicSettings = Seq(
    organization := "org.scaloid",
    organizationHomepage := Some(new URL("http://blog.scaloid.org")),
    description := "Less Painful Android Development with Scala",
    startYear := Some(2012),
    scalaVersion := "2.10.3",
    version := scaloidVersion,
    resolvers ++= Dependencies.resolutionRepos,
    publishMavenStyle := true,
    publishTo <<= version {
      (v: String) =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := {
      _ => false
    },
    pomExtra :=
      <url>http://scaloid.org</url>
        <licenses>
          <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:pocorall/scaloid.git</url>
          <connection>scm:git:git@github.com:pocorall/scaloid.git</connection>
          <developerConnection>scm:git:git@github.com:pocorall/scaloid.git</developerConnection>
        </scm>
        <developers>
          <developer>
            <id>pocorall</id>
            <name>Sung-Ho Lee</name>
            <email>pocorall@gmail.com</email>
          </developer>
        </developers>,
    scalacOptions := Seq(
      "-target:jvm-1.6", "-deprecation", "-feature"
    ),
    javacOptions ++= Seq(
      "-source", "1.6",
      "-target", "1.6"))

  // configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := {
      s => Project.extract(s).currentProject.id + "> "
    }
  }

  //  root project
  lazy val parent = Project("parent", file("."))
    .settings(scaloidSettings: _*)
    .settings(publish := {}, publishLocal := {})
    .aggregate(common, support_v4, util)

  lazy val common = Project("scaloid", file("scaloid-common"))
    .settings(name := "scaloid", exportJars := true)
    .settings(basicSettings: _*)
    .settings(scaloidSettings: _*)

  lazy val support_v4 = Project("support-v4", file("scaloid-support-v4"))
    .settings(name := "scaloid-support-v4", exportJars := true)
    .settings(basicSettings: _*)
    .settings(scaloidSettings: _*)
    .settings(libraryDependencies += android_support_v4)
    .settings(libraryDependencies += scaloid)
    .dependsOn(common)

  lazy val util = Project("util", file("scaloid-util"))
    .settings(name := "scaloid-util", exportJars := true)
    .settings(basicSettings: _*)
    .settings(scaloidSettings: _*)
    .settings(libraryDependencies += scaloid)
    .dependsOn(common)
}

