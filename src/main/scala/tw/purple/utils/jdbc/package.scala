package tw.purple.utils

package object jdbc {

  sealed trait DatabaseKind

  case object POSTGRES extends DatabaseKind

  case object MYSQL extends DatabaseKind

  abstract class JdbcConnect(val kind: DatabaseKind, val vendor: String, val driverClass: String, val ip: String, val port: Int, val dbName: String) {
    def url(queryString: String = ""): String = {
      val url = s"jdbc:$vendor://$ip:$port/$dbName"
      if (queryString.nonEmpty) url + "?" + queryString else url
    }
  }
  case class PostgresConnect(override val ip: String, override val port: Int, override val dbName: String)
                extends JdbcConnect(POSTGRES, "postgresql", "org.postgresql.Driver", ip, port, dbName)
  case class MysqlConnect(override val ip: String, override val port: Int, override val dbName: String)
                extends JdbcConnect(MYSQL, "mysql", "com.mysql.cj.jdbc.Driver", ip, port, dbName)

}
