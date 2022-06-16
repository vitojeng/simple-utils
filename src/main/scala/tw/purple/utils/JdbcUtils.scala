package tw.purple.utils

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import scala.util.Using


object JdbcUtils {

  sealed trait DatabaseKind {
    def driverClass: String
  }

  case object POSTGRES extends DatabaseKind {
    override def driverClass = "org.postgresql.Driver"
  }

  case class JdbcConnect(kind: DatabaseKind, ip: String, port: Int, dbName: String)

  implicit class JdbcConnectOps(private val db: JdbcConnect) extends AnyVal {

    private def dbStr = db.kind match {
      case POSTGRES => "postgresql"
      case _ => throw new RuntimeException("Unsupported database: " + db.kind)
    }

    def url(queryString: String = ""): String = {
      val url = s"jdbc:$dbStr://${db.ip}:${db.port}/${db.dbName}"
      if (queryString.nonEmpty) url + "?" + queryString else url
    }

    def connection(url: String): Connection =
      DriverManager.getConnection(url)

    def connection(url: String, username: String, password: String): Connection =
      DriverManager.getConnection(url, username, password)

    def datasource(url: String, username: String, password: String): HikariDataSource = {
      val hikariConfig = new HikariConfig
      hikariConfig.setJdbcUrl(url)
      hikariConfig.setUsername(username)
      hikariConfig.setPassword(password)
      hikariConfig.setDriverClassName(db.kind.driverClass)
      new HikariDataSource(hikariConfig)
    }
  }



  implicit class ConnectionOps(private val conn: Connection) extends AnyVal {

    private def _iteratorOf[T](resultSet: ResultSet)(f: ResultSet => T): Iterator[T] = {
      new Iterator[T] {
        def hasNext: Boolean = resultSet.next()

        def next(): T = f(resultSet)
      }
    }

    private def _setParameters(statement: PreparedStatement, parameters: Seq[Any]): Unit = {
      statement.clearParameters()
      if (parameters.nonEmpty) {
        parameters.zipWithIndex.foreach { case (value, i) =>
          val parameterIndex = i + 1
          value match {
            case v: String => statement.setString(parameterIndex, v)
            case v: Long => statement.setLong(parameterIndex, v)
            case v: Int => statement.setInt(parameterIndex, v)
            case v: Boolean => statement.setBoolean(parameterIndex, v)
            case null => statement.setObject(parameterIndex, null)
            case v => throw new RuntimeException("The query parameter type not supported: " + v.getClass.toString)
          }
        }
      }
    }

    def query[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T): Seq[T] = {
      Using.Manager { use =>
        val statement = use(conn.prepareStatement(sql))
        _setParameters(statement, parameters)
        val rs = use(statement.executeQuery())
        _iteratorOf(rs)(f).toSeq
      }.get
    }

    def update(sql: String, parameters: Seq[Any] = Seq.empty): Int = {
      Using.resource(conn.prepareStatement(sql)) { statement =>
        _setParameters(statement, parameters)
        statement.executeUpdate()
      }
    }

  }

}