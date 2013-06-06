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
}

