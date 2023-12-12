package com.antgroup.openspg.reasoner.lube.block

import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.types.KgType
import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.pattern.{Element, PatternElement, PredicateElement}

/**
 * every operator block tree of root is result block
 */
sealed trait ResultBlock extends Block {}

/**
 * output as table
 * @param dependencies
 * @param selectList
 * @param graph
 */
final case class TableResultBlock(
    dependencies: List[Block],
    selectList: OrderedFields,
    asList: List[String],
    graph: IRGraph)
    extends ResultBlock {

  /**
   * The metadata output by the current block
   *
   * @return
   */
  override def binds: Binds = selectList
}

/**
 * output as graph
 * @param dependencies
 * @param outputGraphPath the path name array for output
 * @param graph
 */
final case class GraphResultBlock(
    dependencies: List[Block],
    outputGraphPath: List[String],
    graph: IRGraph)
    extends ResultBlock {
  override val binds: Binds = dependencies.head.binds
}

/**
 * DDL operator set
 */
sealed trait DDLOp

/**
 * like "(A:label)-[p:property_name]->(V:String)",will convert to add property operator
 * @param s
 * @param propertyName
 * @param propertyType
 */
case class AddProperty(s: Element, propertyName: String, propertyType: KgType) extends DDLOp

/**
 * add vertex in graph state.
 *
 * @param s
 * @param props
 */
case class AddVertex(s: PatternElement, props: Map[String, Expr]) extends DDLOp

/**
 * like "(A:label)-[p:belongTo]->(B:Concept)",will convert to add predicate operator
 * @param predicate
 */
case class AddPredicate(predicate: PredicateElement) extends DDLOp

/**
 * output is add a property or add a predicate instance
 * @param ddlOp
 * @param dependencies
 * @param graph
 */
case class DDLBlock(ddlOp: Set[DDLOp], dependencies: List[Block], graph: IRGraph)
    extends ResultBlock {

  /**
   * The metadata output by the current block
   *
   * @return
   */
  override def binds: Binds = {
    val fields = dependencies.head.binds.fields
    ddlOp.head match {
      case AddProperty(s, propertyType, _) =>
        val field = fields.find(f => f.name.equals(s.alias)).get
        if (field.isInstanceOf[IRNode]) {
          field.asInstanceOf[IRNode].fields.add(propertyType)
        } else if (field.isInstanceOf[IREdge]) {
          field.asInstanceOf[IREdge].fields.add(propertyType)
        }
        Fields(fields)
      case AddPredicate(predicate) =>
        val newFields = new ListBuffer[IRField]
        newFields.++=(dependencies.head.binds.fields)
        newFields.+=(IREdge(predicate.alias, null))
        Fields(newFields.toList)
      case other =>
        throw UnsupportedOperationException(s"$other ddlop unsupported")
    }
  }

}

final case class OrderedFields(orderedFields: List[IRField] = List.empty) extends Binds {
  override def fields: List[IRField] = orderedFields

}

object OrderedFields {
  def fieldsFrom[E](fields: IRField*): OrderedFields = OrderedFields(fields.toList)

  def unapplySeq(arg: OrderedFields): Option[Seq[IRField]] = Some(arg.orderedFields)
}
