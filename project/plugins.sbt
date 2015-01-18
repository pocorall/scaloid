libraryDependencies ++= Seq(
  "org.antlr" % "ST4" % "4.0.7",
  "com.google.android" % "android" % "2.3.3" withSources(),
  "com.google.android" % "support-v4" % "r7" withSources(),
  "org.reflections" % "reflections" % "0.9.9",
  "org.slf4j" % "slf4j-nop" % "1.7.5", // for reflections
  "javax.servlet" % "javax.servlet-api" % "3.0.1" // for reflections
)