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

package com.antgroup.openspg.reasoner.lube.logical.planning

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.types.{KTObject, KTString}
import com.antgroup.openspg.reasoner.lube.block.Aggregations
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.operators.{Aggregate, LogicalOperator, StackingLogicalOperator}
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils
import org.apache.commons.lang3.StringUtils

class AggregationPlanner(group: List[IRField], aggregations: Aggregations) {

  def plan(dependency: LogicalOperator): LogicalOperator = {
    val groupVar: List[Var] = group.map(toVar(_, dependency.solved))
    val aggMap = new mutable.HashMap[Var, Aggregator]()
    var resolved = dependency.solved
    for (p <- aggregations.pairs) {
      val ruleFields =
        ExprUtils.getAllInputFieldInRule(p._2, resolved.getNodeAliasSet, resolved.getEdgeAliasSet)
      val referFields: List[IRField] = ruleFields.map(v => {
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
      val renameVar = ruleFields
        .filter(_.isInstanceOf[IRVariable])
        .map(v => (v, propertyVarToIr(resolved.tmpFields(v.asInstanceOf[IRVariable]))))
        .toMap
      val newAggExpr = ExprUtils.renameVariableInExpr(p._2, renameVar).asInstanceOf[Aggregator]

      val field = getAggregateTarget(referFields, resolved, dependency)
      field match {
        case IRNode(alias, _) =>
          val propertyVar = PropertyVar(alias, new Field(p._1.name, KTString, true))
          aggMap.put(propertyVar, newAggExpr)
          resolved = resolved.addField((p._1.asInstanceOf[IRVariable], propertyVar))
        case IREdge(alias, _) =>
          if (resolved.getVar(alias).isInstanceOf[RepeatPathVar]) {
            aggMap.put(resolved.getVar(alias).asInstanceOf[RepeatPathVar].pathVar, newAggExpr)
          } else {
            val propertyVar = PropertyVar(alias, new Field(p._1.name, KTString, true))
            resolved = resolved.addField((p._1.asInstanceOf[IRVariable], propertyVar))
            aggMap.put(propertyVar, newAggExpr)
          }
        case IRVariable(alias) =>
          val tmpPropertyVar = resolved.tmpFields(IRVariable(alias))
          val propertyVar =
            PropertyVar(tmpPropertyVar.name, new Field(p._1.name, KTString, true))
          aggMap.put(propertyVar, newAggExpr)
          resolved = resolved.addField((p._1.asInstanceOf[IRVariable], propertyVar))
        case _ =>
          throw UnsupportedOperationException(s"unsupport $field")
      }
    }
    Aggregate(dependency, groupVar, aggMap.toMap, resolved.solve)
  }

  private def toVar(field: IRField, solvedModel: SolvedModel): Var = {
    field match {
      case IRNode(name, fields) => NodeVar(name, Set.empty)
      case IRProperty(name, field) => PropertyVar(name, new Field(field, KTObject, true))
      case field: IRVariable => solvedModel.getField(field)
      case _ => throw UnsupportedOperationException(s"cannot group by $field")
    }
  }

  private def getAggregateTarget(
                                  referFields: List[IRField],
                                  resolved: SolvedModel,
                                  dependency: LogicalOperator): IRField = {
    val fieldGroups = referFields.groupBy(_.name)
    if (fieldGroups.size == 1) {
      fieldGroups.values.head.head
    } else {
      val aliasSet = new mutable.HashSet[String]()
      for (rVar <- referFields) {
        rVar match {
          case IRNode(name, _) => aliasSet.add(name)
          case IREdge(name, _) => aliasSet.add(name)
          case v @ IRVariable(_) => aliasSet.add(resolved.getField(v).name)
          case _ =>
        }
      }
      val targetAlias = getTargetAlias(aliasSet.toSet, dependency)
      fieldGroups(targetAlias).head
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

  private def propertyVarToIr(propertyVar: PropertyVar): IRProperty = {
    IRProperty(propertyVar.name, propertyVar.field.name)
  }

}
