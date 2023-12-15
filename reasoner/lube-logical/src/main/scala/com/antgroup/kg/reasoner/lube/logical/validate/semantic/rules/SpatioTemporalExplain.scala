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

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.KGDSLGrammarException
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.common.utils.ResourceLoader
import com.antgroup.openspg.reasoner.lube.Logging
import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock}
import com.antgroup.openspg.reasoner.lube.catalog.{Catalog, SemanticRule, TemplateSemanticRule}
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.pattern.{Connection, GraphPath, GraphPattern, LinkedPatternConnection, PatternConnection}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.semantic.Explain
import com.antgroup.openspg.reasoner.udf.{UdfMng, UdfMngFactory}
import com.antgroup.openspg.reasoner.udf.model.UdtfMeta

object SpatioTemporalExplain extends Explain with Logging {
  private val udfMng: UdfMng = UdfMngFactory.getUdfMng

  private val stTemplateMap: Map[String, String] = Map.apply(
    ("nearby", "SpatioTemporalTemplate/NearbyTemplate.vm"),
    ("within", "SpatioTemporalTemplate/WithinTemplate.vm"),
    ("intersects", "SpatioTemporalTemplate/IntersectsTemplate.vm"))

  /**
   * Rewrite [[Block]] tree TopDown
   *
   * @param context
   * @return
   */
  override def explain(implicit context: LogicalPlannerContext): PartialFunction[Block, Block] = {
    case MatchBlock(dependencies, patterns) =>
      var newGraphPathMap: Map[String, GraphPath] = Map.empty
      patterns.foreach((alias2GraphPath) => {
        val graphPath = alias2GraphPath._2
        val edgesMap: Map[String, Set[Connection]] = graphPath.graphPattern.edges
        var aliasMap: Map[String, Set[String]] = Map.empty
        graphPath.graphPattern.nodes.foreach((entry) => {
          val alias = entry._1
          val typeSet = entry._2.typeNames
          aliasMap += (alias -> typeSet)
        })

        var newEdgesMap: Map[String, Set[Connection]] = Map.empty
        if (null != edgesMap) {
          for (edgeEntry <- edgesMap) {
            val key = edgeEntry._1
            val patternConnectionSet = edgeEntry._2
            var replaceMap: Map[Connection, Connection] = Map.empty
            for (patternConnection <- patternConnectionSet) {
              patternConnection match {
                case linkedPC: LinkedPatternConnection =>
                  if (!isLinkedFuncInUdtf(linkedPC.funcName)) {
                    val newAlias = linkedPC.relTypes.head
                    registerInSPG(newAlias, linkedPC, aliasMap)
                    // generate new PatternConnection
                    val newPatternConnection = new PatternConnection(
                      linkedPC.alias,
                      linkedPC.source,
                      Set.apply(newAlias),
                      linkedPC.target,
                      linkedPC.direction,
                      linkedPC.rule,
                      linkedPC.limit)
                    replaceMap += (patternConnection -> newPatternConnection)
                    addEdgeToGraph(newPatternConnection, aliasMap)
                  } else {
                    val startType = aliasMap(linkedPC.source).head
                    val endType = aliasMap(linkedPC.target).head
                    context.catalog
                      .getGraph(Catalog.defaultGraphName)
                      .addEdge(
                        startType,
                        linkedPC.relTypes.head,
                        endType,
                        linkedPC.direction,
                        Set.empty,
                        true)
                  }

                case _ =>
              }
            }
            val newPatternConnectionSet: mutable.Set[Connection] =
              mutable.Set(patternConnectionSet.toSeq: _*)
            for ((oldPC, newPC) <- replaceMap) {
              newPatternConnectionSet -= oldPC
              newPatternConnectionSet += newPC
            }
            newEdgesMap += (key -> newPatternConnectionSet.toSet)
          }
        }
        val newGraphPattern: GraphPattern = graphPath.graphPattern.copy(edges = newEdgesMap)
        val newGraphPath = graphPath.copy(graphPattern = newGraphPattern)
        newGraphPathMap += (alias2GraphPath._1 -> newGraphPath)
      })
      // rewrite current match block
      MatchBlock(dependencies, newGraphPathMap)
  }

  private def addEdgeToGraph(pc: PatternConnection, aliasMap: Map[String, Set[String]])(implicit
      context: LogicalPlannerContext): Unit = {
    val srcTypes = aliasMap(pc.source)
    val dstTypes = aliasMap(pc.target)
    val graph = context.catalog.getGraph(Catalog.defaultGraphName)
    for (s <- srcTypes) {
      for (o <- dstTypes) {
        graph.addEdge(s, pc.relTypes.head, o, pc.direction, Set.empty, false)
      }
    }
  }

