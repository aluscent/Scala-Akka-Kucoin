version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.15"
val akkaVersion = "2.6.16"
val akkaHttpVersion = "10.2.7"

lazy val root = (project in file("."))
  .settings(
    name := "Scala-Akka-Kucoin",
    idePackagePrefix := Some("org")
  )

val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,

  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,

  "org.apache.kafka" % "kafka-clients" % "3.0.0",
  //"org.apache.kafka" %% "kafka" % "3.0.0",
//  "io.confluent" % "kafka-avro-serializer" % "7.0.0",

  "org.elasticsearch" % "elasticsearch" % "7.16.2",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.16.2",

  "org.slf4j" % "slf4j-simple" % "2.0.0-alpha5"
)

libraryDependencies ++= dependencies