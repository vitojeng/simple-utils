import Dependencies._

val scala2Version = "2.13.8"

lazy val root = project
  .in(file("."))
  .settings(
    name := "simple-utils",
    version := "0.1.0",

    libraryDependencies ++= Seq(
        hikariCP % Compile,

        munit % Test,
        postgresql % Test,
        testcontainers % Test,
        testcontainersPostgresql % Test,
        testcontainersJdbc % Test,
        slf4jApi % Test,
        logbackClassic % Test,
    ),

    scalaVersion := scala2Version
  )
