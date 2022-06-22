package tw.purple.utils.jdbc

import com.zaxxer.hikari.HikariDataSource
import munit.Fixture
import org.testcontainers.containers.{MySQLContainer, PostgreSQLContainer}
import tw.purple.utils.jdbc.JdbcUtils._

object DbFixtures {
  val USERNAME = "myuser"
  val PASSWORD = "mypass"
  val DBNAME = "test"

  val postgres = new Fixture[PostgreSQLContainer[_]]("postgres") {
    private var container: PostgreSQLContainer[_] = null
    override def apply(): PostgreSQLContainer[_] = container
    override def beforeAll(): Unit = {
      container = ContainerUtils.postgres.newContainer(USERNAME, PASSWORD, DBNAME, "sql/init_postgresql.sql")
      container.start()
    }
    override def afterAll(): Unit = {
      container.stop()
    }
  }

  val pgDatasource = new Fixture[HikariDataSource]("postgres_datasource") {
    private var dataSource: HikariDataSource = _
    override def apply(): HikariDataSource = dataSource
    override def beforeAll(): Unit = {
      val ip = postgres().getHost
      val port = postgres().getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
      val connect = PostgresConnect(ip, port, DBNAME)
      dataSource = connect.getDatasource(connect.url(), USERNAME, PASSWORD)
    }
    override def afterAll(): Unit = {
      dataSource.close()
    }
  }

  val mysql = new Fixture[MySQLContainer[_]]("mysql") {
    private var container: MySQLContainer[_] = null
    override def apply(): MySQLContainer[_] = container
    override def beforeAll(): Unit = {
      container = ContainerUtils.mysql.newContainer(USERNAME, PASSWORD, DBNAME, "sql/init_mysql.sql")
      container.start()
    }
    override def afterAll(): Unit = {
      container.stop()
    }
  }

  val mysqlDatasource = new Fixture[HikariDataSource]("mysql_datasource") {
    private var dataSource: HikariDataSource = _
    override def apply(): HikariDataSource = dataSource
    override def beforeAll(): Unit = {
      val ip = mysql().getHost
      val port = mysql().getMappedPort(MySQLContainer.MYSQL_PORT)
      val connect = MysqlConnect(ip, port, DBNAME)
      dataSource = connect.getDatasource(connect.url(), USERNAME, PASSWORD)
    }
    override def afterAll(): Unit = {
      dataSource.close()
    }
  }

}
