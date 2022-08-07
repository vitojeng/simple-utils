package tw.purple.utils.jdbc

import org.testcontainers.containers.{JdbcDatabaseContainer, MySQLContainer, PostgreSQLContainer}
import JdbcOps._
import com.zaxxer.hikari.HikariConfig

import java.io.FileOutputStream
import java.util.Properties
import scala.util.Using

class JdbcContextBuilderSuite extends munit.FunSuite {

  private val postgres: Fixture[PostgreSQLContainer[_]] = DbFixtures.postgres.newContainer()
  private val mysql: Fixture[MySQLContainer[_]] = DbFixtures.mysql.newContainer()

  override def munitFixtures = List(postgres, mysql)

  private def containers: List[JdbcDatabaseContainer[_]] = List(postgres(), mysql())

  private def createContext(container: JdbcDatabaseContainer[_]) =
    ContainerUtils.createContext(container, DbFixtures.DBNAME, DbFixtures.USERNAME, DbFixtures.PASSWORD)

  test("build datasource from essential config") {
    containers.foreach { container =>
      val context = createContext(container)
      // Different drive return different type
      val value: Int = ContainerUtils.dbKind(container) match {
        case POSTGRES => context.valueOf[Int]("select 1")
        case MYSQL => context.valueOf[Long]("select 1").toInt
      }
      assertEquals(value, 1)
    }
  }

  test("build datasource from hikari config") {
    containers.foreach { container =>
      val hikariConfig = new HikariConfig()
      hikariConfig.setJdbcUrl(container.getJdbcUrl)
      hikariConfig.setUsername(DbFixtures.USERNAME)
      hikariConfig.setPassword(DbFixtures.PASSWORD)

      val dbKind = ContainerUtils.dbKind(container)
      val builder = new JdbcContextBuilder(dbKind)
      val context = builder.dataSource(hikariConfig).build()
      // Different drive return different type
      val value: Int = ContainerUtils.dbKind(container) match {
        case POSTGRES => context.valueOf[Int]("select 1")
        case MYSQL => context.valueOf[Long]("select 1").toInt
      }
      assertEquals(value, 1)
    }
  }

  test("build datasource from properties") {
    containers.foreach { container =>
      val props = new Properties()
      props.setProperty("jdbcUrl", container.getJdbcUrl)
      props.setProperty("dataSource.user", DbFixtures.USERNAME)
      props.setProperty("dataSource.password", DbFixtures.PASSWORD)
      props.setProperty("dataSource.databaseName", DbFixtures.DBNAME)

      val dbKind = ContainerUtils.dbKind(container)
      val builder = new JdbcContextBuilder(dbKind)
      val context = builder.dataSource(props).build()
      // Different drive return different type
      val value: Int = ContainerUtils.dbKind(container) match {
        case POSTGRES => context.valueOf[Int]("select 1")
        case MYSQL => context.valueOf[Long]("select 1").toInt
      }
      assertEquals(value, 1)
    }
  }

  test("build datasource from properties file") {
    containers.foreach { container =>
      val props = new Properties()
      props.setProperty("jdbcUrl", container.getJdbcUrl)
      props.setProperty("dataSource.user", DbFixtures.USERNAME)
      props.setProperty("dataSource.password", DbFixtures.PASSWORD)
      props.setProperty("dataSource.databaseName", DbFixtures.DBNAME)

      val tempFile = java.io.File.createTempFile("simple-datasource", null)
      Using.resource(new FileOutputStream(tempFile)) { out =>
        props.store(out, null)
      }

      val dbKind = ContainerUtils.dbKind(container)
      val builder = new JdbcContextBuilder(dbKind)
      val context = builder.dataSource(tempFile.getAbsolutePath).build()
      // Different drive return different type
      val value: Int = ContainerUtils.dbKind(container) match {
        case POSTGRES => context.valueOf[Int]("select 1")
        case MYSQL => context.valueOf[Long]("select 1").toInt
      }
      assertEquals(value, 1)

      tempFile.deleteOnExit()
    }
  }

}
