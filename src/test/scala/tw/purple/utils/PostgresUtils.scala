package tw.purple.utils

import org.testcontainers.containers.{JdbcDatabaseContainer, PostgreSQLContainer}

object PostgresUtils {

  val POSTGRES_IMAGE_NAME = "postgres:12.9"

  def newContainer(username: String, password: String, databaseName: String
                   , initScript: String=""): PostgreSQLContainer[_] = {
    val instance: PostgreSQLContainer[_] = new PostgreSQLContainer(POSTGRES_IMAGE_NAME)
    instance.withDatabaseName(databaseName)
    instance.withUsername(username)
    instance.withPassword(password)
    if (!initScript.isBlank)
      instance.withInitScript(initScript)

    instance
  }

  def jdbcUrl(container: JdbcDatabaseContainer[_], dbName: String = ""): String = {
    val url = String.format("jdbc:postgresql://%s:%d/%s",
      container.getHost(),
      container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
      dbName)
    url
  }

}
