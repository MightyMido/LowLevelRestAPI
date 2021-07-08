name := "untitled"

version := "0.1"

scalaVersion := "2.12.14"
val akkaVersion = "2.6.15"
val AkkaHttpVersion = "10.2.4"
val scalaTestVersion = "3.0.5"

libraryDependencies ++= Seq(
  //akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,

  //testing
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  //data base
  "com.oracle.ojdbc" % "ojdbc8" % "19.3.0.0",
  "com.microsoft.sqlserver" % "mssql-jdbc" % "9.2.1.jre15"
)

// have fun man cool mental models

