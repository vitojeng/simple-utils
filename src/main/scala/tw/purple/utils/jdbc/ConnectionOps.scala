package tw.purple.utils.jdbc

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.util.Using

object ConnectionOps {

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
    Using.resource(conn.prepareStatement(sql)) { statement =>
      _setParameters(statement, parameters)
      statement.executeUpdate()
    }
  }

  def query[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T)(implicit conn: Connection): Seq[T] = {
    val statement = conn.prepareStatement(sql)
    Using.resource(statement) { statement =>
      _setParameters(statement, parameters)
      Using.resource(statement.executeQuery()) { rs =>
        _iteratorOf(rs)(f).toSeq
      }
    }
  }

  def firstRow[T](sql: String, parameters: Seq[Any] = Seq.empty)(f: ResultSet => T)(implicit conn: Connection): Option[T] = {
    val statement = conn.prepareStatement(sql)
    Using.resource(statement) { statement =>
      _setParameters(statement, parameters)
      Using.resource(statement.executeQuery()) { rs =>
        if (rs.next()) Some(f(rs)) else None
      }
    }
  }

  def valueOf[T](sql: String, parameters: Seq[Any] = Seq.empty, columnIndex: Int = 1)(implicit conn: Connection): T = {
    firstRow(sql, parameters) { rs =>
      rs.getObject(columnIndex).asInstanceOf[T]
    } match {
      case Some(v) => v
      case None => throw new RuntimeException("Query result set is empty.")
    }
  }

}