  private def isLinkedFuncInUdtf(funcName: String): Boolean = {
    val allUdtfMeta: List[UdtfMeta] = asScalaBufferConverter(udfMng.getAllUdtfMeta).asScala.toList
    if (null == allUdtfMeta) {
      return false
    }
    if (allUdtfMeta.map(udtfMeta => udtfMeta.getName).contains(funcName)) {
      return true
    }
    false
  }

  private def registerInSPG(
      newAlias: String,
      linkedPC: LinkedPatternConnection,
      aliasMap: Map[String, Set[String]])(implicit context: LogicalPlannerContext): Unit = {
    val funcName = linkedPC.funcName
    if (!stTemplateMap.contains(funcName)) {
      logger.warn("funcName=%s is not support in SpatioTemporalExplain".format(funcName))
      return
    }

    val templateString = ResourceLoader.loadResourceFile(stTemplateMap(funcName))
    val params: Map[String, String] = parseLinkedPC(newAlias, linkedPC, aliasMap)
    val templateRule: SemanticRule = TemplateSemanticRule(templateString, params)

    var srcTypes = aliasMap(linkedPC.source)
    var dstTypes = aliasMap(linkedPC.target)
    if (Direction.IN.equals(linkedPC.direction)) {
      srcTypes = aliasMap(linkedPC.target)
      dstTypes = aliasMap(linkedPC.source)
    }
    for (s <- srcTypes) {
      for (o <- dstTypes) {
        val spo = s + "_" + newAlias + "_" + o
        context.catalog.getGraph(Catalog.defaultGraphName).registerRule(spo, templateRule)
      }
    }
  }

  private def parseLinkedPC(
      newAlias: String,
      linkedPC: LinkedPatternConnection,
      aliasMap: Map[String, Set[String]]): Map[String, String] = {

    val funcName = linkedPC.funcName
    val sExpr: Expr = linkedPC.params(0)
    val oExpr: Expr = linkedPC.params(1)
    if (!sExpr.isInstanceOf[UnaryOpExpr]) {
      throw new KGDSLGrammarException(
        "the first parameter in %s should like A.property".format(funcName))
    }
    if (!oExpr.isInstanceOf[UnaryOpExpr]) {
      throw new KGDSLGrammarException(
        "the second parameter in %s should like A.property".format(funcName))
    }

    val sAlias = sExpr.asInstanceOf[UnaryOpExpr].arg.asInstanceOf[Ref].refName
    val sType = aliasMap(sAlias).mkString("|")
    val sProperty = sExpr.asInstanceOf[UnaryOpExpr].name.asInstanceOf[GetField].fieldName
    val oAlias = oExpr.asInstanceOf[UnaryOpExpr].arg.asInstanceOf[Ref].refName
    val oType = aliasMap(oAlias).mkString("|")
    val oProperty = oExpr.asInstanceOf[UnaryOpExpr].name.asInstanceOf[GetField].fieldName
    var paramsMap: Map[String, String] = Map.empty
    paramsMap += ("sType" -> sType)
    paramsMap += ("oType" -> oType)
    paramsMap += ("newAlias" -> newAlias)
    paramsMap += ("sAlias" -> "s")
    paramsMap += ("oAlias" -> "o")
    paramsMap += ("sProperty" -> sProperty)
    paramsMap += ("originSProperty" -> sProperty)
    paramsMap += ("oProperty" -> oProperty)
    paramsMap += ("originOProperty" -> oProperty)

    var distanceStr: String = "0"
    if (funcName.equals("nearby")) {
      val paramSize = 3
      if (linkedPC.params.size != paramSize) {
        throw new KGDSLGrammarException("nearby function should have 3 parameters")
      }
      val distanceExpr: Expr = linkedPC.params(2)
      if (!(distanceExpr.isInstanceOf[VLong] || distanceExpr.isInstanceOf[VDouble])) {
        throw new KGDSLGrammarException(
          "the third parameter in %s should be a number".format(funcName))
      }
      distanceExpr match {
        case long: VLong =>
          distanceStr = long.value
        case double: VDouble =>
          distanceStr = double.value
        case _ =>
      }

      val distance = distanceStr.toDouble
      if (distance < 0) {
        throw new KGDSLGrammarException(
          "the third parameter=%s in %s should be positive".format(distanceStr, funcName))
      }
    }
    paramsMap += ("distance" -> distanceStr)
    paramsMap
  }

}
