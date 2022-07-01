package tw.purple.utils.jdbc


import org.testcontainers.containers.{MySQLContainer, PostgreSQLContainer}
import tw.purple.utils.jdbc.JdbcOps._

class JdbcContextSuite extends munit.FunSuite {

  val postgres: Fixture[PostgreSQLContainer[_]] = DbFixtures.postgres.container
  val pgContext: Fixture[JdbcContext] = DbFixtures.postgres.context
  val mysql: Fixture[MySQLContainer[_]] = DbFixtures.mysql.container
  val mysqlContext: Fixture[JdbcContext] = DbFixtures.mysql.context

  val dbContextFixtures = List(pgContext, mysqlContext)

  override def munitFixtures =
    List(postgres, mysql) ++ dbContextFixtures

  private def jdbcContexts: List[JdbcContext] = dbContextFixtures.map(_.apply())

  test("query with parameter") {
    jdbcContexts.foreach { dbContext =>
      val payment: Long = 405000
      val sql = "SELECT name FROM passengers where payment>=?"

      val lines: Seq[String] = dbContext.query(sql, Seq(payment)) { rs =>
        rs.getString(1)
      }
      assertEquals(lines, Seq("Anna", "Harry"))
    }
  }

  test("update with parameters") {
    jdbcContexts.foreach { dbContext =>
      val sql = "update passengers set name='NewName' where id>=?"
      val count = dbContext.update(sql, Seq(5))
      assertEquals(count, 2)
    }
  }


  test("query first row") {
    jdbcContexts.foreach { dbContext =>
      val sql = "SELECT id, name, email FROM passengers where id>=?"
      val row1 = dbContext.firstRow(sql, Seq(3)) { rs =>
          (rs.getInt("id"), rs.getString("name"), rs.getString("email"))
      }
      assertEquals(row1.get._1, 3)
      assertEquals(row1.get._2, "Wonder")
    }
  }

  test("query first row - empty") {
    jdbcContexts.foreach { dbContext =>
      val sql = "SELECT id, name, email FROM passengers where id>=?"
      val row = dbContext.firstRow(sql, Seq(300)) { rs =>
        (rs.getInt("id"), rs.getString("name"), rs.getString("email"))
      }
      assertEquals(row.isEmpty, true)
    }

  }

  test("query the value") {
    jdbcContexts.foreach { dbContext =>
      val sql = "SELECT id, name, email, active FROM passengers where id=?"
      val id: Int = dbContext.valueOf(sql, Seq(3))
      val name: String = dbContext.valueOf(sql, Seq(3), 2)
      val email = dbContext.valueOf[String](sql, Seq(3), 3)
      val active = dbContext.valueOf[Boolean](sql, Seq(3), 4)
      assertEquals(id, 3)
      assertEquals(name, "Wonder")
      assertEquals(email, "wonder2@yahoo.com")
      assertEquals(active, false)
    }

  }

}
