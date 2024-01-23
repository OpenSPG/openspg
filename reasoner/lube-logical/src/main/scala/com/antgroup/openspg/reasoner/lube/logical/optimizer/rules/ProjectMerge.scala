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

import com.antgroup.openspg.reasoner.common.exception.SystemError
import com.antgroup.openspg.reasoner.lube.common.expr.{Directly, Expr}
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.operators.{LogicalOperator, Project}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * Merge adjacent Project operators
 */
object ProjectMerge extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case project @ Project(in: Project, _, _) =>
      if (!canMerge(project, in)) {
        project
      } else {
        val parentProjects = in.asInstanceOf[Project].expr
        val newProjects = new mutable.HashMap[Var, Expr]()
        for (pair <- parentProjects) {
          if (!pair._2.isInstanceOf[Directly.type]) {
            newProjects.put(pair._1, pair._2)
          }
        }

        val directProjects = new mutable.HashSet[Var]()
        for (pair <- project.expr) {
          if (pair._2.isInstanceOf[Directly.type]) {
            directProjects.add(pair._1)
          }
        }
        // direct projects - expr projects
        for (exprPro <- newProjects) {
          val v = exprPro._1
          if (directProjects.contains(v)) {
            val diffVar = diff(v, exprPro._1.asInstanceOf[PropertyVar])
            if (diffVar != null) {
              directProjects.add(diffVar)
            } else {
              directProjects.remove(v)
            }
          }
        }
        for (directPro <- directProjects) {
          newProjects.put(directPro, Directly)
        }

        for (pair <- project.expr) {
          if (!pair._2.isInstanceOf[Directly.type]) {
            newProjects.put(pair._1, pair._2)
          }
        }
        Project(in.asInstanceOf[Project].in, newProjects.toMap, in.solved)
      }
  }

  private def diff(left: Var, right: PropertyVar): Var = {
    left match {
      case NodeVar(name, fields) =>
        NodeVar(name, fields.diff(Set.apply(right.field)))
      case EdgeVar(name, fields) =>
        EdgeVar(name, fields.diff(Set.apply(right.field)))
      case PropertyVar(name, field) =>
        if (field.name.equals(right.field.name)) {
          null
        } else {
          left
        }
      case _ => throw SystemError(s"Unsupport $left")
    }
  }

  private def canMerge(project: Project, in: Project): Boolean = {
    val parentOutput = in.expr.filter(!_._2.isInstanceOf[Directly.type]).keySet
    val computeExprs = project.expr.filter(!_._2.isInstanceOf[Directly.type])
    for (expr <- computeExprs) {
      val fields = ExprUtil.getReferProperties(expr._2)
      for (pair <- fields) {
        val outputVar = parentOutput
          .filter(_.name.equals(pair._1))
          .map(_.asInstanceOf[PropertyVar])
          .map(_.field.name)
        if (outputVar.contains(pair._2)) {
          return false
        }
      }
    }
    true
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1

}
