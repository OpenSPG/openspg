package com.antgroup.openspg.reasoner.lube.block

import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRGraph}

final case class AggregationBlock(
    dependencies: List[Block],
    aggregations: Aggregations,
    group: List[String],
    graph: IRGraph)
    extends BasicBlock[Fields](BlockType("aggregation")) {

  override def binds: Fields = {
    val fields = new ListBuffer[IRField]
    fields ++= dependencies.head.binds.fields
    fields ++= aggregations.fields
    Fields(fields.toList)
  }

}

final case class Aggregations(pairs: Map[IRField, Aggregator]) extends Binds {
  override def fields: List[IRField] = pairs.keySet.toList

}
