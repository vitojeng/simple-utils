package tw.purple.utils.jdbc

import org.testcontainers.containers.{MySQLContainer, PostgreSQLContainer}
import JdbcOps._
import com.zaxxer.hikari.HikariConfig

import java.io.FileOutputStream
import java.util.Properties
import scala.util.Using

class JdbcContextBuilderSuite extends munit.FunSuite {

  val postgres: Fixture[PostgreSQLContainer[_]] = DbFixtures.postgres.newContainer()

  override def munitFixtures = List(postgres)

  test("build datasource from essential config") {
    val builder = new JdbcContextBuilder(POSTGRES)
    val context = builder.url(postgres().getHost, postgres().getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), DbFixtures.DBNAME)
                          .dataSource(DbFixtures.USERNAME, DbFixtures.PASSWORD)
                          .build()
    val value = context.valueOf[Int]("select 1")
    assertEquals(value, 1)
  }

  test("build datasource from hikari config") {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(postgres().getJdbcUrl)
    hikariConfig.setUsername(DbFixtures.USERNAME)
    hikariConfig.setPassword(DbFixtures.PASSWORD)

    val builder = new JdbcContextBuilder(POSTGRES)
    val context = builder.dataSource(hikariConfig)
                          .build()
    val value = context.valueOf[Int]("select 1")
    assertEquals(value, 1)
  }

  test("build datasource from properties") {
    val props = new Properties()
    props.setProperty("jdbcUrl", postgres().getJdbcUrl)
    props.setProperty("dataSource.user", DbFixtures.USERNAME)
    props.setProperty("dataSource.password", DbFixtures.PASSWORD)
    props.setProperty("dataSource.databaseName", DbFixtures.DBNAME)

    val builder = new JdbcContextBuilder(POSTGRES)
    val context = builder.dataSource(props)
                          .build()
    val value = context.valueOf[Int]("select 1")
    assertEquals(value, 1)
  }

  test("build datasource from properties file") {
    val props = new Properties()
    props.setProperty("jdbcUrl", postgres().getJdbcUrl)
    props.setProperty("dataSource.user", DbFixtures.USERNAME)
    props.setProperty("dataSource.password", DbFixtures.PASSWORD)
    props.setProperty("dataSource.databaseName", DbFixtures.DBNAME)

    val tempFile = java.io.File.createTempFile("postgres-datasource", null)
    Using.resource(new FileOutputStream(tempFile)) { out =>
      props.store(out, null)
    }

    val builder = new JdbcContextBuilder(POSTGRES)
    val context = builder.dataSource(tempFile.getAbsolutePath)
                          .build()
    val value = context.valueOf[Int]("select 1")
    assertEquals(value, 1)

    tempFile.deleteOnExit()
  }

}
