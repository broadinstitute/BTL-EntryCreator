name := "EntryCreator"

version := "2017.1.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21", // Needed for new versions of akka-http - not sure why
  "org.json4s" %% "json4s-native" % "3.4.2",
  "org.json4s" %% "json4s-ext" % "3.4.2",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.3",
  "com.github.scopt" %% "scopt" % "3.5.0"
)

resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("public")
)
