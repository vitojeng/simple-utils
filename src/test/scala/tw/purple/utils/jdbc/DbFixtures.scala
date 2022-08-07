package tw.purple.utils.jdbc

import munit.Fixture
import org.testcontainers.containers.{MySQLContainer, PostgreSQLContainer}

object DbFixtures {
  val USERNAME = "myuser"
  val PASSWORD = "mypass"
  val DBNAME = "test"

  object postgres {
    def newContainer(): Fixture[PostgreSQLContainer[_]] = new Fixture[PostgreSQLContainer[_]]("postgres") {
      private var _container: PostgreSQLContainer[_] = null
      override def apply(): PostgreSQLContainer[_] = _container
      override def beforeAll(): Unit = {
        _container = ContainerUtils.postgres.newContainer(USERNAME, PASSWORD, DBNAME, "sql/init_postgresql.sql")
        _container.start()
      }
      override def afterAll(): Unit = {
        _container.stop()
      }
    }

    def context(container: Fixture[PostgreSQLContainer[_]]): Fixture[JdbcContext] = new Fixture[JdbcContext]("postgres_context") {
      private var _context: JdbcContext = _
      override def apply(): JdbcContext = _context
      override def beforeAll(): Unit = {
        _context = JdbcContext.postgres()
          .url(container().getHost, container().getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), DbFixtures.DBNAME)
          .dataSource(DbFixtures.USERNAME, DbFixtures.PASSWORD)
          .build()
      }

      override def afterAll(): Unit = {
        _context.close()
      }
    }
  }

  object mysql {
    def newContainer(): Fixture[MySQLContainer[_]] = new Fixture[MySQLContainer[_]]("mysql") {
      private var _container: MySQLContainer[_] = null
      override def apply(): MySQLContainer[_] = _container
      override def beforeAll(): Unit = {
        _container = ContainerUtils.mysql.newContainer(USERNAME, PASSWORD, DBNAME, "sql/init_mysql.sql")
        _container.start()
      }
      override def afterAll(): Unit = {
        _container.stop()
      }
    }

    def context(container: Fixture[MySQLContainer[_]]): Fixture[JdbcContext] = new Fixture[JdbcContext]("mysql_context") {
      private var _context: JdbcContext = _
      override def apply(): JdbcContext = _context
      override def beforeAll(): Unit = {
        _context = JdbcContext.mysql()
          .url(container().getHost, container().getMappedPort(MySQLContainer.MYSQL_PORT), DbFixtures.DBNAME)
          .dataSource(DbFixtures.USERNAME, DbFixtures.PASSWORD)
          .build()
      }
      override def afterAll(): Unit = {
        _context.close()
      }
    }
  }


}
