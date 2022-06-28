package tw.purple.utils.jdbc

import tw.purple.utils.jdbc.JdbcUtils.ConnectionImports._


class JdbcUtilSuite extends munit.FunSuite {

  val postgres = DbFixtures.postgres
  val pgContext: Fixture[JdbcContext] = DbFixtures.pgContext
  val mysql = DbFixtures.mysql
  val mysqlContext: Fixture[JdbcContext] = DbFixtures.mysqlContext

  val dbContextFixtures = List(pgContext, mysqlContext)

  override def munitFixtures =
    List(postgres, mysql) ++ dbContextFixtures

  private def jdbcContexts: List[JdbcContext] = dbContextFixtures.map(_.apply())

  test("connection from DriverManager") {
    val sql = "SELECT name FROM passengers"
    jdbcContexts.foreach { ctx =>
      ctx.connection { implicit conn =>
        val lines: Seq[String] = query(sql) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Jack", "Anna", "Wonder", "Stacy", "Stevie", "Harry"))
      }
    }
  }

  test("query with parameter") {
    jdbcContexts.foreach { dbContext =>
      val sql = "SELECT name FROM passengers where id>=?"
      dbContext.connection { implicit conn =>
        val lines: Seq[String] = query(sql, Seq(3)) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Wonder", "Stacy", "Stevie", "Harry"))
      }
    }
  }

  test("update with parameters") {
    jdbcContexts.foreach { dbContext =>
      val count = dbContext.connection { implicit conn =>
        update("update passengers set name='NewName' where id>=?", Seq(5))
      }
      assertEquals(count, 2)
    }
  }


  test("query first row") {
    jdbcContexts.foreach { dbContext =>
      val sql = "SELECT id, name, email FROM passengers where id>=?"
      val row1 = dbContext.connection { implicit conn =>
        firstRow(sql, Seq(3)) { rs =>
          (rs.getInt("id"), rs.getString("name"), rs.getString("email"))
        }
      }
      assertEquals(row1.get._1, 3)
      assertEquals(row1.get._2, "Wonder")
    }
  }

  test("query first row - empty") {
    jdbcContexts.foreach { dbContext =>
      val sql = "SELECT id, name, email FROM passengers where id>=?"
      val row = dbContext.connection { implicit conn =>
        firstRow(sql, Seq(300)) { rs =>
          (rs.getInt("id"), rs.getString("name"), rs.getString("email"))
        }
      }
      assertEquals(row.isEmpty, true)
    }

  }

}
