ThisBuild / organization := "tw.purple"
ThisBuild / organizationName := "Purple software"
ThisBuild / organizationHomepage := Some(url("https://github.com/vitojeng/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/vitojeng/simple-utils"),
    "scm:git@github.com:vitojeng/simple-utils.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "vitojeng",
    name  = "Vito Jeng",
    email = "vito@purple.tw",
    url   = url("https://github.com/vitojeng/")
  )
)

ThisBuild / description := "Describe your project here..."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/vitojeng/simple-utils"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / publishMavenStyle := true

ThisBuild / versionScheme := Some("early-semver")
