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

package com.antgroup.openspg.reasoner.lube.logical.validate.semantic.rules

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.SchemaException
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock}
import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.common.pattern.{EntityElement, PatternConnection, PatternElement}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.semantic.Explain

object ConceptExplain extends Explain {

  override def explain(implicit context: LogicalPlannerContext): PartialFunction[Block, Block] = {
    case matchBlock @ MatchBlock(dependencies, patterns) =>
      val pattern = patterns.values.head.graphPattern
      val entityElements = pattern.nodes.values.filter(_.isInstanceOf[EntityElement])
      if (entityElements.isEmpty) {
        matchBlock
      } else {
        var newNodes = pattern.nodes
        var newEdges = pattern.edges
        var newProps = pattern.properties
        for (ele <- entityElements.map(_.asInstanceOf[EntityElement])) {

          val graph = context.catalog.getGraph(Catalog.defaultGraphName).graphSchema

          val taxonOfEntityTypes = graph.getTargetType(ele.label, "belongTo", Direction.IN)
          val belongToCheck = newEdges.flatMap(x => {
            x._2.map(e => {
              if (ele.alias.equals(e.target)) {
                if (e.relTypes.size == 1 && e.relTypes.head.equals("belongTo") &&
                  taxonOfEntityTypes.contains(newNodes(x._1).typeNames.head)) {
                  true
                } else {
                  false
                }
              }
              else {
                null
              }
            }).filter(_ != null)
          })
          if (belongToCheck.nonEmpty) {
            if (belongToCheck.toSet.contains(true) && belongToCheck.to.contains(false)) {
              throw SchemaException(s"BelongTo find conflict $taxonOfEntityTypes")
            }
          }
          if (belongToCheck.isEmpty || belongToCheck.toSet.contains(false)) {
            val conceptAlias = genAlias(newNodes.keySet);
            newNodes.+=((conceptAlias, ele.copy(alias = conceptAlias)))
            newNodes.+=(
              (
                ele.alias,
                PatternElement(
                  ele.alias,
                  graph.getTargetType(ele.label, "belongTo", Direction.IN),
                  null)))
            newProps.+=((conceptAlias, Set.empty))
            newProps.+=((ele.alias, Set.apply(Constants.NODE_ID_KEY)))
            val connAlias = s"E_${conceptAlias}"
            val connection = new PatternConnection(
              connAlias,
              conceptAlias,
              Set.apply("belongTo"),
              ele.alias,
              Direction.IN,
              null)
            newEdges.+=((conceptAlias, Set.apply(connection)))
            newProps.+=((s"E_${conceptAlias}", Set.empty))
          }
        }
        val newPattern = pattern.copy(nodes = newNodes, edges = newEdges, properties = newProps)
        MatchBlock(
          dependencies,
          Map.apply((patterns.head._1, patterns.head._2.copy(graphPattern = newPattern))))
      }
  }

  private def genAlias(aliases: Set[String]): String = s"C_${aliases.size + 1}"
}
