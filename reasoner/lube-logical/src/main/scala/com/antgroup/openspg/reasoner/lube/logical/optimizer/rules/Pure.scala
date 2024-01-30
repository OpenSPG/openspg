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

import com.antgroup.openspg.reasoner.common.exception.InvalidRefVariable
import com.antgroup.openspg.reasoner.lube.common.expr.Directly
import com.antgroup.openspg.reasoner.lube.logical.{PathVar, RepeatPathVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Down, SimpleRule}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * Attribute clipping
 * if and only if the output of the current Op is not dependent on the downstream node,
 * add a ProjectOp for attribute clipping
 */
object Pure extends SimpleRule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case leaf: LogicalLeafOperator => leaf
    case ddl @ DDL(in, _) =>
      val projects = ddl.refFields.map((_, Directly)).toMap
      ddl.withNewChildren(Array.apply(Project(in, projects, in.solved)))
    case select @ Select(in, _, _) =>
      val projects = select.refFields.map((_, Directly)).toMap
      select.withNewChildren(Array.apply(Project(in, projects, in.solved)))
    case project @ Project(in, _, _) =>
      if (in.isInstanceOf[Project] || in.isInstanceOf[ExpandInto] || in
          .isInstanceOf[PatternScan] || in.isInstanceOf[BinaryLogicalOperator]) {
        project
      } else {
        val projectOutput: List[Var] = project.fields
        val inRef = in.refFields
        val fields = mergeFields(projectOutput, inRef, in.solved)
        val ininOp = in.asInstanceOf[StackingLogicalOperator].in
        val newProject = Project(ininOp, fields.map((_, Directly)).toMap, ininOp.solved)
        val newIn = in.withNewChildren(Array.apply(newProject))
        project.withNewChildren(Array.apply(newIn))
      }

  }

  private def mergeFields(left: List[Var], right: List[Var], solved: SolvedModel): List[Var] = {
    val varMap = new mutable.HashMap[String, Var]()
    if (left != null && left.nonEmpty) {
      for (v <- left) {
        varMap.put(v.name, v)
      }
    }
    if (right != null && right.nonEmpty) {
      for (v <- right) {
        if (!varMap.contains(v.name)) {
          varMap.put(v.name, v)
        } else {
          varMap.put(v.name, varMap(v.name).merge(Option.apply(v)))
        }
      }
    }

    varMap.values.map(f => {
      if (f.isInstanceOf[PathVar]) {
        f
      } else if (!solved.fields.contains(f.name)) {
        throw InvalidRefVariable(s"can not find $f")
      } else if (solved.fields.get(f.name).get.isInstanceOf[RepeatPathVar]) {
        f.intersect(solved.fields.get(f.name).get.asInstanceOf[RepeatPathVar].pathVar.elements(1))
      } else {
        f.intersect(solved.fields.get(f.name).get)
      }
    }).toList
  }

  override def direction: Direction = Down

  override def maxIterations: Int = 1
}
