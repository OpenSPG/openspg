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

package com.antgroup.openspg.reasoner.lube.logical.optimizer.rules

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.trees.{BottomUpWithContext, TopDown}
import com.antgroup.openspg.reasoner.common.types.{KTAdvanced, KTConcept, KTStd}
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Field, NodeType}
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRProperty}
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.rule.{Rule => LogicalRule}
import com.antgroup.openspg.reasoner.lube.logical.NodeVar
import com.antgroup.openspg.reasoner.lube.logical.PatternOps.PatternOps
import com.antgroup.openspg.reasoner.lube.logical.operators.{ExpandInto, LogicalOperator, PatternScan}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Down, Rule}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils

/**
 * Transform edge to property, there are three possibilities.
 * 1. cur node is std or concept, then push related edge to property.
 * 2. cur node is advanced and has std/concept/advanced property,
 *    then travel down to find related edge and push the related edge to property.
 * 3. cur node is advanced, then travel down to find cur node is transferred from a attribute.
 * All possibilities require a downward traversal, and the downward traversal can cover all cases.
 */
object EdgeToProperty extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case expandInto @ ExpandInto(_, _, pattern) =>
      edgeToProperty(expandInto, expandInto.graph, pattern)
  }

  private def edgeToProperty(
      expandInto: ExpandInto,
      graph: SemanticPropertyGraph,
      pattern: Pattern): LogicalOperator = {
    val rootTypes = pattern.root.typeNames
    if (rootTypes.size > 1) {
      return expandInto
    }
    val rootNode = graph.getNode(rootTypes.head)
    if (rootNode.nodeType == NodeType.CONCEPT || rootNode.nodeType == NodeType.STANDARD) {
      val logicalOp = stdPushDown(expandInto, pattern)
      return logicalOp
    }
    val propsTypes = rootNode.properties
      .filter(t =>
        t.kgType.isInstanceOf[KTStd] || t.kgType.isInstanceOf[KTConcept] || t.kgType
          .isInstanceOf[KTAdvanced])
    if (!propsTypes.isEmpty) {
      val logicalOp = basicToStd(expandInto, propsTypes, pattern)
      return logicalOp
    }
    expandInto
  }

  /**
   * Case 1: Concept or Standard
   * @param expandInto
   * @param pattern
   * @return
   */
  private def stdPushDown(expandInto: ExpandInto, pattern: Pattern): LogicalOperator = {
    if (!pattern.isInstanceOf[NodePattern]) {
      // has other edge
      expandInto
    } else {
      if (hasOnlyId(expandInto)) {
        stdToProperty(expandInto.in, pattern)
      } else {
        expandInto
      }
    }
  }

  private def stdToProperty(root: LogicalOperator, pattern: Pattern): LogicalOperator = {
    val alias = pattern.root.alias
    val rule = pattern.root.rule
    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case expandInto @ ExpandInto(in, target, pattern) =>
        val conns = pattern.topology.values.flatten.filter(_.target.equals(alias)).toSet
        if (conns.isEmpty) {
          expandInto
        } else {
          var newPattern: Pattern = null
          for (conn <- conns) {
            newPattern = pattern.removeConnection(conn.source, conn.target)
            if (rule != null) {
              val curAlias = newPattern.root.alias
              val map: Map[IRField, IRProperty] =
                Map.apply(IRProperty(alias, "id") -> IRProperty(curAlias, conn.relTypes.head))
              val newRule = RuleUtils.renameVariableInRule(rule, map)
              newPattern = newPattern.fillInRule(newRule, curAlias)
            }
          }
          ExpandInto(in, target, newPattern)
        }
      case patternScan @ PatternScan(in, pattern) =>
        val conns = pattern.topology.values.flatten.filter(_.target.equals(alias)).toSet
        if (conns.isEmpty) {
          patternScan
        } else {
          var newPattern: Pattern = null
          for (conn <- conns) {
            newPattern = pattern.removeConnection(conn.source, conn.target)
            if (rule != null) {
              val curAlias = newPattern.root.alias
              val map: Map[IRField, IRProperty] =
                Map.apply(IRProperty(alias, "id") -> IRProperty(curAlias, conn.relTypes.head))
              val newRule = RuleUtils.renameVariableInRule(rule, map)
              newPattern = newPattern.fillInRule(newRule, curAlias)
            }
          }
          PatternScan(in, newPattern)
        }
    }
    TopDown[LogicalOperator](rewriter).transform(root)
  }

  private def hasOnlyId(logicalOp: LogicalOperator): Boolean = {
    val refProps = logicalOp.refFields.head
      .asInstanceOf[NodeVar]
      .fields
      .map(_.name)
    if (refProps.isEmpty || refProps.equals(Set.apply("id"))) {
      // has only `id` or empty props
      true
    } else {
      false
    }
  }

  /**
   * Case 2: cur node is advanced and has std/concept/advanced property
   *
   * @param expandInto
   * @param graph
   * @param pattern
   * @return
   */
  private def basicToStd(
      expandInto: ExpandInto,
      fields: Set[Field],
      pattern: Pattern): LogicalOperator = {
    val fieldNames = fields.map(_.name)

    val rewriter: PartialFunction[
      (LogicalOperator, mutable.HashMap[String, LogicalRule]),
      (LogicalOperator, mutable.HashMap[String, LogicalRule])] = {
      case (expand @ ExpandInto(in, target, pat), ruleMap) =>
        val relations =
          pat.topology.values.flatten.filter(r => !r.relTypes.intersect(fieldNames).isEmpty)
        if (!relations.isEmpty) {
          if (hasOnlyId(expand)) {
            val src = pat.root.alias
            val dst = pattern.root.alias
            val newPattern = pat.removeConnection(src, dst)
            val map: Map[IRField, IRProperty] =
              Map.apply(IRProperty(src, "id") -> IRProperty(dst, relations.head.relTypes.head))
            val newRule = RuleUtils.renameVariableInRule(pat.root.rule, map)
            ruleMap.put(src, newRule)
            (ExpandInto(in, target, newPattern), ruleMap)
          } else {
            (expand, ruleMap)
          }
        } else {
          (expand, ruleMap)
        }
    }

    val newIn = BottomUpWithContext(rewriter).transform(
      expandInto.in,
      new mutable.HashMap[String, LogicalRule]())
    if (newIn._2.isEmpty) {
      expandInto
    } else {
      val newPattern = pattern
      for (pair <- newIn._2) {
        if (pair._2 != null) {
          if (newPattern.root.rule == null) {
            newPattern.root.rule = pair._2
          } else {
            newPattern.root.rule = newPattern.root.rule.andRule(pair._2)
          }
        }
      }
      ExpandInto(newIn._1, expandInto.target, newPattern)
    }
  }

  override def direction: Direction = Down

  override def maxIterations: Int = 10
}
