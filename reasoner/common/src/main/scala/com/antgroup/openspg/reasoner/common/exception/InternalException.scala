package com.antgroup.openspg.reasoner.common.exception

import scala.compat.Platform.EOL

/**
 * Internal exceptions of KGReasoner
 */
abstract class InternalException(msg: String, cause: Throwable = null)
    extends RuntimeException(msg, cause)
    with Serializable

final case class SchemaException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class KGValueException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class NotImplementedException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class IllegalStateException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class IllegalArgumentException(
    expected: Any,
    actual: Any = "none",
    explanation: String = "",
    cause: Throwable = null)
    extends InternalException(
      s"""
       |${if (explanation.nonEmpty) s"Explanation:$EOL\t$explanation$EOL" else ""}
       |Expected:
       |\t$expected
       |Found:
       |\t$actual""".stripMargin,
      cause)

final case class UnsupportedOperationException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class NoSuitableSignatureForExpr(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class GraphNotFoundException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class InvalidGraphException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class GraphAlreadyExistsException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class ViewAlreadyExistsException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class ConnectionNotFoundException(msg: String, cause: Throwable = null)
  extends InternalException(msg, cause)

final case class NotDefineException(msg: String, cause: Throwable = null)
  extends InternalException(msg, cause)

final case class UdfExistsException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class InvalidRefVariable(msg: String, cause: Throwable = null)
  extends InternalException(msg, cause)

final case class KGDSLOneTaskException(msg: String, cause: Throwable = null)
  extends InternalException(msg, cause)

final class KGDSLGrammarException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

case class KGDSLInvalidTokenException(input: String, line: Int, col: Int,
                                      errMsg: String = "", cause: Throwable = null)
    extends InternalException(
      f"""line: $line, column: $col, input:
         |$input
         |${" " * col}^ error here
         |${errMsg}""".stripMargin,
      cause)

/**
 * Unrecoverable system error
 */
final case class SystemError(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class OdpsException(msg: String, cause: Throwable = null)
    extends InternalException(msg, cause)

final case class HiveException(msg: String, cause: Throwable = null)
  extends InternalException(msg, cause)
