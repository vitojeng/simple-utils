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

  val pgContext = new Fixture[JdbcContext]("postgres_context") {
    private var context: JdbcContext = _
    override def apply(): JdbcContext = context
    override def beforeAll(): Unit = {
      context = JdbcContext.postgres()
        .url(postgres().getHost, postgres().getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), DbFixtures.DBNAME)
        .dataSource(DbFixtures.USERNAME, DbFixtures.PASSWORD)
        .build()
    }
    override def afterAll(): Unit = {
      context.close()
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

  val mysqlContext = new Fixture[JdbcContext]("mysql_context") {
    private var context: JdbcContext = _
    override def apply(): JdbcContext = context
    override def beforeAll(): Unit = {
      context = JdbcContext.mysql()
        .url(mysql().getHost, mysql().getMappedPort(MySQLContainer.MYSQL_PORT), DbFixtures.DBNAME)
        .dataSource(DbFixtures.USERNAME, DbFixtures.PASSWORD)
        .build()
    }
    override def afterAll(): Unit = {
      context.close()
    }
  }


}
