package com.antgroup.openspg.reasoner.lube.common.expr

import com.antgroup.openspg.reasoner.common.trees.AbstractTreeNode
import com.antgroup.openspg.reasoner.common.types.KgType

/**
 * Computed Expressions in Rules
 */
sealed abstract class Expr extends AbstractTreeNode[Expr] {}

final case class OpChainExpr(curExpr: Expr, preChainExpr: OpChainExpr) extends Expr {}
/**
 * Strongly Typed Computational Expressions
 */
sealed trait TypeValidatedExpr extends Expr {}

case object Directly extends Expr

/**
 * Aggregation operator in calculation expression
 */
sealed trait Aggregator extends TypeValidatedExpr {}

/**
 * list object operator set
 */
sealed trait ListOpSet

/**
 * aggregator operator set
 */
sealed trait AggregatorOpSet

/**
 * Constant, such as '123', 'abcd'
 */
sealed trait VConstant extends Expr

/**
 * String-to-basic-type conversion operators
 */
case object VNull extends Expr

final case class VString(value: String) extends VConstant
final case class VLong(value: String) extends VConstant
final case class VDouble(value: String) extends VConstant
final case class VBoolean(value: String) extends VConstant

/**
 * list-to-basic-type-list conversion operators
 * @param list
 * @param listType
 */
final case class VList(list: List[String], listType: KgType) extends Expr {}

/**
 * aggregator operator set
 */
case object Max extends AggregatorOpSet {}
case object Min extends AggregatorOpSet {}
case object Sum extends AggregatorOpSet {}
case object Avg extends AggregatorOpSet {}
case object Count extends AggregatorOpSet {}

case object First extends AggregatorOpSet

final case class AggUdf(name: String, funcArgs: List[Expr])
  extends AggregatorOpSet {}

final case class StrJoin(tok: String) extends AggregatorOpSet {}
final case class Accumulate(op: String) extends AggregatorOpSet {}

final case class Get(index: Integer) extends ListOpSet {}

final case class Limit(column: Expr, num: Integer) extends TypeValidatedExpr {}


/**
 * list object operator set
 */
sealed trait OrderOpSet
case object DescExpr extends OrderOpSet {}
case object AscExpr extends OrderOpSet {}

final case class OrderAndLimit(order: OrderOpSet, limit: Limit) extends TypeValidatedExpr {}

final case class Slice(start: Integer, end: Integer) extends ListOpSet {}

final case class Filter(condition: Expr) extends TypeValidatedExpr {}
/**
 * list operator expr paradigm
 * @param name
 * @param opInput
 */
final case class ListOpExpr(name: ListOpSet, opInput: Ref) extends TypeValidatedExpr {}



/**
 * aggregator operator expr paradigm
 *
 * @param name
 * @param aggEleExpr
 */
case class AggOpExpr(name: AggregatorOpSet, aggEleExpr: Expr)
  extends Aggregator {}

/**
 * aggregator operator expr paradigm with condition
 * @param name
 * @param aggEleExpr
 */
final case class AggIfOpExpr(aggOpExpr: AggOpExpr,
                             condition: Expr) extends Aggregator {}


/**
 *  This operator retrieves the multi-version data for a given property
 *  within a specified interval and returns a list of the retrieved data.
 * @param start start date expr of the interval
 * @param end end date expr of the interval
 */
final case class Window(start: Expr, end: Expr) extends TypeValidatedExpr {}

/**
 * graph aggregation operator expr paradigm
 * @param pathName
 * @param by
 * @param expr
 */
final case class GraphAggregatorExpr(pathName: String, by: List[Ref], expr: Aggregator)
    extends Aggregator {}

/**
 * function operator expr paradigm
 * @param name
 * @param funcArgs
 */
final case class FunctionExpr(name: String, funcArgs: List[Expr]) extends TypeValidatedExpr {}

/**
 * unary operator set
 */
sealed trait UnaryOpSet

/**
 * The following operators are unary operators
 */
case object Not extends UnaryOpSet
case object Neg extends UnaryOpSet
case object Exists extends UnaryOpSet
case object Abs extends UnaryOpSet
case object Floor extends UnaryOpSet
case object Ceil extends UnaryOpSet
case object VSome extends UnaryOpSet
case class GetField(fieldName: String) extends UnaryOpSet
case class Cast(castType: String) extends UnaryOpSet

/**
 * unary operator expr paradigm
 * @param name
 * @param arg
 */
final case class UnaryOpExpr(name: UnaryOpSet, arg: Expr) extends TypeValidatedExpr {}

/**
 * binary operator set
 */
sealed trait BinaryOpSet

/**
 * The following operators are binary operators
 */
case object BAdd extends BinaryOpSet
case object BSub extends BinaryOpSet
case object BMul extends BinaryOpSet
case object BDiv extends BinaryOpSet
case object BAnd extends BinaryOpSet
case object BAssign extends BinaryOpSet
case object BEqual extends BinaryOpSet
case object BNotEqual extends BinaryOpSet
case object BGreaterThan extends BinaryOpSet
case object BNotGreaterThan extends BinaryOpSet
case object BSmallerThan extends BinaryOpSet
case object BNotSmallerThan extends BinaryOpSet
case object BOr extends BinaryOpSet
case object BXor extends BinaryOpSet
case object BIn extends BinaryOpSet
case object BLike extends BinaryOpSet
case object BRLike extends BinaryOpSet
case object BIndex extends BinaryOpSet
case object BMod extends BinaryOpSet

/**
 * binary operator expr paradigm
 * @param name
 * @param l
 * @param r
 */
final case class BinaryOpExpr(name: BinaryOpSet, l: Expr, r: Expr) extends TypeValidatedExpr {}

/**
 * Get variable value by variable name
 * @param refName
 */
final case class Ref(refName: String) extends TypeValidatedExpr {}

/**
 * Get parameter script by param name
 * @param paramName
 */
final case class Parameter(paramName: String) extends TypeValidatedExpr {}

