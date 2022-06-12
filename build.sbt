import Dependencies._

val scala2Version = "2.13.8"
val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "simple-utils",
    version := "0.1.0",

    libraryDependencies ++= Seq(
        munit % Test,
    ),

    // To make the default compiler and REPL use Dotty
    scalaVersion := scala3Version,

    // To cross compile with Scala 3 and Scala 2
    crossScalaVersions := Seq(scala3Version, scala2Version)
  )
