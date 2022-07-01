package tw.purple.utils.jdbc

import org.testcontainers.containers.PostgreSQLContainer
import JdbcOps._
import com.zaxxer.hikari.HikariConfig

import java.util.Properties

class JdbcContextBuilderSuite extends munit.FunSuite {

  val postgres: Fixture[PostgreSQLContainer[_]] = DbFixtures.postgres.container

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
    val hikariConfig = new HikariConfig(props)

    val builder = new JdbcContextBuilder(POSTGRES)
    val context = builder.dataSource(hikariConfig)
                          .build()
    val value = context.valueOf[Int]("select 1")
    assertEquals(value, 1)
  }

}
