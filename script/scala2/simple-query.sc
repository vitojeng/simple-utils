//> using scala "2.13.8"
//> using lib "tw.purple::simple-utils:0.1-SNAPSHOT"
//> using lib "mysql:mysql-connector-java:8.0.29"
//> using lib "org.slf4j:slf4j-api:1.7.32"
//> using lib "ch.qos.logback:logback-classic:1.2.10"
//
//> using repository "https://s01.oss.sonatype.org/content/repositories/snapshots/"


/**
 *
 * [scala2]$ scala-cli simple-query.sc --jar .
 *
 */

import tw.purple.utils.jdbc._
import JdbcOps._
import scala.util.Using

def newJdbcContext = JdbcContext.mysql()
                      .url("localhost", 3306, "information_schema")
                      .dataSource("mysqluser", "mysqlpw")
                      .build()


val SQL_TABLE_COLUMNS =
  """select c.TABLE_SCHEMA schema_name,
    |   c.TABLE_NAME table_name,
    |   c.COLUMN_NAME column_name,
    |   c.DATA_TYPE type_name
    |from information_schema.COLUMNS c
    |where c.TABLE_SCHEMA = 'information_schema' and c.TABLE_NAME = ?;
    |""".stripMargin

case class Column(schemaName: String, tableName: String, columnName: String, typeName: String)

Using.resource(newJdbcContext) { ctx =>
  val tableName = "COLUMNS"
  val columns = ctx.query(SQL_TABLE_COLUMNS, Seq(tableName)) { rs =>
    Column(rs.getString(1), rs.getString(2),
      rs.getString(3), rs.getString(4))
  }
  println(columns.mkString("\n"))
}

