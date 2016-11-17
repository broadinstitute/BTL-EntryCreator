name := "EntryCreator"

version := "1.0.3"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21", // Needed for new versions of akka-http - not sure why
  "org.json4s" %% "json4s-native" % "3.4.2",
  "org.json4s" %% "json4s-ext" % "3.4.2",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.11", // Going to 2.4.x required Java 8
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-testkit" % "2.4.11" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.github.scopt" %% "scopt" % "3.5.0"
)

resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("public")
)
