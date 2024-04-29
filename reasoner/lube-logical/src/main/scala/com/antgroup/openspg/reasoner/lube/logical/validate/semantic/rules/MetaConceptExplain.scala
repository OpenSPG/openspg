package com.antgroup.openspg.reasoner.lube.logical.validate.semantic.rules

import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock}
import com.antgroup.openspg.reasoner.lube.catalog.{SemanticPropertyGraph, SemanticRule}
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  Connection,
  GraphPattern,
  PatternElement
}
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
          val metaConceptEdges =
            pattern.edges.values.filter(edge => edge.exists(_.relTypes.contains("belongTo")))
          if (metaConceptEdges.isEmpty) {
            p
          }
          else {
            val catalog = context.catalog
            val kg = catalog.getKnowledgeGraph();
            val metaConceptMap: mutable.HashMap[String, Set[String]] = mutable.HashMap.empty
            metaConceptEdges.foreach(ce => parseMetaConcept(kg, ce, pattern, metaConceptMap))
            val newNodes = pattern.nodes.map(n =>
              if (metaConceptMap.contains(n._1)) {
                n.copy(
                  n._1,
                  PatternElement(n._1, metaConceptMap.getOrElse(n._1, n._2.typeNames), n._2.rule))
              } else {
                n
              }
            )
            val newPath = p._2.copy(graphPattern = pattern.copy(nodes = newNodes))
            (p._1, newPath)
          }
        }
        MatchBlock(dependencies, newPatterns)
      }
  }
  private def parseMetaConcept(
      graph: SemanticPropertyGraph,
      metaConceptEdge: Set[Connection],
      pattern: GraphPattern,
      metaConceptMap: mutable.HashMap[String, Set[String]]): Unit = {
    val Rules = graph.ruleDefines.keys
    metaConceptEdge.foreach(c => {
      val sourceTypes = pattern.nodes(c.source).typeNames
      val targetTypes = pattern.nodes(c.target).typeNames
      val targetAlias = pattern.nodes(c.target).alias
      for (s <- sourceTypes) {
        for (t <- targetTypes) {
          val spo = s + "_belongTo_" + t
          val matchedRules = Rules.filter(r => r.split('/').head.equals(spo))
          val expandConcept = matchedRules.map(r => r.split("_belongTo_").last)
          if (!metaConceptMap.contains(targetAlias)) {
            metaConceptMap(targetAlias) = Set.empty
          }
          metaConceptMap(targetAlias) = metaConceptMap(targetAlias).++(expandConcept.toSet)
        }
      }
    })
  }

}
