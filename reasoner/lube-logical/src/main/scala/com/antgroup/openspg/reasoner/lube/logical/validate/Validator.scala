/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.lube.logical.validate

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.{SchemaException, UnsupportedOperationException}
import com.antgroup.openspg.reasoner.lube.Logging
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.catalog._
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRField, IRNode, IRRepeatPath}
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.semantic.SemanticExplainer
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface
import com.antgroup.openspg.reasoner.lube.utils.BlockUtils

object Validator extends Logging {

  def validate(blocks: List[Block])(implicit context: LogicalPlannerContext): Dag[Block] = {
    val graph = context.catalog.getGraph(Catalog.defaultGraphName)
    val dag = new Dag[Block]()
    for (block <- blocks) {
      val blockName = BlockUtils.getDefine(block).head
      val finalBlock = SemanticExplainer.explain(block)
      dag.addNode(blockName, finalBlock)
      val dependencies = needResolved(resolve(finalBlock), graph)
      dependencies.foreach(dag.addEdge(blockName, _))
    }
    dag.order()
    dag
  }

  def validate(parser: ParserInterface, input: Block)(implicit
      context: LogicalPlannerContext): Dag[Block] = {
    val dag = new Dag[Block]()
    val graph = context.catalog.getGraph(Catalog.defaultGraphName)
    val rootName = "result"
    val block = SemanticExplainer.explain(input)
    dag.addNode(rootName, block)
    val dependencies = needResolved(resolve(block), graph)
    dependencies.foreach(dag.addEdge(rootName, _))
    val queue = new mutable.Queue[String]()
    dependencies.foreach(queue.enqueue(_))
    while (!queue.isEmpty) {
      val ruleName = queue.dequeue()
      val dsl = graph.getRule(ruleName)
      logger.info(s"validate dslName=$ruleName, dsl=$dsl")
      val subBlock = SemanticExplainer.explain(parser.parse(generateDsl(dsl)))
      val nodeName = BlockUtils.getDefine(subBlock).head
      dag.addNode(nodeName, subBlock)
      val dependencies = needResolved(resolve(subBlock), graph)
      dependencies.foreach(queue.enqueue(_))
      dependencies.foreach(dag.addEdge(nodeName, _))
    }
    dag.order()
    dag
  }

  private def generateDsl(rule: SemanticRule): String = {
    rule match {
      case GeneralSemanticRule(dsl) => dsl
      case r: TemplateSemanticRule => r.constructDsl()
    }
  }

  private def needResolved(solved: SolvedModel, graph: SemanticPropertyGraph): Set[String] = {
    val refFields = solved.fields.values.toList
    val defined = new mutable.HashSet[String]()
    for (field <- refFields) {
      field match {
        case NodeVar(name, fields) =>
          val props = fields.filter(!_.resolved)
          val types = solved.getTypes(name)
          props.foreach(p => defined.add(types.head + "." + p.name))
        case EdgeVar(name, _) =>
          val types = solved.getTypes(name)
          if (types == null || types.isEmpty) {
            throw
              SchemaException(s"Cannot find $name types in ${solved.alias2Types}, pls check schema")
          }
          if (types.head.split("_").length == 3) {
            // TODO linked edge
            val edge = graph.getEdge(types.head)
            if (!edge.resolved) {
              defined.add(types.head)
            }
          }
        case _ =>
      }
    }
    defined.toSet
  }

  private def resolve(input: Block)(implicit context: LogicalPlannerContext): SolvedModel = {
    input.transform[SolvedModel] {
      case (matchBlock@MatchBlock(_, patterns), _) =>
        val types = patterns.values.head.graphPattern
          .generateGraphPatternTypesBySchema(context.catalog.getGraph(Catalog.defaultGraphName))
        val fields = matchBlock.binds.fields
        fieldToVar(fields, types)
      case (_, list) =>
        if (!list.isEmpty) {
          list.head
        } else {
          null
        }
    }
  }

  private def fieldToVar(fields: List[IRField], types: Map[String, Set[String]])(implicit
      context: LogicalPlannerContext): SolvedModel = {
    val graph = context.catalog.getGraph(Catalog.defaultGraphName)
    val varMap = new mutable.HashMap[String, Var]
    fields.foreach(field => {
      field match {
        case node: IRNode =>
          if (!types.contains(node.name) || (types(node.name).isEmpty && node.fields.nonEmpty)) {
            throw SchemaException(s"Cannot find ${node.name} in $types")
          }
          varMap.put(
            node.name,
            NodeVar(
              node.name,
              node.fields.map(graph.graphSchema.getNodeField(types(node.name), _)).toSet))
        case edge: IREdge =>
          if (types(edge.name).isEmpty && edge.fields.nonEmpty) {
            throw SchemaException(s"Cannot find ${edge.name} in $types")
          }
          varMap.put(
            edge.name,
            EdgeVar(
              edge.name,
              edge.fields.map(graph.graphSchema.getEdgeField(types(edge.name), _)).toSet))
        case path: IRRepeatPath =>
          val edge = path.element.elements(1).asInstanceOf[IREdge]
          if (types(edge.name).isEmpty && edge.fields.nonEmpty) {
            throw SchemaException(s"Cannot find ${edge.name} in $types")
          }
          varMap.put(
            edge.name,
            EdgeVar(
              edge.name,
              edge.fields.map(graph.graphSchema.getEdgeField(types(edge.name), _)).toSet))
        case _ => throw UnsupportedOperationException(s"validator unsupported ${field}")
      }
    })
    SolvedModel(types, varMap.toMap, Map.empty)
  }

}
