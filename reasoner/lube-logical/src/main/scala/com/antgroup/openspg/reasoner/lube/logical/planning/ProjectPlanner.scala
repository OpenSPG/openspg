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

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.types.KgType
import com.antgroup.openspg.reasoner.lube.block.ProjectFields
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.{Directly, Expr}
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.operators.{LogicalOperator, Project, StackingLogicalOperator}
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Rule2ExprTransformer
import org.apache.commons.lang3.StringUtils

class ProjectPlanner(projects: ProjectFields)(implicit context: LogicalPlannerContext) {

  def plan(dependency: LogicalOperator): LogicalOperator = {
    val projectMap = new mutable.HashMap[Var, Expr]()
    var resolved = dependency.solved
    for (rule <- projects.items) {
      val ruleReferVars = RuleUtils.getAllInputFieldInRule(
        rule._2,
        resolved.getNodeAliasSet,
        resolved.getEdgeAliasSet)
      val referVars: List[IRField] = ruleReferVars.map(v => {
        if (v.isInstanceOf[IRVariable]) {
          val pv = resolved.getField(v.asInstanceOf[IRVariable])
          if (resolved.getNodeAliasSet.contains(pv.name)) {
            IRNode(pv.name, Set.apply(pv.field.name))
          } else {
            IREdge(pv.name, Set.apply(pv.field.name))
          }
        } else {
          v
        }
      })
      val propertyVar = getTarget(rule._1, referVars, rule._2, resolved, dependency)
      val transformer = new Rule2ExprTransformer()
      val reference = ruleReferVars.filter(_.isInstanceOf[IRVariable])
      val replaceVar = reference
        .map(varName => (varName.name, dependency.solved.getField(IRVariable(varName.name))))
        .toMap
      val newExpr = ExprUtil.transExpr(transformer.transform(rule._2), replaceVar)
      projectMap.put(propertyVar, newExpr)
      resolved = resolved.addField((IRVariable(rule._1.name), propertyVar))
    }
    for (field <- dependency.fields) {
      projectMap.put(field, Directly)
    }
    Project(dependency, projectMap.toMap, resolved.solve)
  }

  private def getTarget(
      left: IRField,
      referVars: List[IRField],
      rule: Rule,
      resolved: SolvedModel,
      dependency: LogicalOperator): PropertyVar = {
    val referTypes = new mutable.HashMap[IRField, KgType]()
    for (v <- referVars) {
      resolved.getVar(v.name) match {
        case p: PropertyVar => referTypes.put(v, p.field.kgType)
        case node: NodeVar =>
          node.fields.foreach(f => referTypes.put(IRProperty(v.name, f.name), f.kgType))
        case edge: EdgeVar =>
          edge.fields.foreach(f => referTypes.put(IRProperty(v.name, f.name), f.kgType))
        case _ => throw UnsupportedOperationException(s"cannot support $v")
      }
    }
    referTypes.++=(resolved.tmpFields.map(p => (p._1, p._2.field.kgType)))
    val ruleRetType = ExprUtil.getTargetType(rule, referTypes.toMap, context.catalog.getUdfRepo)

    left match {
      case IRVariable(name) =>
        if (referVars.size == 1) {
          PropertyVar(referVars.head.name, new Field(name, ruleRetType, true))
        } else {
          val aliasSet = new mutable.HashSet[String]()
          for (rVar <- referVars) {
            rVar match {
              case IRNode(name, _) => aliasSet.add(name)
              case v @ IRVariable(_) => aliasSet.add(resolved.getField(v).name)
              case _ =>
            }
          }
          val targetAlias = getTargetAlias(aliasSet.toSet, dependency)
          PropertyVar(targetAlias, new Field(left.name, ruleRetType, true))
        }
      case IRProperty(name, field) => PropertyVar(name, new Field(field, ruleRetType, true))
      case IRNode(name, fields) => PropertyVar(name, new Field(fields.head, ruleRetType, true))
      case IREdge(name, fields) => PropertyVar(name, new Field(fields.head, ruleRetType, true))
      case _ => throw UnsupportedOperationException(s"cannot support $left")
    }

  }

  private def getTargetAlias(aliasSet: Set[String], dependency: LogicalOperator): String = {
    val aliasOrder = dependency.transform[String] {
      case (stackOp: StackingLogicalOperator, list) =>
        if (StringUtils.isEmpty(list.head)) {
          val curAliasSet = stackOp.fields.map(_.name).toSet
          if (aliasSet.diff(curAliasSet).isEmpty) {
            if (aliasSet.isEmpty) {
              curAliasSet.head
            } else {
              aliasSet.head
            }
          } else {
            ""
          }
        } else {
          list.head
        }
      case (_, list) =>
        if (!list.isEmpty && StringUtils.isNotEmpty(list.head)) {
          list.head
        } else {
          ""
        }
    }
    aliasOrder
  }

}
