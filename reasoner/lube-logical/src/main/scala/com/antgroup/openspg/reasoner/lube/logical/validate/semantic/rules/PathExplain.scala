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

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock, TableResultBlock}
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode, IRPath}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.semantic.Explain
import scala.collection.mutable.ListBuffer

object PathExplain extends Explain {
  override def explain(implicit context: LogicalPlannerContext): PartialFunction[Block, Block] = {
    case tableResultBlock@TableResultBlock(dependencies, selectList, asList) =>
      if (selectList.fields.isEmpty) {
        tableResultBlock
      } else {
        val pathNodes = ListBuffer[String]()
        val pathEdges = ListBuffer[String]()
        val newSelectFields = selectList.fields.map {
          case path@IRPath(_, elements) =>
            val newPathField = elements.map {
              case node@IRNode(name, fields) =>
                pathNodes.+=(name)
                node.copy(fields = fields + Constants.PROPERTY_JSON_KEY)
              case edge@IREdge(name, fields) =>
                pathEdges.+=(name)
                edge.copy(fields = fields + Constants.PROPERTY_JSON_KEY)
              case other => other
            }
            path.copy(elements = newPathField)

          case other => other
        }
        val newSelectList = selectList.copy(orderedFields = newSelectFields)
        val newTableResultBlock = TableResultBlock(dependencies, newSelectList, asList)
        newTableResultBlock.rewriteTopDown(explainMatch(pathNodes, pathEdges))
      }
  }

  private def explainMatch(pathNodes: ListBuffer[String],
                           pathEdges: ListBuffer[String]): PartialFunction[Block, Block] = {
    case matchBlock@MatchBlock(dependencies, patterns) =>
      if (patterns.isEmpty) {
        matchBlock
      } else {
        val newPatterns = patterns.map {
          p =>
            val pattern = p._2.graphPattern
            val newProperties = pattern.properties.map {
              case (key, value) =>
                if (pathNodes.contains(key) || pathEdges.contains(key)) {
                  (key, value + Constants.PROPERTY_JSON_KEY)
                } else {
                  (key, value)
                }
            }
            val newPath = p._2.copy(graphPattern = pattern.copy(properties = newProperties))
            (p._1, newPath)
        }
        MatchBlock(dependencies, newPatterns)
      }
  }
}
