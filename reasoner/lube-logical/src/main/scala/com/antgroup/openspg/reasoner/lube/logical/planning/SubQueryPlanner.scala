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

package com.antgroup.openspg.reasoner.lube.logical.planning

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.common.utils.ParameterUtils
import com.antgroup.openspg.reasoner.lube.Logging
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.catalog.{Catalog, SemanticRule, TemplateSemanticRule}
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, RepeatPathVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.planning.SubQueryPlanner.nodeName
import com.antgroup.openspg.reasoner.lube.logical.validate.Dag
import com.antgroup.openspg.reasoner.lube.utils.BlockUtils

class SubQueryPlanner(val dag: Dag[Block])(implicit context: LogicalPlannerContext)
    extends Logging {

  private lazy val graph = context.catalog.getGraph(Catalog.defaultGraphName)

  def plan: Dag[LogicalOperator] = {
    if (!"true".equals(context.params
        .getOrElse(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, "false").toString)) {
      dag.map(LogicalPlanner.plan(_).head)
    } else {
      val logicalDag = new Dag[LogicalOperator]()
      val edges = dag.getEdges
      val renameMap = new mutable.HashMap[String, Set[String]]()
      renameMap.put("result", Set.apply("result"))
      val queue = new mutable.Queue[(String, String, String, Direction)]()
      queue.enqueue(("result", null, null, null))
      while (queue.nonEmpty) {
        val tuple = planSubQuery(renameMap, queue)
        logicalDag.addNode(tuple._1, tuple._2)
      }
      for (edge <- edges) {
        if (renameMap.contains(edge._1)) {
          for (from <- renameMap(edge._1)) {
            edge._2.map(renameMap(_)).flatten.map(logicalDag.addEdge(from, _))
          }
        }
      }
      logicalDag
    }
  }

  private def planSubQuery(
      renameMap: mutable.HashMap[String, Set[String]],
      queue: mutable.Queue[(String, String, String, Direction)]): (String, LogicalOperator) = {
    val q = queue.dequeue()
    val define = q._1
    val startAlias = q._2
    val startType = q._3
    val direction = q._4
    val block = dag.popNode(define)
    var logicalOperator: LogicalOperator = null
    if (startAlias == null) {
      logicalOperator = LogicalPlanner.plan(block).head
    } else {
      if (ParameterUtils.isEnableSPGPlanPrettyPrint(context.params)) {
        logger.info("origin block:")
        logger.info(block.pretty)
      }

      val finaBlock = rewriteBlock(block, startType, direction)
      if (ParameterUtils.isEnableSPGPlanPrettyPrint(context.params)) {
        logger.info("rewrite block:")
        logger.info(finaBlock.pretty)
      }
      logicalOperator = LogicalPlanner.plan(finaBlock).head
      logicalOperator = logicalOperator.rewrite {
        case start: Start => Driving(start.graph, startAlias, start.solved)
      }
    }
    if (ParameterUtils.isEnableSPGPlanPrettyPrint(context.params)) {
      logger.info(logicalOperator.pretty)
    }
    logicalOperator.transform[Unit] {
      case (expandInto: ExpandInto, _) =>
        val defined = needResolved(expandInto.solved, expandInto.refFields, expandInto.pattern)
        if (!defined.isEmpty) {
          val root = expandInto.pattern.root
          for (d <- defined) {
            queue.enqueue((d._1, root.alias, root.typeNames.head, d._2))
            if (!renameMap.contains(d._1)) {
              renameMap.put(d._1, Set.apply(nodeName(d._1, d._2, root.alias)))
            } else {
              renameMap.put(d._1, renameMap(d._1) + nodeName(d._1, d._2, root.alias))
            }
          }
        }
      case (scan: PatternScan, _) =>
        val defined = needResolved(scan.solved, scan.refFields, scan.pattern)
        if (!defined.isEmpty) {
          val root = scan.pattern.root
          val rootAlias = scan.in.asInstanceOf[Source].alias
          for (d <- defined) {
            queue.enqueue((d._1, rootAlias, root.typeNames.head, d._2))
            if (!renameMap.contains(d._1)) {
              renameMap.put(d._1, Set.apply(nodeName(d._1, d._2, rootAlias)))
            } else {
              renameMap.put(d._1, renameMap(d._1) + nodeName(d._1, d._2, rootAlias))
            }
          }
        }
      case (_, _) =>
    }
    Tuple2.apply(nodeName(define, direction, startAlias), logicalOperator)
  }

  private def needResolved(
      solved: SolvedModel,
      refFields: List[Var],
      pattern: Pattern): Set[(String, Direction)] = {
    val defined = new mutable.HashSet[(String, Direction)]()
    for (field <- refFields) {
      field match {
        case NodeVar(name, fields) =>
          val props = fields.filter(!_.resolved)
          val types = solved.getTypes(name)
          props.flatMap(p => types.map(t => s"$t.${p.name}")).foreach(p => defined.add((p, null)))
        case EdgeVar(name, fields) =>
          val types = solved.getTypes(name)
          for (t <- types) {
            val spo = new SPO(t)
            val edge = graph.getEdge(t)
            if (!edge.resolved) {
              val direction =
                pattern
                  .topology(pattern.root.alias)
                  .filter(_.alias.equals(name))
                  .filter(conn => {
                    if (conn.direction == Direction.OUT) {
                      conn.relTypes.contains(spo.getP) && pattern
                        .getNode(conn.target)
                        .typeNames
                        .contains(getMetaType(spo.getO))
                    } else {
                      conn.relTypes.contains(spo.getP) && pattern
                        .getNode(conn.source)
                        .typeNames
                        .contains(getMetaType(spo.getO))
                    }
                  })
                  .head
                  .direction
              defined.add((t, direction))
            }
          }
        case RepeatPathVar(pathVar, _, _) =>
          val types = solved.getTypes(pathVar.name)
          for (t <- types) {
            val spo = new SPO(t)
            val edge = graph.getEdge(t)
            if (!edge.resolved) {
              val direction =
                pattern
                  .topology(pattern.root.alias)
                  .filter(_.alias.equals(pathVar.name))
                  .filter(conn => {
                    if (conn.direction == Direction.OUT) {
                      conn.relTypes.contains(spo.getP) && pattern
                        .getNode(conn.target)
                        .typeNames
                        .contains(getMetaType(spo.getO))
                    } else {
                      conn.relTypes.contains(spo.getP) && pattern
                        .getNode(conn.source)
                        .typeNames
                        .contains(getMetaType(spo.getO))
                    }
                  })
                  .head
                  .direction
              defined.add((t, direction))
            }
          }
        case _ =>
      }
    }
    defined.toSet
  }

  private def getMetaType(sType: String) = {
    if (sType.contains("/")) {
      sType.split("/")(0)
    } else {
      sType
    }
  }

  private def rewriteBlock(
      block: Block,
      startType: String,
      direction: Direction): Block = {
    val rewriteBlock = rewriteBlockDirection(block, direction)
    val alias = getRootAlias(rewriteBlock, startType, direction)
    rewriteBlock.rewriteTopDown { case matchBlock: MatchBlock =>
      val patterns = matchBlock.patterns.map(tuple =>
        (
          tuple._1,
          tuple._2.copy(graphPattern = tuple._2.graphPattern.copy(rootAlias = alias))))
      matchBlock.copy(patterns = patterns)
    }
  }

  private def rewriteBlockDirection(block: Block, direction: Direction): Block = {
    if (!block.isInstanceOf[DDLBlock]) {
      return block
    }
    try {
      val defineName = BlockUtils.getDefine(block)
      val semanticRule: SemanticRule =
        context.catalog.getGraph(Catalog.defaultGraphName).getRule(defineName.head)
      semanticRule match {
        case rule: TemplateSemanticRule =>
          if (Direction.IN.equals(direction)) {
            return context.parser.parse(rule.constructReverseDsl())
          }
        case _ =>
      }
    } catch {
      case e: Exception =>
        logger.warn(e.getMessage)
    }
    block
  }

  private def getRootAlias(block: Block, startType: String, direction: Direction): String = {
    var rootAlias: String = null
    block match {
      case DDLBlock(ddlOps, _) =>
        ddlOps.foreach(op => {
          op match {
            case AddPredicate(predicate, _) =>
              if (direction == Direction.OUT && predicate.direction == Direction.OUT) {
                rootAlias = predicate.source.alias
              } else if (direction == Direction.IN && predicate.direction == Direction.OUT) {
                rootAlias = predicate.target.alias
              } else if (direction == Direction.OUT && predicate.direction == Direction.IN) {
                rootAlias == predicate.target.alias
              } else if (direction != null) {
                rootAlias == predicate.source.alias
              }
            case AddProperty(s, _, _, _) =>
              rootAlias = s.alias
            case _ =>
          }
        })
      case _ =>
    }
    rootAlias
  }

}

object SubQueryPlanner {

  def nodeName(define: String, direction: Direction, alias: String): String = {
    if (define.equals("result")) {
      define
    } else if (direction == null) {
      s"$define$alias"
    } else {
      s"$define$direction$alias"
    }
  }

}
