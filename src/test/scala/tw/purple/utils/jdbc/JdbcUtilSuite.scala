package tw.purple.utils.jdbc

import org.testcontainers.containers.{MySQLContainer, PostgreSQLContainer}
import tw.purple.utils.jdbc.JdbcUtils.ConnectionImports._
import tw.purple.utils.jdbc.JdbcUtils._

import javax.sql.DataSource


class JdbcUtilSuite extends munit.FunSuite {

  val postgres = DbFixtures.postgres
  val pgDatasource = DbFixtures.pgDatasource
  val mysql = DbFixtures.mysql
  val mysqlDataSource = DbFixtures.mysqlDatasource

  val dataSourceFixtures = List(pgDatasource, mysqlDataSource)

  override def munitFixtures =
    List(postgres, mysql) ++ dataSourceFixtures


  private def postgresCtx = {
    JdbcContext.postgres(postgres().getHost, postgres().getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), DbFixtures.DBNAME)
  }

  private def mysqlCtx = {
    JdbcContext.mysql(mysql().getHost, mysql().getMappedPort(MySQLContainer.MYSQL_PORT), DbFixtures.DBNAME)
  }

  test("connection from DriverManager") {
    val sql = "SELECT name FROM passengers"
    Seq(postgresCtx, mysqlCtx).foreach { ctx =>
      ctx.connection(ctx.url(), DbFixtures.USERNAME, DbFixtures.PASSWORD) { implicit conn =>
        val lines: Seq[String] = query(sql) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Jack", "Anna", "Wonder", "Stacy", "Stevie", "Harry"))
      }
    }
  }

  private def eachDataSource(body: DataSource => Unit): Unit = {
    dataSourceFixtures.foreach { fixture =>
      println(s"Fixture: ${fixture.fixtureName}")
      body(fixture())
    }
  }


  test("query with parameter") {
    eachDataSource { dataSource =>
      val sql = "SELECT name FROM passengers where id>=?"
      dataSource.connection { implicit conn =>
        val lines: Seq[String] = query(sql, Seq(3)) { rs =>
          rs.getString(1)
        }
        assertEquals(lines, Seq("Wonder", "Stacy", "Stevie", "Harry"))
      }
    }
  }

  test("update with parameters") {
    eachDataSource { dataSource =>
      val count = dataSource.connection { implicit conn =>
        update("update passengers set name='NewName' where id>=?", Seq(5))
      }
      assertEquals(count, 2)
    }
  }

  test("query first row") {
    eachDataSource { dataSource =>
      val sql = "SELECT id, name, email FROM passengers where id>=?"
      val row1 = dataSource.connection { implicit conn =>
        firstRow(sql, Seq(3)) { rs =>
          (rs.getInt("id"), rs.getString("name"), rs.getString("email"))
        }
      }
      assertEquals(row1.get._1, 3)
      assertEquals(row1.get._2, "Wonder")
    }
  }

  test("query first row - empty") {
    eachDataSource { dataSource =>
      val sql = "SELECT id, name, email FROM passengers where id>=?"
      val row = dataSource.connection { implicit conn =>
        firstRow(sql, Seq(300)) { rs =>
          (rs.getInt("id"), rs.getString("name"), rs.getString("email"))
        }
      }
      assertEquals(row.isEmpty, true)
    }

  }

}
