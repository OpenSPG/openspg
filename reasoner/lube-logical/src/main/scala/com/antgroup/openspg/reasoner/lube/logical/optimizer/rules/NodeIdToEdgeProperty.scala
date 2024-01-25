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
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.SystemError
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRNode, IRProperty}
import com.antgroup.openspg.reasoner.lube.common.pattern.{NodePattern, Pattern, VariablePatternConnection}
import com.antgroup.openspg.reasoner.lube.logical.{NodeVar, PropertyVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils

object NodeIdToEdgeProperty extends Rule {
  private val NODE_DEFAULT_PROPS = Set.apply(Constants.NODE_ID_KEY, Constants.CONTEXT_LABEL)

  def ruleWithContext(implicit context: LogicalPlannerContext): PartialFunction[
    (LogicalOperator, Map[String, Object]),
    (LogicalOperator, Map[String, Object])] = {
    case (expandInto: ExpandInto, map) =>
      if (!canPushDown(expandInto)) {
        expandInto -> map
      } else {
        val toEdgeAlias = toPropertyName(expandInto)
        expandInto -> (map + (expandInto.pattern.root.alias -> toEdgeAlias))
      }
    case (filter: Filter, map) => filterUpdate(filter, map) -> map
    case (select: Select, map) => selectUpdate(select, map) -> map
  }

  private def filterUpdate(filter: Filter, map: Map[String, Object]): Filter = {
    val input = RuleUtils.getAllInputFieldInRule(filter.rule, null, null)
    val replaceVar = new mutable.HashMap[IRField, IRProperty]
    for (irField <- input) {
      if (irField.isInstanceOf[IRNode] && map.contains(irField.name)) {
        for (propName <- irField.asInstanceOf[IRNode].fields) {
          propName match {
            case Constants.NODE_ID_KEY =>
              replaceVar.put(
                IRProperty(irField.name, Constants.NODE_ID_KEY),
                IRProperty(map(irField.name).toString, Constants.EDGE_TO_ID_KEY))
            case Constants.CONTEXT_LABEL =>
              replaceVar.put(
                IRProperty(irField.name, Constants.CONTEXT_LABEL),
                IRProperty(map(irField.name).toString, Constants.EDGE_TO_ID_TYPE_KEY))
            case _ => throw SystemError("something wrong.")
          }
        }
      }
    }
    if (replaceVar.isEmpty) {
      filter
    } else {
      val newRule = RuleUtils.renameVariableInRule(filter.rule, replaceVar.toMap)
      filter.copy(rule = newRule)
    }
  }

  private def selectUpdate(select: Select, map: Map[String, Object]): Select = {
    val newFields = new ListBuffer[Var]()
    for (field <- select.fields) {
      if (field.isInstanceOf[PropertyVar] && map.contains(field.name)) {
        val propName = field.asInstanceOf[PropertyVar].field.name
        propName match {
          case Constants.NODE_ID_KEY =>
            newFields.append(
              PropertyVar(
                map(field.name).toString,
                new Field(Constants.EDGE_TO_ID_KEY, KTString, true)))
          case Constants.CONTEXT_LABEL =>
            newFields.append(
              PropertyVar(
                map(field.name).toString,
                new Field(Constants.EDGE_TO_ID_TYPE_KEY, KTString, true)))
          case _ => throw SystemError("something wrong.")
        }
      } else {
        newFields.append(field)
      }
    }
    select.copy(fields = newFields.toList)
  }

  private def toPropertyName(expandInto: ExpandInto): String = {
    val alias = expandInto.pattern.root.alias
    val edgeAlias = expandInto.transform[String] {
      case (patternScan: PatternScan, _) =>
        getEdgePropertyName(alias, patternScan.pattern)
      case (expandInto: ExpandInto, list) =>
        if (!list.isEmpty && !list.head.isEmpty) {
          list.head
        } else {
          getEdgePropertyName(alias, expandInto.pattern)
        }
      case (_, list) =>
        if (list.isEmpty) {
          ""
        } else {
          list.head
        }
    }
    edgeAlias
  }

  private def getEdgePropertyName(alias: String, pattern: Pattern): String = {
    val connections = pattern.topology.values.flatten.filter(_.target.equals(alias))
    val fixedEdges = connections.filter(!_.isInstanceOf[VariablePatternConnection])
    val varEdges = connections
      .filter(_.isInstanceOf[VariablePatternConnection])
      .map(_.asInstanceOf[VariablePatternConnection])
      .filter(_.upper == 1)
    if (fixedEdges.isEmpty && varEdges.isEmpty) {
      ""
    } else if (fixedEdges.isEmpty) {
      varEdges.head.alias
    } else {
      fixedEdges.head.alias
    }
  }

  private def canPushDown(expandInto: ExpandInto): Boolean = {
    if (!expandInto.pattern.isInstanceOf[NodePattern]) {
      false
    } else {
      val fieldNames = expandInto.refFields.head.asInstanceOf[NodeVar].fields.map(_.name)
      val normalNames = fieldNames.filter(!NODE_DEFAULT_PROPS.contains(_))
      if (normalNames.isEmpty) {
        true
      } else {
        false
      }
    }
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
