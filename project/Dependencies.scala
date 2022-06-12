import sbt._

object Dependencies {

  val testcontainersVersion = "1.17.2"

  lazy val postgresql = "org.postgresql" % "postgresql" % "42.2.5"

  lazy val munit = "org.scalameta" %% "munit" % "1.0.0-M5"
  lazy val testcontainers = "org.testcontainers" % "testcontainers" % testcontainersVersion
  lazy val testcontainersPostgresql = "org.testcontainers" % "postgresql" % testcontainersVersion
  lazy val testcontainersJdbc = "org.testcontainers" % "jdbc" % testcontainersVersion

}