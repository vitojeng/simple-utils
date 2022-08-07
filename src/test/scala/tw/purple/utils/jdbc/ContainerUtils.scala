package tw.purple.utils.jdbc

import org.testcontainers.containers.{JdbcDatabaseContainer, MySQLContainer, PostgreSQLContainer}

object ContainerUtils {

  def dbKind(container: JdbcDatabaseContainer[_]): DbKind = {
    val className = container.getDriverClassName
    if (className.contains("postgresql")) POSTGRES
      else if (className.contains("mysql")) MYSQL
      else throw new RuntimeException("Unknown database driver: " + className)
  }

  def createContext(container: JdbcDatabaseContainer[_], dbname: String, username: String, password: String) = {
    val exposedPort = container.getExposedPorts.get(0)
    val dbKind = ContainerUtils.dbKind(container)
    val builder = new JdbcContextBuilder(dbKind)
    builder.url(container.getHost, container.getMappedPort(exposedPort), dbname)
      .dataSource(username, password)
      .build()
  }


  object postgres {
    val POSTGRES_IMAGE_NAME = "postgres:12.9"

    def newContainer(username: String, password: String, databaseName: String
                     , initScript: String=""): PostgreSQLContainer[_] = {
      val instance: PostgreSQLContainer[_] = new PostgreSQLContainer(POSTGRES_IMAGE_NAME)
      instance.withDatabaseName(databaseName)
      instance.withUsername(username)
      instance.withPassword(password)
      if (initScript.nonEmpty)
        instance.withInitScript(initScript)

      instance
    }

    def jdbcUrl(container: JdbcDatabaseContainer[_], dbName: String = ""): String = {
      String.format("jdbc:postgresql://%s:%d/%s",
        container.getHost(),
        container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        dbName)
    }
  }


  object mysql {
    val MYSQL_IMAGE_NAME = "mysql:5.7.34"

    def newContainer(username: String, password: String, databaseName: String
                          , initScript: String=""): MySQLContainer[_] = {
      val instance: MySQLContainer[_] = new MySQLContainer(MYSQL_IMAGE_NAME)
      instance.withDatabaseName(databaseName)
      instance.withUsername(username)
      instance.withPassword(password)
      if (initScript.nonEmpty)
        instance.withInitScript(initScript)

      instance
    }

    def jdbcUrl(container: JdbcDatabaseContainer[_], dbName: String = ""): String = {
        String.format("jdbc:mysql://%s:%d/%s",
          container.getHost(),
          container.getMappedPort(MySQLContainer.MYSQL_PORT),
          dbName)
    }

  }

}
