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
          } else {
            val kg = context.catalog.getKnowledgeGraph();
            val metaConceptMap: mutable.HashMap[String, Set[String]] = mutable.HashMap.empty
            metaConceptEdges.foreach(e => parseMetaConcept(kg, e, pattern, metaConceptMap))
            val newNodes = pattern.nodes.map(n =>
              if (metaConceptMap.contains(n._1)) {
                n.copy(
                  n._1,
                  PatternElement(n._1, metaConceptMap(n._1), n._2.rule))}
              else n
            )
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
    val Rules = graph.ruleDefines.keys
    metaConceptEdge.foreach(c => {
      val targetAlias = pattern.nodes(c.target).alias
      for (s <- pattern.nodes(c.source).typeNames) {
        for (t <- pattern.nodes(c.target).typeNames) {
          val spo = s + "_belongTo_" + t
          val matchedRules = Rules.filter(r => r.split('/').head.equals(spo))
          if (!metaConceptMap.contains(targetAlias)) {
            metaConceptMap(targetAlias) = Set.empty
          }
          val metaConcepts = metaConceptMap(targetAlias).++(matchedRules.map(r =>
            r.split("_belongTo_").last))
          metaConceptMap.put(targetAlias, metaConcepts)
        }
      }
    })
  }

}
