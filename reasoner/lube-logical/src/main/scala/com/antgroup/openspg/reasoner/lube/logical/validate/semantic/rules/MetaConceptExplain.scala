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

import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock}
import com.antgroup.openspg.reasoner.lube.catalog.{Catalog, SemanticPropertyGraph}
import com.antgroup.openspg.reasoner.lube.common.pattern.{Connection, EntityElement, GraphPattern, PatternElement}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.semantic.Explain
import scala.collection.mutable

object MetaConceptExplain extends Explain {

  override def explain(implicit context: LogicalPlannerContext): PartialFunction[Block, Block] = {
    case matchBlock @ MatchBlock(dependencies, patterns) =>
      if (patterns.isEmpty) {
        matchBlock
      } else {
        val newPatterns = patterns.map { p =>
          val pattern = p._2.graphPattern
          val metaConceptEdges = pattern.edges.values.filter(edge =>
            edge.exists(_.relTypes.contains("belongTo")) && !edge.exists(e =>
              pattern.nodes(e.target).isInstanceOf[EntityElement]))
          if (metaConceptEdges.isEmpty) {
            p
          } else {
            val kg = context.catalog.getGraph(Catalog.defaultGraphName)
            val metaConceptMap: mutable.HashMap[String, Set[String]] = mutable.HashMap.empty
            metaConceptEdges.foreach(e => parseMetaConcept(kg, e, pattern, metaConceptMap))
            val newNodes = pattern.nodes.map(n =>
              if (metaConceptMap.contains(n._1)) {
                n.copy(n._1, PatternElement(n._1, metaConceptMap(n._1), n._2.rule))
              } else n)
            (p._1, p._2.copy(graphPattern = pattern.copy(nodes = newNodes)))
          }
        }
        MatchBlock(dependencies, newPatterns)
      }
  }

  private def parseMetaConcept(
      graph: SemanticPropertyGraph,
      metaConceptEdge: Set[Connection],
      pattern: GraphPattern,
      metaConceptMap: mutable.Map[String, Set[String]]): Unit = {
    val rules = graph.ruleDefines.keys
    if (rules.nonEmpty) {
      metaConceptEdge.foreach(c => {
        val targetAlias = pattern.nodes(c.target).alias
        for (s <- pattern.nodes(c.source).typeNames) {
          for (t <- pattern.nodes(c.target).typeNames) {
            val spo = s + "_belongTo_" + t
            val matchedRules = rules.filter(r => r.split('/').head.equals(spo))
            if (!metaConceptMap.contains(targetAlias)) {
              metaConceptMap(targetAlias) = Set.empty
            }
            val metaConcepts =
              metaConceptMap(targetAlias).++(matchedRules.map(r => r.split("_belongTo_").last))
            metaConceptMap.put(targetAlias, metaConcepts)
          }
        }
      })
    }

  }

}
