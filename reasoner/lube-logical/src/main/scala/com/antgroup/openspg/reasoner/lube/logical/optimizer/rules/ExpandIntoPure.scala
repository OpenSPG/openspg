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

package com.antgroup.openspg.reasoner.lube.logical.optimizer.rules

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.lube.catalog.{Catalog, SemanticPropertyGraph}
import com.antgroup.openspg.reasoner.lube.common.pattern.NodePattern
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, PropertyVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Down, Rule}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * Prue useless expandInto
 */
object ExpandIntoPure extends Rule {

  def ruleWithContext(implicit context: LogicalPlannerContext): PartialFunction[
    (LogicalOperator, Map[String, Object]),
    (LogicalOperator, Map[String, Object])] = {
    case (select: Select, map) =>
      select -> merge(map, select.refFields, select.solved.getNodeAliasSet)
    case (ddl: DDL, map) => ddl -> merge(map, ddl.refFields, ddl.solved.getNodeAliasSet)
    case (filter: Filter, map) =>
      filter -> merge(map, filter.refFields, filter.solved.getNodeAliasSet)
    case (project: Project, map) =>
      project -> merge(map, project.refFields, project.solved.getNodeAliasSet)
    case (aggregate: Aggregate, map) =>
      aggregate -> merge(map, aggregate.refFields, aggregate.solved.getNodeAliasSet)
    case (order: OrderAndLimit, map) =>
      order -> merge(map, order.refFields, order.solved.getNodeAliasSet)
    case (expandInto @ ExpandInto(in, _, _), map) =>
      val newMap = merge(map, expandInto.refFields, expandInto.solved.getNodeAliasSet)
      val needPure = canPure(
        expandInto,
        newMap.asInstanceOf[Map[String, Var]],
        context.catalog.getGraph(Catalog.defaultGraphName))
      if (needPure) {
        in -> newMap
      } else {
        expandInto -> newMap
      }

  }

  private def merge(
      map: Map[String, Object],
      fields: List[Var],
      nodes: Set[String]): Map[String, Object] = {
    val varMap = new mutable.HashMap[String, Var]()
    varMap.++=(map.asInstanceOf[Map[String, Var]])
    for (field <- fields) {
      if (varMap.contains(field.name)) {
        varMap.put(field.name, varMap(field.name).merge(Option.apply(field)))
      } else if (field.isInstanceOf[PropertyVar]) {
        if (nodes.contains(field.name)) {
          varMap.put(
            field.name,
            NodeVar(field.name, Set.apply(field.asInstanceOf[PropertyVar].field)))
        } else {
          varMap.put(
            field.name,
            EdgeVar(field.name, Set.apply(field.asInstanceOf[PropertyVar].field)))
        }
      } else {
        field.flatten.foreach(v => varMap.put(v.name, v))
      }
    }
    varMap.toMap
  }

  private def canPure(
      expandInto: ExpandInto,
      map: Map[String, Var],
      graph: SemanticPropertyGraph): Boolean = {
    if (!expandInto.pattern.isInstanceOf[NodePattern]) {
      false
    } else {
      val alias = expandInto.pattern.root.alias
      val types = expandInto.pattern.root.typeNames
      if (!map.contains(alias) || map(alias).isEmpty) {
        true
      } else {
        val usedPros = map(alias).asInstanceOf[NodeVar].fields
        val originalProps = types.map(graph.getNode(_).properties).flatten
        if (usedPros.intersect(originalProps).isEmpty) {
          true
        } else {
          false
        }
      }
    }
  }

  override def direction: Direction = Down

  override def maxIterations: Int = 10
}
