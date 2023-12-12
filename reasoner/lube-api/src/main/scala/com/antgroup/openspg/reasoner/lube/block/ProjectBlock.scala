package com.antgroup.openspg.reasoner.lube.block

import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRGraph, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

/**
 * a project block，to convert variable to another form
 * @param dependencies
 * @param projects project expression
 * @param graph
 */
final case class ProjectBlock(
    dependencies: List[Block],
    projects: ProjectFields = ProjectFields(),
    graph: IRGraph)
    extends BasicBlock[Fields](BlockType("project")) {

  override def binds: Fields = {
    val fields = new ListBuffer[IRField]
    fields ++= dependencies.head.binds.fields
    fields ++= this.projects.fields
    Fields(fields.toList)
  }

}

/**
 * to explain how to convert variable to another form by express
 * @param items
 */
final case class ProjectFields(items: Map[IRField, Rule] = Map.empty) extends Binds {
  override def fields: List[IRField] = items.keySet.toList
}
