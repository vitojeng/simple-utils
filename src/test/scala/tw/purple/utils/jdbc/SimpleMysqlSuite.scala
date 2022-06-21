package tw.purple.utils.jdbc

import org.testcontainers.containers.{MySQLContainer}

import java.sql.DriverManager
import scala.util.Using

class SimpleMysqlSuite extends munit.FunSuite {

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
      val mysql: MySQLContainer[_] =
        use(ContainerUtils.mysql.newContainer(user, pass, dbName, "sql/init_mysql.sql"))
      mysql.start()
      val url = ContainerUtils.mysql.jdbcUrl(mysql, dbName)
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
