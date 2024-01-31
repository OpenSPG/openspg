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

import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.common.trees.TopDown
import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, RepeatPathVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.validate.Dag

class SubQueryMerger(val dag: Dag[LogicalOperator])(implicit context: LogicalPlannerContext) {
  private lazy val graph = context.catalog.getGraph(Catalog.defaultGraphName)

  def plan: LogicalOperator = {
    TopDown[LogicalOperator](subQuery).transform(dag.popRoot())
  }

  private def subQuery: PartialFunction[LogicalOperator, LogicalOperator] = {
    case expandInto: ExpandInto =>
      val alias = expandInto.pattern.root.alias
      val defined = needResolved(expandInto.solved, expandInto.refFields, expandInto.pattern)
      if (defined.isEmpty) {
        expandInto
      } else {
        expandInto.copy(in = planSubQuery(expandInto.in, defined, alias))
      }
    case scan: PatternScan =>
      val alias = scan.in.asInstanceOf[Source].alias
      val defined = needResolved(scan.solved, scan.refFields, scan.pattern)
      if (defined.isEmpty) {
        scan
      } else {
        scan.copy(in = planSubQuery(scan.in, defined, alias))
      }
  }

  private def planSubQuery(
      dependency: LogicalOperator,
      defines: Set[(String, Direction)],
      alias: String): LogicalOperator = {
    val defineList = defines.toList
    var left: LogicalOperator = dependency
    for (i <- 0 until defineList.size) {
      val define = defineList.apply(i)
      val nodeName = SubQueryPlanner.nodeName(define._1, define._2, alias)
      val right = dag.popNode(nodeName)
      left = SubQuery(left, right)
    }
    left
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

}
