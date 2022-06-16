package tw.purple.utils

import org.testcontainers.containers.PostgreSQLContainer
import tw.purple.utils.JdbcUtils._

import scala.util.Using

class JdbcUtilSuite extends munit.FunSuite {

  val dbName = "test"
  val container = PostgresUtils.newContainer("postgres", "123", dbName, "sql/init_postgresql.sql")
  var ip: String = ""
  var port: Int = 0

  override def beforeAll(): Unit = {
    container.start()
    ip = container.getHost
    port = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
  }

  override def afterAll(): Unit = {
    container.stop()
  }

  var jdbc: JdbcConnect = _
  var url: String = _

  override def beforeEach(context: BeforeEach): Unit = {
    jdbc = JdbcConnect(POSTGRES, ip, port, dbName)
    url = jdbc.url()
    println(url)
  }

  override def afterEach(context: AfterEach): Unit = {
  }

  test("connection - query") {
    val sql = "SELECT name FROM passengers"
    Using.resource(jdbc.connection(url, "postgres", "123")) { conn =>
      val lines: Seq[String] = conn.query(sql) { rs =>
        rs.getString(1)
      }
      assertEquals(lines, Seq("Jack", "Anna", "Wonder", "Stacy", "Stevie", "Harry"))
    }
  }

  test("query with parameter") {
    val sql = "SELECT name FROM passengers where id>=?"
    Using.Manager { use =>
      val ds = use(jdbc.datasource(url, "postgres", "123"))
      val conn = use(ds.getConnection)
      val lines: Seq[String] = conn.query(sql, Seq(3)) { rs =>
        rs.getString(1)
      }
      assertEquals(lines, Seq("Wonder", "Stacy", "Stevie", "Harry"))
    }
  }

  test("connection - update") {
    Using.Manager { use =>
      val ds = use(jdbc.datasource(url, "postgres", "123"))
      val conn = use(ds.getConnection)
      val count = conn.update("update passengers set name='NewName' where id>=?", Seq(5))
      assertEquals(count, 2)
    }
  }


}
