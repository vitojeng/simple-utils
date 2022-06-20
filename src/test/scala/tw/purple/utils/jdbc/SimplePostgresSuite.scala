package tw.purple.utils.jdbc

import org.testcontainers.containers.PostgreSQLContainer

import scala.util.Using
import java.sql.DriverManager

class SimplePostgresSuite extends munit.FunSuite {

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
  }

  test("init script") {
    val user = "postgres"
    val pass = "123"
    val dbName = "test"
    val sql = "SELECT foo FROM bar"
    var firstColumnValue = ""
    Using.Manager { use =>
      val postgres: PostgreSQLContainer[_] =
        use(PostgresUtils.newContainer(user, pass, dbName, "sql/init_postgresql.sql"))
      postgres.start()
      val url = PostgresUtils.jdbcUrl(postgres, dbName)
      val conn = use(DriverManager.getConnection(url, user, pass))
      val statement = use(conn.createStatement())
      statement.execute(sql)
      val rs = use(statement.getResultSet)
      while (rs.next()) {
        firstColumnValue = rs.getString(1)
      }
    }
    assertEquals(firstColumnValue, "hello world", "Value from init script should equal real value")
  }

}
