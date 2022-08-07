package tw.purple.utils.jdbc

import org.testcontainers.containers.{MySQLContainer, PostgreSQLContainer}

class ConnectionOpsSuite extends munit.FunSuite {

  val postgres: Fixture[PostgreSQLContainer[_]] = DbFixtures.postgres.newContainer()
  val pgContext: Fixture[JdbcContext] = DbFixtures.postgres.context(postgres)
  val mysql: Fixture[MySQLContainer[_]] = DbFixtures.mysql.newContainer()
  val mysqlContext: Fixture[JdbcContext] = DbFixtures.mysql.context(mysql)

  val dbContextFixtures = List(pgContext, mysqlContext)

  override def munitFixtures =
    List(postgres, mysql) ++ dbContextFixtures

  private def jdbcContexts: List[JdbcContext] = dbContextFixtures.map(_.apply())

  test("query with parameter - Long") {
    jdbcContexts.foreach { dbContext =>
      import JdbcOps._
      val payment: Long = 405000
      val sql = "SELECT name FROM passengers where payment>=?"
      dbContext.connection { implicit conn =>
        val lines: Seq[String] = query(sql, Seq(payment)) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Anna", "Harry"))
      }
    }
  }

  test("query with parameter - Int") {
    jdbcContexts.foreach { dbContext =>
      import JdbcOps._
      val sql = "SELECT name FROM passengers where id>=?"
      val id: Int = 3
      dbContext.connection { implicit conn =>
        val lines: Seq[String] = query(sql, Seq(id)) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Wonder", "Stacy", "Stevie", "Harry"))
      }
    }
  }

  test("query with parameter - Boolean") {
    jdbcContexts.foreach { dbContext =>
      import JdbcOps._
      val sql = "SELECT name FROM passengers where active=?"
      val active: Boolean = false
      dbContext.connection { implicit conn =>
        val lines: Seq[String] = query(sql, Seq(active)) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Wonder", "Stevie", "Harry"))
      }
    }
  }

  test("update with parameters") {
    jdbcContexts.foreach { dbContext =>
      import JdbcOps._
      val sql = "update passengers set name='NewName' where id>=?"
      val count = dbContext.connection { implicit conn =>
        update(sql, Seq(5))
      }
      assertEquals(count, 2)
    }
  }


  test("query first row") {
    jdbcContexts.foreach { dbContext =>
      import JdbcOps._
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
      import JdbcOps._
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
