package tw.purple.utils.jdbc

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.Connection
import java.util.Properties
import scala.util.Using

abstract class DbKind(val vendor: String, val driverClass: String)

case object POSTGRES extends DbKind("postgresql", "org.postgresql.Driver")

case object MYSQL extends DbKind("mysql", "com.mysql.cj.jdbc.Driver")


trait JdbcContext extends AutoCloseable {
  val kind: DbKind
  def connection[T](body: Connection => T): T
}

private class JdbcContextImpl(val kind: DbKind, val dataSource: HikariDataSource) extends JdbcContext {

  assert(dataSource != null, "Datasource not initialized")

  def connection[T](body: Connection => T): T = {
    val conn = dataSource.getConnection
    Using.resource(conn) { c => body(c) }
  }

  override def close(): Unit = {
    dataSource.close()
  }

}

object JdbcContext {
  def postgres(): JdbcContextBuilder = new JdbcContextBuilder(POSTGRES)

  def mysql(): JdbcContextBuilder = new JdbcContextBuilder(MYSQL)
}

class JdbcContextBuilder(kind: DbKind) {

  assert(kind!=null)

  private var url: String = _
  private var dataSource: HikariDataSource = _

  def url(ip: String, port: Int, dbName: String, queryString: String = ""): JdbcContextBuilder = {
    val str = s"jdbc:${kind.vendor}://$ip:$port/$dbName"
    url = if (queryString.trim.nonEmpty) str + "?" + queryString.trim else str
    this
  }

  def url(value: String): JdbcContextBuilder = {
    url = value.trim
    this
  }

  def dataSource(username: String, password: String): JdbcContextBuilder = {
    assert( url.nonEmpty, "url cannot be empty." )
    val hikariConfig = new HikariConfig
    hikariConfig.setJdbcUrl(url)
    hikariConfig.setUsername(username)
    hikariConfig.setPassword(password)
    hikariConfig.setDriverClassName(kind.driverClass)

    dataSource(hikariConfig)
  }

  def dataSource(config: HikariConfig): JdbcContextBuilder = {
    assert( dataSource==null, "dataSource already assigned." )
    dataSource = new HikariDataSource(config)
    this
  }

  def dataSource(prop: Properties): JdbcContextBuilder = {
    dataSource(new HikariConfig(prop))
  }

  def dataSource(propertyFileName: String): JdbcContextBuilder = {
    dataSource(new HikariConfig(propertyFileName))
  }

  def build(): JdbcContext = {
    new JdbcContextImpl(kind, dataSource)
  }
}