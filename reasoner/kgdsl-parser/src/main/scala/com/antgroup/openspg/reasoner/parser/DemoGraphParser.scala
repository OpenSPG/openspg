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

package com.antgroup.openspg.reasoner.parser
import java.util

import scala.collection.JavaConverters._

import com.antgroup.openspg.reasoner.KGDSLParser._
import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge
import com.antgroup.openspg.reasoner.common.graph.property.{IProperty, IVersionProperty}
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil
import com.antgroup.openspg.reasoner.parser.pattern.{
  ConceptLabelType,
  EntityLabelType,
  LabelType,
  PatternParser
}



class DemoGraphParser {
  val VERSION_KEY = "__version__"
  val KG_REASONER_PROPERTY_TYPE = "__label__"

  val patternParser: PatternParser = new PatternParser()

  def parse(
      demoGraphTxt: String): (List[Vertex[String, IProperty]], List[Edge[String, IProperty]]) = {
    val parser = new LexerInit().initKGReasonerParser(demoGraphTxt)
    parseDemoGraph(parser.demo_graph())
  }

  def parseDemoGraph(ctx: Demo_graphContext)
      : (List[Vertex[String, IProperty]], List[Edge[String, IProperty]]) = {
    var edges: List[Edge[String, IProperty]] = List.empty
    var nodes: List[Vertex[String, IProperty]] = List.empty
    ctx
      .graph_structure_body()
      .graph_structure_one_line()
      .asScala
      .foreach(oneLine => {
        oneLine.getChild(0) match {
          case c: Define_edgeContext =>
            val edge = parseDefineOneEdge(c.define_one_edge())
            edges = edges ++ List.apply(edge)
          case c: Define_vertexContext =>
            val nodeSet = parseDefineVertex(c)
            nodes = nodes ++ nodeSet
        }
      })
    (nodes, edges)
  }

  def parseDefineOneEdge(ctx: Define_one_edgeContext): Edge[String, IProperty] = {
    val s = patternParser.parseIdentifier(ctx.vertex_from().vertex_name().identifier())
    val o = patternParser.parseIdentifier(ctx.vertex_to().vertex_name().identifier())

    val labelProperties =
      Map.apply(Constants.EDGE_FROM_ID_KEY -> s, Constants.EDGE_TO_ID_KEY -> o) ++ patternParser
        .parseLabelPropertyList(ctx.label_property_list())
    val labels = patternParser.parseLabelList(ctx.label_property_list())
    var direction = Direction.OUT
    ctx.getChild(1) match {
      case _: Right_arrowContext => direction = Direction.OUT
      case _: Both_arrowContext => direction = Direction.BOTH
    }
    val edgeProperty = new EdgeProperty(labelProperties.asJava)
    val labelName = getLabel(labels)
    val version = getVersion(labelProperties)
    new Edge[String, IProperty](s, o, edgeProperty, version, direction, labelName)
  }

  def parseDefineOneVertex(ctx: Define_one_vertexContext): List[Vertex[String, IProperty]] = {
    val labelProperties = patternParser.parseLabelPropertyList(ctx.label_property_list())
    val labels = patternParser.parseLabelList(ctx.label_property_list())
    val version = getVersion(labelProperties)
    val bizId = patternParser.parseIdentifier(ctx.vertex_name().identifier())
    List.apply(generateVertex(labels, labelProperties, version, bizId))
  }

  def parseDefineVertex(ctx: Define_vertexContext): List[Vertex[String, IProperty]] = {
    ctx.getChild(0) match {
      case c: Define_multiple_vertexContext => parseDefineMultipleVertex(c)
      case c: Define_one_vertexContext => parseDefineOneVertex(c)
    }
  }

  def parseDefineMultipleVertex(
      ctx: Define_multiple_vertexContext): List[Vertex[String, IProperty]] = {
    val labels = patternParser.parseLabelList(ctx.label_property_list())
    val labelProperties = patternParser.parseLabelPropertyList(ctx.label_property_list())
    val version = getVersion(labelProperties)

    ctx.vertex_name().asScala
    ctx
      .vertex_name()
      .asScala
      .map(x => {
        val bizId = patternParser.parseIdentifier(x.identifier())
        generateVertex(labels, labelProperties, version, bizId)
      })
      .toList
  }

  protected def getLabel(labels: Set[LabelType]): String = {
    labels.head match {
      case ConceptLabelType(label, _) => label
      case EntityLabelType(label) => label
    }
  }

  protected def getVersion(labelProperties: Map[String, Object]): java.lang.Long = {
    var version = 0L
    if (labelProperties.contains(VERSION_KEY)) {
      version = java.lang.Long.parseLong(labelProperties(VERSION_KEY).toString)
    }
    version
  }

  protected def convert2VersionProperty(
      bizId: String,
      version: java.lang.Long,
      rowPropertyMap: Map[String, Object])
      : util.Map[String, util.TreeMap[java.lang.Long, AnyRef]] = {
    val propertyMap = rowPropertyMap ++ Map.apply(Constants.NODE_ID_KEY -> bizId)
    val result = new util.HashMap[String, util.TreeMap[java.lang.Long, AnyRef]]

    propertyMap.foreach(v => {
      val value = v._2
      val k = v._1
      if (result.containsKey(k)) {
        val data = result.get(k)
        data.put(version, value)
      } else {
        val data = new util.TreeMap[java.lang.Long, AnyRef]()
        data.put(version, value)
        result.put(k, data)
      }
    })
    result
  }

  protected def generateVertex(
      labels: Set[LabelType],
      labelProperties: Map[String, Object],
      version: java.lang.Long,
      bizId: String): Vertex[String, IProperty] = {
    val labelName = getLabel(labels)
    val properties = labelProperties ++ Map.apply(KG_REASONER_PROPERTY_TYPE -> labelName)
    val vertexId = IVertexId.from(bizId, labelName)
    val versionProperty: IVersionProperty =
      PropertyUtil.buildVertexProperty(
        vertexId,
        convert2VersionProperty(bizId, version, properties))
    new Vertex[String, IProperty](bizId, versionProperty)
  }

}
