import Dependencies._

val scala2Version = "2.13.8"

ThisBuild / organization := "tw.purple"
ThisBuild / version := "0.1-SNAPSHOT"


lazy val root = project
  .in(file("."))
  .settings(
    name := "simple-utils",

    libraryDependencies ++= Seq(
        hikariCP % Compile,

        munit % Test,
        postgresql % Test,
        mysqlConnectorJava % Test,
        testcontainers % Test,
        testcontainersPostgresql % Test,
        testcontainersMysql % Test,
        testcontainersJdbc % Test,
        slf4jApi % Test,
        logbackClassic % Test,
    ),

    scalaVersion := scala2Version
  )
