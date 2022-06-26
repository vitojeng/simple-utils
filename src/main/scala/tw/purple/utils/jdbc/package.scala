package tw.purple.utils

package object jdbc {

  sealed trait DatabaseKind

  case object POSTGRES extends DatabaseKind

  case object MYSQL extends DatabaseKind

  class JdbcContext(val kind: DatabaseKind, val vendor: String, val driverClass: String, val ip: String, val port: Int, val dbName: String) {
    def url(queryString: String = ""): String = {
      val url = s"jdbc:$vendor://$ip:$port/$dbName"
      if (queryString.nonEmpty) url + "?" + queryString else url
    }
  }

  object JdbcContext {
    def postgres(ip: String, port: Int, dbName: String): JdbcContext = {
      new JdbcContext(POSTGRES, "postgresql", "org.postgresql.Driver", ip, port, dbName)
    }
    def mysql(ip: String, port: Int, dbName: String): JdbcContext = {
      new JdbcContext(MYSQL, "mysql", "com.mysql.cj.jdbc.Driver", ip, port, dbName)
    }
  }

}
