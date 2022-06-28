//> using scala "2.13.8"
//> using lib "tw.purple::simple-utils:0.1-SNAPSHOT"
//> using lib "mysql:mysql-connector-java:8.0.29"
//> using lib "org.slf4j:slf4j-api:1.7.32"
//> using lib "ch.qos.logback:logback-classic:1.2.10"
//> using resourceDir "./resources"
//
//> using repository "https://s01.oss.sonatype.org/content/repositories/snapshots/"


import tw.purple.utils.jdbc._

object Hello {

  def main(args: Array[String]): Unit = {
    println("Hello")
    val jdbc = JdbcContext.mysql()
              .url("localhost", 3306, "information_schema")
              .dataSource("mysqluser", "mysqlpw")
              .build()
    val tableNames = jdbc.connection { implicit conn =>
      import JdbcUtils.ConnectionImports._
      query("show tables")(_.getString(1))
    }
    println(tableNames.mkString("\n"))
  }

}