libraryDependencies ++= Seq(
  "org.antlr" % "ST4" % "4.0.8",
  "com.google.android" % "android" % "4.1.1.4" withSources(),
  "com.google.android" % "support-v4" % "r7" withSources(),
  "org.reflections" % "reflections" % "0.9.10",
  "org.slf4j" % "slf4j-nop" % "1.7.13", // for reflections
  "javax.servlet" % "javax.servlet-api" % "3.0.1" // for reflections
)

addSbtPlugin("com.danieltrinh" % "sbt-scalariform" % "1.4.0")

addSbtPlugin("com.thoughtworks.sbt-api-mappings" % "sbt-api-mappings" % "0.2.2")
