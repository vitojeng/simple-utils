import sbt._

object Dependencies {

  val testcontainersVersion = "1.17.2"

  lazy val hikariCP = "com.zaxxer" % "HikariCP" % "4.0.3" excludeAll(
    ExclusionRule(organization = "org.slf4j"),
  )

  lazy val postgresql = "org.postgresql" % "postgresql" % "42.2.5"
  lazy val mysqlConnectorJava = "mysql" % "mysql-connector-java" % "8.0.29"

  lazy val munit = "org.scalameta" %% "munit" % "1.0.0-M5"
  lazy val testcontainers = "org.testcontainers" % "testcontainers" % testcontainersVersion
  lazy val testcontainersPostgresql = "org.testcontainers" % "postgresql" % testcontainersVersion
  lazy val testcontainersMysql = "org.testcontainers" % "mysql" % testcontainersVersion
  lazy val testcontainersJdbc = "org.testcontainers" % "jdbc" % testcontainersVersion
  lazy val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.32"
  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.10"

}