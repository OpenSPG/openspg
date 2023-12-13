package com.antgroup.openspg.reasoner.util

import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  Connection,
  PatternConnection,
  PatternElement
}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

case class PathConnection(
    alias: String,
    source: String,
    relTypes: Set[String],
    target: String,
    direction: Direction,
    rule: Rule,
    vertexSchemaList: List[PatternElement],
    edgeSchemaList: List[PatternConnection])
    extends Connection {

  override def update(source: String, target: String): Connection =
    copy(source = source, target = target)

  override def update(rule: Rule): Connection = copy(rule = rule)

  override def update(direction: Direction): Connection = copy(direction = direction)

  override def limit: Integer = 0

}
