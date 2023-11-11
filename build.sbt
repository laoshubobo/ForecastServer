ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "ForecastServer"
  )

val http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.5"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "com.softwaremill.sttp.client3" %% "spray-json" % "3.9.1"
)