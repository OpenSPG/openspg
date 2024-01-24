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

import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.lube.logical.{SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * Pure solvedModel after EdgeToProperty
 */
object SolvedModelPure extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case select: Select =>
      val newModel = resolvedModel(select)
      pure(select, newModel)
    case ddl: DDL =>
      val newModel = resolvedModel(ddl)
      pure(ddl, newModel)

  }

  private def resolvedModel(input: LogicalOperator): SolvedModel = {
    val fields = input.transform[List[Var]] {
      case (scan: PatternScan, _) => scan.refFields
      case (expandInto: ExpandInto, tupleList) =>
        val list = new mutable.ListBuffer[Var]()
        list.++=(tupleList.flatten)
        list.++=(expandInto.refFields)
        list.toList
      case (linkedExpand: LinkedExpand, tupleList) =>
        val list = new mutable.ListBuffer[Var]()
        list.++=(tupleList.flatten)
        list.++=(linkedExpand.refFields)
        list.toList
      case (_, tupleList) => tupleList.flatten
    }
    val fieldMap = fields.map(f => (f.name, f)).toMap
    input.solved.copy(fields = fieldMap)
  }

  private def pure(input: LogicalOperator, newModel: SolvedModel): LogicalOperator = {
    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case Start(graph, alias, types, solved) =>
        Start(graph, alias, types, mergeSolvedModel(solved, newModel))
      case Driving(graph, alias, solved) =>
        Driving(graph, alias, mergeSolvedModel(solved, newModel))
      case Project(in, expr, solved) =>
        val finalSolved = mergeSolvedModel(solved, newModel)
        val finalExpr = expr.filter(e => finalSolved.fields.contains(e._1.name))
        Project(in, finalExpr, finalSolved)
      case Aggregate(in, group, aggregations, solved) =>
        Aggregate(in, group, aggregations, mergeSolvedModel(solved, newModel))
    }

    BottomUp[LogicalOperator](rewriter).transform(input)
  }

  private def mergeSolvedModel(curModel: SolvedModel, newModel: SolvedModel): SolvedModel = {
    val alias2Types = new mutable.HashMap[String, Set[String]]()
    val fields = new mutable.HashMap[String, Var]()
    for (pair <- curModel.alias2Types) {
      if (newModel.fields.contains(pair._1)) {
        alias2Types.put(pair._1, pair._2)
      }
    }
    for (pair <- newModel.fields) {
      fields.put(pair._1, pair._2.merge(curModel.fields.get(pair._1)))
    }
    SolvedModel(alias2Types.toMap, fields.toMap, curModel.tmpFields)
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1

}
