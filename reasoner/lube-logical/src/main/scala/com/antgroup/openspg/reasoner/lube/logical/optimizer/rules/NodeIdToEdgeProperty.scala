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
import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.graph.edge
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.block.{AddPredicate, AddProperty, AddVertex, DDLOp}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRNode, IRProperty, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.pattern.{Connection, NodePattern, Pattern, VariablePatternConnection}
import com.antgroup.openspg.reasoner.lube.logical.{NodeVar, PropertyVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.utils.{ExprUtils, RuleUtils}

object NodeIdToEdgeProperty extends Rule {
  private val NODE_DEFAULT_PROPS = Set.apply(Constants.NODE_ID_KEY, Constants.CONTEXT_LABEL)

  def ruleWithContext(implicit context: LogicalPlannerContext): PartialFunction[
    (LogicalOperator, Map[String, Object]),
    (LogicalOperator, Map[String, Object])] = {
    case (expandInto: ExpandInto, map) =>
      if (!canPushDown(expandInto)) {
        expandInto -> map
      } else {
        val toEdge = targetConnection(expandInto)
        if (toEdge != null) {
          expandInto.in -> (map + (expandInto.pattern.root.alias -> toEdge))
        } else {
          expandInto -> map
        }
      }
    case (filter: Filter, map) =>
      if (map.isEmpty) {
        filter -> map
      } else {
        filterUpdate(filter, map) -> map
      }
    case (project: Project, map) =>
      if (map.isEmpty) {
        project -> map
      } else {
        projectUpdate(project, map) -> map
      }
    case (select: Select, map) =>
      if (map.isEmpty) {
        select -> map
      } else {
        selectUpdate(select, map) -> map
      }
    case (ddl: DDL, map) =>
      if (map.isEmpty) {
        ddl -> map
      } else {
        ddlUpdate(ddl, map) -> map
      }
    case (varLenExpand @ BoundedVarLenExpand(_, expandInto: ExpandInto, edgePattern, _), map) =>
      if (edgePattern.edge.upper == 1 && canPushDown(expandInto)) {
        val toEdge = targetConnection(expandInto)
        varLenExpand -> (map + (expandInto.pattern.root.alias -> toEdge))
      } else if (canPushDown(expandInto)) {
        varLenExpand -> (map - expandInto.pattern.root.alias)
      } else {
        varLenExpand -> map
      }
  }

  private def genField(direction: edge.Direction, fieldName: String): String = {
    (direction, fieldName) match {
      case (edge.Direction.OUT | edge.Direction.BOTH, Constants.NODE_ID_KEY) =>
        Constants.EDGE_TO_ID_KEY
      case (edge.Direction.OUT | edge.Direction.BOTH, Constants.CONTEXT_LABEL) =>
        Constants.EDGE_TO_ID_TYPE_KEY
      case (edge.Direction.IN, Constants.NODE_ID_KEY) => Constants.EDGE_FROM_ID_KEY
      case (edge.Direction.IN, Constants.CONTEXT_LABEL) => Constants.EDGE_FROM_ID_TYPE_KEY
      case (_, _) =>
        throw UnsupportedOperationException(s"""unsupport (${direction}, ${fieldName})""")
    }
  }

  private def filterUpdate(filter: Filter, map: Map[String, Object]): Filter = {
    val input = RuleUtils.getAllInputFieldInRule(
      filter.rule,
      filter.solved.getNodeAliasSet,
      filter.solved.getEdgeAliasSet)
    val replaceVar = new mutable.HashMap[IRField, IRField]
    for (irField <- input) {
      if (irField.isInstanceOf[IRNode] && map.contains(irField.name)) {
        for (propName <- irField.asInstanceOf[IRNode].fields) {
          if (NODE_DEFAULT_PROPS.contains(propName)) {
            val edgeInfo = map(irField.name).asInstanceOf[Connection]
            replaceVar.put(
              IRProperty(irField.name, propName),
              IRProperty(edgeInfo.alias, genField(edgeInfo.direction, propName)))
            replaceVar.put(IRVariable(irField.name), IRVariable(edgeInfo.alias))
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

  private def projectUpdate(project: Project, map: Map[String, Object]): Project = {
    val exprMap = new mutable.HashMap[Var, Expr]()
    for (expr <- project.expr) {
      exprMap.put(
        expr._1,
        exprRewrite(expr._2, project.solved.getNodeAliasSet, project.solved.getEdgeAliasSet, map))
    }
    project.copy(expr = exprMap.toMap)
  }

  private def exprRewrite(
      expr: Expr,
      nodes: Set[String],
      edges: Set[String],
      map: Map[String, Object]): Expr = {
    val input = ExprUtils.getAllInputFieldInRule(expr, nodes, edges)
    val replaceVar = new mutable.HashMap[IRField, IRField]
    for (irField <- input) {
      if (irField.isInstanceOf[IRNode] && map.contains(irField.name)) {
        for (propName <- irField.asInstanceOf[IRNode].fields) {
          if (NODE_DEFAULT_PROPS.contains(propName)) {
            val edgeInfo = map(irField.name).asInstanceOf[Connection]
            replaceVar.put(
              IRProperty(irField.name, propName),
              IRProperty(edgeInfo.alias, genField(edgeInfo.direction, propName)))
            replaceVar.put(IRVariable(irField.name), IRVariable(edgeInfo.alias))
          }
        }
      }
    }
    if (replaceVar.isEmpty) {
      expr
    } else {
      ExprUtils.renameVariableInExpr(expr, replaceVar.toMap)
    }
  }

  private def ddlUpdate(ddl: DDL, map: Map[String, Object]): DDL = {
    val nodes = ddl.solved.getNodeAliasSet
    val edges = ddl.solved.getEdgeAliasSet
    val newOps = new mutable.HashSet[DDLOp]()
    for (ddlOp <- ddl.ddlOp) {
      ddlOp match {
        case ddlOp: AddProperty => newOps.add(ddlOp)
        case AddVertex(s, props) =>
          val newProps = props.map(p => (p._1, exprRewrite(p._2, nodes, edges, map)))
          newOps.add(AddVertex(s, newProps))
        case AddPredicate(predicate, _) =>
          val newProps = predicate.fields.map(p => (p._1, exprRewrite(p._2, nodes, edges, map)))
          newOps.add(AddPredicate(predicate.copy(fields = newProps)))
      }
    }
    ddl.copy(ddlOp = newOps.toSet)
  }

  private def selectUpdate(select: Select, map: Map[String, Object]): Select = {
    val newFields = new ListBuffer[Var]()
    for (field <- select.fields) {
      if (field.isInstanceOf[PropertyVar] && map.contains(field.name)) {
        val edgeInfo = map(field.name).asInstanceOf[Connection]
        val propName = field.asInstanceOf[PropertyVar].field.name
        if (NODE_DEFAULT_PROPS.contains(propName)) {
          newFields.append(
            PropertyVar(
              edgeInfo.alias,
              new Field(genField(edgeInfo.direction, propName), KTString, true)))
        } else {
          newFields.append(field)
        }
      } else {
        newFields.append(field)
      }
    }
    select.copy(fields = newFields.toList)
  }

  private def targetConnection(expandInto: ExpandInto): Connection = {
    val alias = expandInto.pattern.root.alias
    val edgeAlias = expandInto.transform[Connection] {
      case (patternScan: PatternScan, _) =>
        targetConnection(alias, patternScan.pattern)
      case (expandInto: ExpandInto, list) =>
        if (!list.isEmpty && list.head != null) {
          list.head
        } else {
          targetConnection(alias, expandInto.pattern)
        }
      case (linkedExpand: LinkedExpand, list) =>
        if (!list.isEmpty && list.head != null) {
          list.head
        } else {
          targetConnection(alias, linkedExpand.edgePattern)
        }
      case (_, list) =>
        if (list.isEmpty) {
          null
        } else {
          list.head
        }
    }
    edgeAlias
  }

  private def targetConnection(alias: String, pattern: Pattern): Connection = {
    val connections = pattern.topology.values.flatten.filter(_.target.equals(alias))
    val fixedEdges = connections.filter(!_.isInstanceOf[VariablePatternConnection])
    val varEdges = connections
      .filter(_.isInstanceOf[VariablePatternConnection])
      .map(_.asInstanceOf[VariablePatternConnection])
      .filter(_.upper == 1)
    if (fixedEdges.isEmpty && varEdges.isEmpty) {
      null
    } else if (fixedEdges.isEmpty) {
      varEdges.head
    } else {
      fixedEdges.head
    }
  }

  private def canPushDown(expandInto: ExpandInto): Boolean = {
    if (!expandInto.pattern.isInstanceOf[NodePattern]) {
      false
    } else {
      val fieldNames = expandInto.fields
        .filter(_.name.equals(expandInto.pattern.root.alias))
        .head
        .asInstanceOf[NodeVar]
        .fields
        .map(_.name)
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
