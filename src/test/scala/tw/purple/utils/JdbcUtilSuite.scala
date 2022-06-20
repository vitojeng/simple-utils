package tw.purple.utils

import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.PostgreSQLContainer
import tw.purple.utils.JdbcUtils._
import tw.purple.utils.JdbcUtils.ConnectionImports._

import scala.util.Using

class JdbcUtilSuite extends munit.FunSuite {

  val dbName = "test"
  val container = PostgresUtils.newContainer("postgres", "123", dbName, "sql/init_postgresql.sql")
  var ip: String = ""
  var port: Int = 0
  var dataSource: HikariDataSource = _
  var jdbc: JdbcConnect = _
  var url: String = _

  override def beforeAll(): Unit = {
    container.start()
    ip = container.getHost
    port = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
    jdbc = JdbcConnect(POSTGRES, ip, port, dbName)
    url = jdbc.url()
    println(url)
    dataSource = jdbc.getDatasource(url, "postgres", "123")
  }

  override def afterAll(): Unit = {
    dataSource.close()
    container.stop()
  }


  override def beforeEach(context: BeforeEach): Unit = {
  }

  override def afterEach(context: AfterEach): Unit = {
  }

  test("connection from DriverManager") {
    val sql = "SELECT name FROM passengers"
    jdbc.connection(url, "postgres", "123") { implicit conn =>
      val lines: Seq[String] = query(sql) { rs =>
        rs.getString(1)
      }
      assertEquals(lines, Seq("Jack", "Anna", "Wonder", "Stacy", "Stevie", "Harry"))
    }
  }

  test("query with parameter") {
    val sql = "SELECT name FROM passengers where id>=?"
    dataSource.connection { implicit conn =>
      val lines: Seq[String] = query(sql, Seq(3)) { rs =>
        rs.getString(1)
      }
      assertEquals(lines, Seq("Wonder", "Stacy", "Stevie", "Harry"))
    }
  }

  test("update with parameters") {
    val count = dataSource.connection { implicit conn =>
      update("update passengers set name='NewName' where id>=?", Seq(5))
    }
    assertEquals(count, 2)
  }


}