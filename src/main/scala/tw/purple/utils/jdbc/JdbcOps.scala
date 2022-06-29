package tw.purple.utils.jdbc

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.util.Using

object JdbcOps {

  implicit class JdbcContextOps(private val ctx: JdbcContext) extends AnyVal {
    def query[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T): Seq[T] = {
      ctx.connection[Seq[T]] { conn =>
        JdbcOps.query[T](sql, parameters)(f)(conn)
      }
    }
    def update(sql: String, parameters: Seq[Any] = Seq.empty): Int = {
      ctx.connection[Int] { conn =>
        JdbcOps.update(sql, parameters)(conn)
      }
    }

    def firstRow[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T): Option[T] = {
      ctx.connection[Option[T]] { conn =>
        JdbcOps.firstRow[T](sql, parameters)(f)(conn)
      }
    }

    def valueOf[T](sql: String, parameters: Seq[Any] = Seq.empty, columnIndex: Int = 1): T = {
      ctx.connection[T] { conn =>
        JdbcOps.valueOf[T](sql, parameters, columnIndex)(conn)
      }
    }
  }

  private def _iteratorOf[T](resultSet: ResultSet)(f: ResultSet => T): Iterator[T] = {
    new Iterator[T] {
      def hasNext: Boolean = resultSet.next()

      def next(): T = f(resultSet)
    }
  }

  private def _setParameters(statement: PreparedStatement, parameters: Seq[Any]): Unit = {
    statement.clearParameters()
    if (parameters.nonEmpty) {
      parameters.zipWithIndex.foreach { case (value, i) =>
        val parameterIndex = i + 1
        value match {
          case v: String => statement.setString(parameterIndex, v)
          case v: Long => statement.setLong(parameterIndex, v)
          case v: Int => statement.setInt(parameterIndex, v)
          case v: Boolean => statement.setBoolean(parameterIndex, v)
          case null => statement.setObject(parameterIndex, null)
          case v => throw new RuntimeException("The query parameter type not supported: " + v.getClass.toString)
        }
      }
    }
  }

  def update(sql: String, parameters: Seq[Any] = Seq.empty)(implicit conn: Connection): Int = {
    Using.resource[PreparedStatement, Int](conn.prepareStatement(sql)) { statement =>
      _setParameters(statement, parameters)
      statement.executeUpdate()
    }
  }

  def query[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T)(implicit conn: Connection): Seq[T] = {
    val statement = conn.prepareStatement(sql)
    Using.resource[PreparedStatement, Seq[T]](statement) { statement =>
      _setParameters(statement, parameters)
      Using.resource[ResultSet, Seq[T]](statement.executeQuery()) { rs =>
        _iteratorOf(rs)(f).toSeq
      }
    }
  }

  def firstRow[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T)(implicit conn: Connection): Option[T] = {
    val statement = conn.prepareStatement(sql)
    Using.resource[PreparedStatement, Option[T]](statement) { statement =>
      _setParameters(statement, parameters)
      Using.resource[ResultSet, Option[T]](statement.executeQuery()) { rs =>
        if (rs.next()) Some(f(rs)) else None
      }
    }
  }

  def valueOf[T](sql: String, parameters: Seq[Any] = Seq.empty, columnIndex: Int = 1)(implicit conn: Connection): T = {
    firstRow[T](sql, parameters) { rs =>
      rs.getObject(columnIndex).asInstanceOf[T]
    } match {
      case Some(v) => v
      case None => throw new RuntimeException("Query result set is empty.")
    }
  }

}
