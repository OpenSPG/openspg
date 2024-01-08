/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.lube.physical.planning

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.common.exception.NotImplementedException
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.common.expr.Directly
import com.antgroup.openspg.reasoner.lube.logical.{NodeVar, PathVar, RepeatPathVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.planning.{FullOuterJoin, InnerJoin, LeftOuterJoin}
import com.antgroup.openspg.reasoner.lube.physical.operators
import com.antgroup.openspg.reasoner.lube.physical.operators.{AddInto, Drop, Fold, PhysicalOperator, Unfold}
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

/**
 * Physical planner for KGReasoner, generate an optimal physical plan for KGDSL or GQL.
 * Physical planner generate logical plan for giving logical plan,
 * which is represented by [[LogicalOperator]].
 */
object PhysicalPlanner {

  /**
   * Generate physical plan for giving logical plan.
   *
   * @param input unresolved logical plan
   * @return
   */
  def plan[T <: RDG[T]: TypeTag](input: LogicalOperator)(implicit
      context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    plan[T](input, null)
  }

  private def plan[T <: RDG[T]: TypeTag](input: LogicalOperator, workingRdgName: String)(implicit
      context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    input match {
      case start: Source => planStart(start, workingRdgName)
      case PatternScan(in, pattern) =>
        operators.PatternScan(plan[T](in, workingRdgName), pattern, input.fields)
      case ExpandInto(in, target, pattern) =>
        operators.ExpandInto(plan[T](in, workingRdgName), target, pattern, input.fields)
      case Select(in, orderedFields, as) =>
        operators.Select(plan[T](in, workingRdgName), orderedFields, as)
      case Filter(in, expr) => operators.Filter(plan[T](in, workingRdgName), expr)
      case DDL(in, ddlOp) => operators.DDL(plan[T](in, workingRdgName), ddlOp)
      case Aggregate(in, group, aggregations, _) =>
        operators.Aggregate(plan[T](in, workingRdgName), group, aggregations, input.fields)
      case OrderAndLimit(in, group, sortItem, limit) =>
        operators.OrderBy(plan[T](in, workingRdgName), sortItem, group, limit)
      case Project(in, _, _) =>
        planProject(plan[T](in, workingRdgName), input.asInstanceOf[Project])
      case LinkedExpand(in, linkedEdgePattern) =>
        operators.LinkedExpand(plan[T](in, workingRdgName), linkedEdgePattern, input.fields)
      case subQuery: SubQuery => planSubQuery(subQuery, workingRdgName)
      case boundedVarLenExpand: BoundedVarLenExpand =>
        planBoundedVarLenExpand(boundedVarLenExpand, workingRdgName)
      case optional: Optional => planOptional(optional, workingRdgName)
      case patternJoin: PatternJoin => planPatternJoin(patternJoin, workingRdgName)
      case patternUnion: PatternUnion => planPatternUnion(patternUnion, workingRdgName)
      case other => throw NotImplementedException(s"physical planning of operator $other")
    }
  }

  private def planStart[T <: RDG[T]: TypeTag](start: Source, workingRdgName: String)(implicit
      context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    start match {
      case start: Start =>
        operators.Start(start.graph.graphName, start.alias, start.fields, start.types)(
          implicitly[TypeTag[T]],
          context)
      case driving: Driving =>
        operators.DrivingRDG(start.graph.graphName, start.fields, start.alias, workingRdgName)(
          implicitly[TypeTag[T]],
          context)
    }
  }

  private def planProject[T <: RDG[T]: TypeTag](
      dependency: PhysicalOperator[T],
      project: Project): PhysicalOperator[T] = {
    // addInto then drop
    var inputOp = dependency
    val addInto = project.expr.filter(!_._2.isInstanceOf[Directly.type])
    if (!addInto.isEmpty) {
      val addIntoOp = AddInto(dependency, addInto)
      inputOp = addIntoOp
    }

    val inputFields = inputOp.meta
    val outputFields =
      project.fields.filter(!_.isInstanceOf[PathVar]).map(f => f.flatten).flatten.distinct
    val fieldMap = new mutable.HashMap[String, Var]()
    for (v <- inputFields) {
      fieldMap.put(v.name, v)
    }
    for (v <- outputFields) {
      fieldMap.put(v.name, fieldMap(v.name).diff(v))
      if (fieldMap(v.name).isEmpty) {
        fieldMap.remove(v.name)
      }
    }
    val dropFields = fieldMap.values.toSet
    if (dropFields.isEmpty) {
      inputOp
    } else {
      Drop(inputOp, fieldMap.values.toSet)
    }
  }

  private def planSubQuery[T <: RDG[T]: TypeTag](subQuery: SubQuery, workingRdgName: String)(
      implicit context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    val left = plan[T](subQuery.lhs, workingRdgName)
    val cacheOp = operators.Cache(left)
    val right = plan[T](subQuery.rhs, cacheOp.cacheName)
    val rst = operators.Join(
      cacheOp,
      right,
      intersect(left.meta, right.meta),
      LeftOuterJoin,
      left.meta.map(l => (l, l)).toMap,
      right.meta.map(r => (r, r)).toMap)
    rst
  }

  private def planOptional[T <: RDG[T]: TypeTag](optional: Optional, workingRdgName: String)(
      implicit context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    val left = plan[T](optional.lhs, workingRdgName)
    val cacheOp = operators.Cache(left)
    val right = plan[T](optional.rhs, cacheOp.cacheName)
    val rst = operators.Join(
      cacheOp,
      right,
      intersect(left.meta, right.meta),
      LeftOuterJoin,
      left.meta.map(l => (l, l)).toMap,
      right.meta.map(r => (r, r)).toMap)
    rst
  }

  private def planPatternJoin[T <: RDG[T]: TypeTag](
      patternJoin: PatternJoin,
      workingRdgName: String)(implicit
      context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    val left = plan[T](patternJoin.lhs, workingRdgName)
    val cacheOp = operators.Cache(left)
    val right = patternJoin.joinType match {
      case LeftOuterJoin | InnerJoin => plan[T](patternJoin.rhs, cacheOp.cacheName)
      case _ => plan[T](patternJoin.rhs, workingRdgName)
    }

    val rst = operators.Join(
      cacheOp,
      right,
      intersect(left.meta, right.meta),
      patternJoin.joinType,
      left.meta.map(l => (l, l)).toMap,
      right.meta.map(r => (r, r)).toMap)
    rst
  }

  private def planPatternUnion[T <: RDG[T]: TypeTag](
      patternJoin: PatternUnion,
      workingRdgName: String)(implicit
      context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    val left = plan[T](patternJoin.lhs, workingRdgName)
    val cacheOp = operators.Cache(left)
    val right = plan[T](patternJoin.rhs, cacheOp.cacheName)
    val rst = operators.Union(cacheOp, right)
    rst
  }

  private def intersect(lhs: List[Var], rhs: List[Var]): List[(String, String)] = {
    val rst = new ListBuffer[(String, String)]()
    val lhsSet = lhs.filter(_.isInstanceOf[NodeVar]).map(_.name).toSet
    val rhsSet = rhs.filter(_.isInstanceOf[NodeVar]).map(_.name).toSet
    for (alias <- lhsSet.intersect(rhsSet)) {
      rst.prepend((alias, alias))
    }
    rst.toList
  }

  private def planBoundedVarLenExpand[T <: RDG[T]: TypeTag](
      boundedVarLenExpand: BoundedVarLenExpand,
      workingRdgName: String)(implicit
      context: PhysicalPlannerContext[T]): PhysicalOperator[T] = {
    val left = plan[T](boundedVarLenExpand.lhs, workingRdgName)
    val cacheOp = operators.Cache(left)
    val right = plan[T](boundedVarLenExpand.rhs, cacheOp.cacheName)
    val joinPair = if (boundedVarLenExpand.index == 1) {
      (boundedVarLenExpand.edgePattern.src.alias, boundedVarLenExpand.edgePattern.src.alias)
    } else {
      (boundedVarLenExpand.edgePattern.dst.alias, boundedVarLenExpand.edgePattern.src.alias)
    }
    val leftMapping = left.meta.map(m => m.flatten).flatten.map(v => (v, v)).toMap
    val rightMapping = right.meta
      .map(m => m.flatten)
      .flatten
      .map(v => (v, v.rename(v.name + "_varlen_" + boundedVarLenExpand.index)))
      .toMap
    val joinOp = if (boundedVarLenExpand.index <= boundedVarLenExpand.edgePattern.edge.lower) {
      operators.Join(cacheOp, right, List.apply(joinPair), InnerJoin, leftMapping, rightMapping)
    } else {
      operators.Join(
        cacheOp,
        right,
        List.apply(joinPair),
        LeftOuterJoin,
        leftMapping,
        rightMapping)
    }
    val repeatPathVar = getRepeatPathVar(boundedVarLenExpand, right.meta)
    val fold = Fold(
      joinOp,
      List.apply((repeatPathVar.pathVar.elements.map(rightMapping(_)), repeatPathVar)))
    if (boundedVarLenExpand.index == boundedVarLenExpand.edgePattern.edge.upper) {
      Unfold(fold, List.apply((repeatPathVar, repeatPathVar.pathVar.elements)))
    } else {
      fold
    }
  }

  private def getRepeatPathVar(boundedVarLenExpand: BoundedVarLenExpand, inputMeta: List[Var]) = {
    val varMap = inputMeta.map(f => (f.name, f)).toMap
    val repeatPathVar = varMap(boundedVarLenExpand.edgePattern.edge.alias)
      .asInstanceOf[RepeatPathVar]
    val lower = boundedVarLenExpand.edgePattern.edge.lower
    val upper = boundedVarLenExpand.edgePattern.edge.upper
    boundedVarLenExpand.edgePattern.edge.direction match {
      case Direction.IN =>
        val pathVar = repeatPathVar.pathVar
        repeatPathVar.copy(
          lower = lower,
          upper = upper,
          pathVar = pathVar.copy(elements = pathVar.elements.reverse))
      case _ =>
        repeatPathVar.copy(lower = lower, upper = upper)
    }
  }

}
