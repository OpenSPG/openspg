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

package com.antgroup.openspg.reasoner.lube.logical.validate.semantic.rules

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock, OrderedFields, TableResultBlock}
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRProperty}
import com.antgroup.openspg.reasoner.lube.common.pattern.{GraphPattern, VariablePatternConnection}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.semantic.Explain

object NodeIdTransform extends Explain {

  /**
   * Rewrite [[Block]] tree TopDown
   *
   * @param context
   * @return
   */
  override def explain(implicit context: LogicalPlannerContext): PartialFunction[Block, Block] = {
    case tableResult: TableResultBlock => tableResultTransform(tableResult)
  }

  private def tableResultTransform(tableResultBlock: TableResultBlock): Block = {
    val matchBlock = tableResultBlock.transform[MatchBlock] {
      case (matchBlock: MatchBlock, _) => matchBlock
      case (_, list) =>
        if (list == null || list.isEmpty) {
          null
        } else {
          list.head
        }

    }
    val pattern = matchBlock.patterns.values.head.graphPattern
    val props = tableResultBlock.selectList.orderedFields.groupBy(_.name)
    val selects = new ListBuffer[IRField]
    var needModResolve = false
    val removed = new mutable.HashSet[String]()
    for (select <- tableResultBlock.selectList.orderedFields) {
      if (select.isInstanceOf[IRProperty] && props(select.name).size == 1 && select
          .asInstanceOf[IRProperty]
          .field
          .equals(Constants.NODE_ID_KEY)) {
        selects.append(transform(select.name, pattern))
        removed.add(select.name)
        needModResolve = true
      } else {
        selects.append(select)
      }
    }

    val newBlock = tableResultBlock.copy(selectList = OrderedFields(selects.toList))
    if (needModResolve) {
      newBlock.rewrite { case matchBlock @ MatchBlock(dependencies, patterns) =>
        val props = new mutable.HashMap[String, Set[String]]()
        for (prop <- pattern.properties) {
          if (removed.contains(prop._1)) {
            props.put(prop._1, Set.empty)
          } else {
            props.put(prop._1, prop._2)
          }
        }
        val selectMap = selects.filter(_.isInstanceOf[IRProperty]).groupBy(_.name)
        for (select <- selectMap) {
          val propNames =
            props(select._1) ++ (select._2.map(_.asInstanceOf[IRProperty].field).toSet)
          props.put(select._1, propNames)
        }
        val newPattern = pattern.copy(properties = props.toMap)
        val path = patterns.head._2.copy(graphPattern = newPattern)
        MatchBlock(dependencies, Map.apply((patterns.head._1 -> path)))
      }
    } else {
      newBlock
    }

  }

  private def transform(alias: String, pattern: GraphPattern): IRProperty = {
    var ir: IRProperty = null
    if (pattern.edges.contains(alias)) {
      val fixedEdges = pattern.edges(alias).filter(!_.isInstanceOf[VariablePatternConnection])
      val varEdges = pattern
        .edges(alias)
        .filter(_.isInstanceOf[VariablePatternConnection])
        .map(_.asInstanceOf[VariablePatternConnection])
        .filter(_.upper == 1)
      if (!fixedEdges.isEmpty) {
        ir = IRProperty(fixedEdges.head.alias, Constants.EDGE_FROM_ID_KEY)
      } else if (!varEdges.isEmpty) {
        ir = IRProperty(varEdges.head.alias, Constants.EDGE_FROM_ID_KEY)
      }
    } else {
      val inEdges = pattern.edges.values.flatten.filter(_.target.equals(alias))
      val fixedEdges = inEdges.filter(!_.isInstanceOf[VariablePatternConnection])
      val varEdges = inEdges
        .filter(_.isInstanceOf[VariablePatternConnection])
        .map(_.asInstanceOf[VariablePatternConnection])
        .filter(_.upper == 1)
      if (!fixedEdges.isEmpty) {
        ir = IRProperty(fixedEdges.head.alias, Constants.EDGE_TO_ID_KEY)
      } else if (!varEdges.isEmpty) {
        ir = IRProperty(varEdges.head.alias, Constants.EDGE_TO_ID_KEY)
      }
    }
    if (ir == null) {
      IRProperty(alias, Constants.NODE_ID_KEY)
    } else {
      ir
    }
  }

}
