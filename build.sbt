import Dependencies._

import ScaloidSettings._

lazy val basicSettings = Seq(
  organization := "org.scaloid",
  organizationHomepage := Some(new URL("http://blog.scaloid.org")),
  description := "Less Painful Android Development with Scala",
  startYear := Some(2012),
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12"), // only 2.11.x for now
  version := scaloidVersion,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := {
    _ => false
  },
  pomExtra :=
    <url>https://scaloid.org</url>
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
    "-target", "1.6"),
  resolvers += "Android Repository" at (new File(System.getenv("ANDROID_HOME")) / "extras" / "android" / "m2repository").getCanonicalFile.toURI.toString,
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
)

//  root project
lazy val parent = project.in(file("."))
  .settings(basicSettings: _*)
  .settings(scaloidSettings: _*)
  .settings(publish := {}, publishLocal := {})
  .aggregate(common, support_v4, util)

lazy val common = Project("scaloid", file("scaloid-common"))
  .settings(name := "scaloid", exportJars := true)
  .settings(basicSettings: _*)
  .settings(scaloidSettings: _*)
  .settings(libraryDependencies ++= Seq(robolectric, scalaTest, junit, junitInterface, android),
    libraryDependencies += ("org.scala-lang" % "scala-reflect" % scalaVersion.value))
  //RobolectricTestRunner requires "fork" to reflect test code changes without sbt restart.
  .settings(fork in Test := true)

lazy val support_v4 = Project("support-v4", file("scaloid-support-v4"))
  .settings(name := "scaloid-support-v4", exportJars := true)
  .settings(basicSettings: _*)
  .settings(scaloidSettings: _*)
  .settings(libraryDependencies += android_support_v4)
  .settings(libraryDependencies += scaloid)
  .dependsOn(common)

lazy val util = project.in(file("scaloid-util"))
  .settings(name := "scaloid-util", exportJars := true)
  .settings(basicSettings: _*)
  .settings(scaloidSettings: _*)
  .settings(libraryDependencies += scaloid)
  .dependsOn(common)

