import sbt._

object Dependencies {
  val android = "com.google.android" % "android" % "4.1.1.4" % "provided"
  val android_support_v4 = "com.google.android" % "support-v4" % "r7" % "provided"
  val scaloidVersion = "4.4"
  val scaloid = "org.scaloid" %% "scaloid" % scaloidVersion

  val robolectric = "org.robolectric" % "robolectric" % "2.4" % "test"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.1.4" % "test"
  val junit = "junit" % "junit" % "4.12" % "test"
  val junitInterface = "com.novocode" % "junit-interface" % "0.11" % "test"
}
