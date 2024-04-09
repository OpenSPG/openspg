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

package com.antgroup.openspg.reasoner.parser.pattern

import scala.collection.JavaConverters._
import scala.collection.mutable

import com.antgroup.openspg.reasoner.KGDSLParser._
import com.antgroup.openspg.reasoner.common.exception.KGDSLGrammarException
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.block.{MatchBlock, SourceBlock}
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode, KG}
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.rule.{LogicRule, Rule}
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser

/**
 * This class is primarily used for temporarily storing declaration information of nodes and edges.
 *
 * @param alias
 * @param typeNames
 * @param rule
 */
class PatternDeclarationInfo(val alias: String, val typeNames: Set[LabelType], val rule: Expr) {}

sealed trait LabelType

case class EntityLabelType(label: String) extends LabelType

case class ConceptLabelType(label: String, id: String) extends LabelType

/**
 * This class is only used to store information on the linked edge
 *
 * @param alias
 * @param typeNames
 * @param rule
 * @param linkedEdge
 */
class LinkedPatternDeclarationInfo(
    alias: String,
    typeNames: Set[LabelType],
    rule: Expr,
    val linkedEdge: FunctionExpr)
    extends PatternDeclarationInfo(alias, typeNames, rule) {}

/**
 * This class is an ANTLR parsing class used for parsing DSL.
 */
class PatternParser extends Serializable {

  /**
   * This is an expression parsing object.
   */
  val exprParser = new RuleExprParser()

  /**
   * The default name given when no path name is specified by the user.
   */
  val defaultPathName = "unresolved_default_path"

  /**
   * Thr default alias name prefix
   */
  val defaultNamePrefix = "anonymous_"
  // const
  val PROPERTY_LABEL_KEY = "__label_list__"
  val PROPERTY_START_FLAG_KEY = "__start__"
  val PROPERTY_PRE_NODE_LIMIT_KEY = "PRE_NODE_LIMIT"
  val PROPERTY_OPTIONAL_KEY = "__optional__"
  val VAR_REPEAT_KEY = "repeat"

  /**
   * To prevent alias conflicts,
   * the system increments the alias number based on the number of anonymous users.
   */
  var defaultNameNum = 1

  /**
   * Retrieve the ID assigned to an anonymous alias
   * @return
   */
  def getDefaultAliasNum: Int = {
    defaultNameNum = defaultNameNum + 1
    defaultNameNum
  }

  /**
   * get default alias name
   * @return
   */
  def getDefaultName: String = {
    defaultNamePrefix + getDefaultAliasNum
  }

  /**
   * get default edge name
   * @param s start entity
   * @param o end entity
   * @return
   */
  def getDefaultEdgeName(s: String, o: String): String = {
    s + "_" + o + "_" + getDefaultAliasNum
  }

  // All functions below are parsing and processing functions for ANTLR

  def parseGraphStructureDefine(
      ctx: Graph_structure_defineContext,
      head: Element = null,
      predicate: PredicateElement = null): MatchBlock = {
    var pathMaps = Map[String, GraphPath]()

    if (ctx != null) {
      val paths: Set[GraphPath] = ctx.getChild(0) match {
        case c: Graph_structure_bodyContext => parseGraphStructureBody(c, head, predicate)
        case c: Path_pattern_listContext => parsePathPatternList(c, head, predicate)
        case _ => throw new UnsupportedOperationException(ctx.getChild(0).toString + " not impl")
      }
      paths.foreach(x => {
        if (pathMaps.contains(x.pathName)) {
          pathMaps += (x.pathName -> mergeGraphPath(x, pathMaps(x.pathName)))
        } else {
          pathMaps += (x.pathName -> x)
        }
      })
    }

    if (pathMaps.isEmpty) {
      if (head == null) {
        throw new KGDSLGrammarException("Must contain graph pattern info")
      }
      pathMaps += (defaultPathName -> GraphPath(
        defaultPathName,
        GraphPattern(
          if (head != null && head.alias != null) head.alias else null,
          Map.apply((head.alias -> head)),
          Map.empty,
          Map.empty),
        false))
    }
    parseSourceAndMatchBlock(Map.empty, pathMaps)
  }

  def parseSourceAndMatchBlock(
      refFieldsMap: Map[String, Set[String]],
      patterns: Map[String, GraphPath]): MatchBlock = {
    val nodesProp = new mutable.HashMap[String, IRNode]()
    val edgesProp = new mutable.HashMap[String, IREdge]()
    patterns.foreach(path => {
      path._2.graphPattern.nodes.foreach(node => {
        nodesProp += (node._1 -> IRNode(node._1, Set.empty))
      })
      path._2.graphPattern.edges.foreach(edgeSet => {
        edgeSet._2.foreach(edge => {
          edgesProp += (edge.alias -> IREdge(edge.alias, Set.empty))
        })
      })
    })

    val patternMaps = patterns.map(pattern => {
      val nodeMaps = pattern._2.graphPattern.nodes.keySet
        .map(nodeAlias => {
          if (!nodesProp.contains(nodeAlias)) {
            nodesProp += (nodeAlias -> IRNode(nodeAlias, Set.empty))
          }
          if (refFieldsMap.contains(nodeAlias)) {
            val irNode = nodesProp(nodeAlias).copy(fields =
              nodesProp(nodeAlias).fields ++ refFieldsMap(nodeAlias))
            nodesProp.put(nodeAlias, irNode)
            nodeAlias -> refFieldsMap(nodeAlias)
          } else {
            nodeAlias -> Set.empty[String]
          }
        })
        .filter(_ != null)
        .toMap
      val edgeMaps = pattern._2.graphPattern.edges
        .map(edgeSet => {
          edgeSet._2
            .map(edge => {
              if (!edgesProp.contains(edge.alias)) {
                edgesProp +=
                  (edge.alias -> IREdge(edge.alias, Set.empty))
              }
              if (refFieldsMap.contains(edge.alias)) {
                val irEdge = edgesProp(edge.alias).copy(fields =
                  edgesProp(edge.alias).fields ++ refFieldsMap(edge.alias))
                edgesProp.put(edge.alias, irEdge)
                edge.alias -> refFieldsMap(edge.alias)
              } else {
                edge.alias -> Set.empty[String]
              }
            })
            .filter(_ != null)
        })
        .flatten
      val updatedGraphPattern =
        pattern._2.graphPattern.copy(properties = (nodeMaps ++ edgeMaps))
      pattern._1 -> pattern._2.copy(graphPattern = updatedGraphPattern)
    })

    MatchBlock(List.apply(SourceBlock(KG(nodesProp.toMap, edgesProp.toMap))), patternMaps)
  }

  def parseGraphStructureBody(
      ctx: Graph_structure_bodyContext,
      head: Element,
      predicate: PredicateElement): Set[GraphPath] = {
    val patterns: List[GraphPath] = ctx
      .graph_structure_one_line()
      .asScala
      .toList
      .map(x => {
        parseGraphStructureOneLine(x)
      })
    // merge to one graph
    var nodeSet = Map[String, Element]()
    if (head != null && head.alias != null && head.typeNames != null) {
      nodeSet += (head.alias -> head)
    }
    if (predicate != null) {
      nodeSet += (predicate.target.alias ->
        PatternElement(predicate.target.alias, predicate.target.typeNames, null))
    }
    var realHead = head
    patterns.foreach(x =>
      if (x.graphPattern.nodes.nonEmpty) {
        nodeSet.++=(x.graphPattern.nodes)
        if (x.graphPattern.rootAlias != null) {
          realHead = x.graphPattern.nodes(x.graphPattern.rootAlias)
        } else if (realHead != null && realHead.typeNames != null && realHead.alias == null) {
          x.graphPattern.nodes.foreach(n =>
            if (n._2.typeNames.intersect(realHead.typeNames).nonEmpty) {
              realHead = n._2
            })
        }
      })
    var topology = Map[String, Set[Connection]]()
    patterns.foreach(x =>
      if (x.graphPattern.edges.nonEmpty) {
        for (node <- x.graphPattern.edges.keySet) {
          var patternEle = node
          if (nodeSet.contains(node)) {
            patternEle = nodeSet(node).alias
          }
          val edges = x.graphPattern.edges(node)
          val newEdges = edges.map(k => {
            var newEdge = k
            if (nodeSet.contains(newEdge.source)) {
              newEdge = newEdge.update(nodeSet(newEdge.source).alias, newEdge.target)
            }
            if (nodeSet.contains(newEdge.target)) {
              newEdge = newEdge.update(newEdge.source, nodeSet(k.target).alias)
            }
            newEdge
          })
          if (topology.contains(patternEle)) {
            topology += (patternEle -> topology(patternEle).union(newEdges))
          } else {
            topology += (patternEle -> x.graphPattern.edges(node))
          }
        }
      })
    Set.apply(
      GraphPath(
        defaultPathName,
        GraphPattern(
          if (realHead == null || realHead.alias == null) null else realHead.alias,
          nodeSet,
          topology,
          Map.empty),
        false))
  }

  def parseGraphStructureOneLine(ctx: Graph_structure_one_lineContext): GraphPath = {
    ctx.getChild(0) match {
      case c: Define_edgeContext => parseDefineEdge(c)
      case c: Define_vertexContext => parseDefineVertex(c)
    }
  }

  def parseDefineEdge(ctx: Define_edgeContext): GraphPath = {
    parseDefineOneEdge(ctx.define_one_edge())
  }

  def parseDefineOneEdge(ctx: Define_one_edgeContext): GraphPath = {
    val s = createEleWithName(ctx.vertex_from().getText)
    val o = createEleWithName(ctx.vertex_to().getText)

    val labelProperties = parseLabelPropertyList(ctx.label_property_list())
    val labels = parseLabelList(ctx.label_property_list())
    var edgeName = getDefaultEdgeName(ctx.vertex_from().getText, ctx.vertex_to().getText)
    if (ctx.edge_name() != null && !ctx.edge_name().isEmpty) {
      edgeName = ctx.edge_name().getText
    }
    var direction = Direction.OUT
    ctx.getChild(1) match {
      case _: Right_arrowContext => direction = Direction.OUT
      case _: Both_arrowContext => direction = Direction.BOTH
    }
    var limit = -1
    if (labelProperties.contains(PROPERTY_PRE_NODE_LIMIT_KEY)) {
      limit = Integer.valueOf(labelProperties(PROPERTY_PRE_NODE_LIMIT_KEY).toString)
    }
    var isOptional = false;
    if (labelProperties.contains(PROPERTY_OPTIONAL_KEY)) {
      isOptional = true;
    }
    var p: Connection = new PatternConnection(
      edgeName,
      s.alias,
      labels.map(x =>
        x match {
          case EntityLabelType(label) => label
          case ConceptLabelType(_, _) =>
            throw new KGDSLGrammarException("edge can not use concept label")
        }),
      o.alias,
      direction,
      null,
      limit,
      true,
      isOptional)
    if (ctx.repeat_time() != null && !ctx.repeat_time().isEmpty) {
      p = VariablePatternConnection(
        p.alias,
        p.source,
        p.relTypes,
        p.target,
        p.direction,
        p.asInstanceOf[PatternConnection].rule,
        p.limit,
        parseLowerBound(ctx.repeat_time.lower_bound()),
        parseUpperBound(ctx.repeat_time().upper_bound()))
    }
    GraphPath(
      "",
      GraphPattern(null, Map.empty, Map.apply(s.alias -> Set.apply(p)), Map.empty),
      false)
  }

  def parseLowerBound(ctx: Lower_boundContext): Integer = {
    Integer.valueOf(ctx.getText)
  }

  def parseUpperBound(ctx: Upper_boundContext): Integer = {
    Integer.valueOf(ctx.getText)
  }

  def getRepeatCondition(lower: Integer, upper: Integer): Expr = {
    BinaryOpExpr(
      BAnd,
      BinaryOpExpr(BGreaterThan, Ref(VAR_REPEAT_KEY), VLong(lower.toString)),
      BinaryOpExpr(BSmallerThan, Ref(VAR_REPEAT_KEY), VLong(upper.toString)))
  }

  def createEleWithName(name: String): PatternElement = {
    PatternElement(name, Set.empty, null)
  }

  def parseDefineVertex(ctx: Define_vertexContext): GraphPath = {
    ctx.getChild(0) match {
      case c: Define_multiple_vertexContext => parseDefineMultipleVertex(c)
      case c: Define_one_vertexContext => parseDefineOneVertex(c)
    }
  }

  def parseLabelList(ctx: Label_property_listContext): Set[LabelType] = {
    var conceptTypeNum = 0
    var entityTypeNum = 0

    var labels = Set[LabelType]()
    if (null == ctx) {
      return labels
    }
    ctx
      .label_name()
      .asScala
      .foreach(x => {
        val t = parseLabelName(x)
        t match {
          case ConceptLabelType(_, _) => conceptTypeNum = conceptTypeNum + 1
          case EntityLabelType(_) => entityTypeNum = entityTypeNum + 1
        }
        labels += t
      })

    if (conceptTypeNum > 2) {
      throw new KGDSLGrammarException("Node pattern only one concept type")
    }
    if (conceptTypeNum != 0 && entityTypeNum != 0) {
      throw new KGDSLGrammarException("Node pattern can not mix use node type and concept")
    }
    labels
  }

  def parseLabelPropertyList(ctx: Label_property_listContext): Map[String, Object] = {
    var propertyMap = Map[String, Object]()
    if (null == ctx) {
      return propertyMap
    }
    for (i <- 0 until ctx.property_key().size()) {
      val keyName = ctx.property_key(i).getText
      propertyMap += (keyName -> parsePropertyValue(ctx.property_value(i)))
    }
    propertyMap
  }

  def parsePropertyValue(ctx: Property_valueContext): Object = {
    ctx.getChild(0) match {
      case c: OC_NumberLiteralContext => parseUNumericLiteral(c)
      case c: Character_string_literalContext => exprParser.parseCharacterStrLiteral(c).value
      case x => x.getText
    }
  }

  def parseUNumericLiteral(context: OC_NumberLiteralContext): Object = {
    context.getChild(0) match {
      case c: OC_DoubleLiteralContext =>
        java.lang.Double.valueOf(c.getText)
      case c: OC_IntegerLiteralContext =>
        java.lang.Integer.valueOf(c.getText)
    }
  }

  def parseElementInfo(alias: String, labels: Set[LabelType]): Element = {
    var conceptLabel: ConceptLabelType = null
    var typeLabels = Set[String]()
    labels.foreach {
      case c: ConceptLabelType => conceptLabel = c
      case c: EntityLabelType => typeLabels += c.label
    }
    if (conceptLabel == null) {
      PatternElement(alias, typeLabels, null)
    } else {
      EntityElement(conceptLabel.id, conceptLabel.label, alias)
    }
  }

  def parseDefineOneVertex(ctx: Define_one_vertexContext): GraphPath = {
    val labelProperties = parseLabelPropertyList(ctx.label_property_list())
    val labels = parseLabelList(ctx.label_property_list())
    val ele = parseElementInfo(ctx.vertex_name().getText, labels)

    if (labelProperties.contains(PROPERTY_START_FLAG_KEY)) {
      GraphPath(
        "",
        GraphPattern(ele.alias, Map.apply((ele.alias -> ele)), Map.empty, Map.empty),
        false)
    } else {
      GraphPath(
        "",
        GraphPattern(null, Map.apply((ele.alias -> ele)), Map.empty, Map.empty),
        false)
    }
  }

  def parseDefineMultipleVertex(ctx: Define_multiple_vertexContext): GraphPath = {
    val labels = parseLabelList(ctx.label_property_list())
    var nodes = Map[String, Element]()
    ctx
      .vertex_name()
      .asScala
      .foreach(x => nodes += (x.getText -> parseElementInfo(x.getText, labels)))
    GraphPath("", GraphPattern(null, nodes, Map.empty, Map.empty), false)
  }

  def parsePathPatternList(
      ctx: Path_pattern_listContext,
      head: Element,
      predicate: PredicateElement): Set[GraphPath] = {
    var pathList = Set[GraphPath]()
    var nodes = Map[String, Element]()
    if (head != null && head.alias != null && head.typeNames != null) {
      nodes += (head.alias -> head)
    }
    if (predicate != null) {
      nodes += (predicate.target.alias ->
        PatternElement(predicate.target.alias, predicate.target.typeNames, null))
    }
    ctx
      .path_pattern()
      .asScala
      .foreach(x => {
        val path = parsePathPattern(x, head)
        pathList += path
        if (path.graphPattern.nodes != null && path.graphPattern.nodes.nonEmpty) {
          for (node <- path.graphPattern.nodes) {
            if (nodes.contains(node._1)) {
              nodes += (node._1 -> mergePatternElement(node._2, nodes(node._1)))
            } else {
              nodes += (node._1 -> node._2)
            }
          }
        }
      })
    var retPathList = Set[GraphPath]()
    for (path <- pathList) {
      var nodesSet = Map[String, Element]()
      var topology = Map[String, Set[Connection]]()
      for (node <- path.graphPattern.nodes) {
        nodesSet += (node._1 -> nodes(node._1))
      }
      for (ele <- path.graphPattern.edges.keySet) {
        val edges = path.graphPattern
          .edges(ele)
          .map(e => e.update(nodesSet(e.source).alias, nodesSet(e.target).alias))
        topology += (ele -> edges)
      }

      retPathList += GraphPath(
        path.pathName,
        GraphPattern(path.graphPattern.rootAlias, nodesSet, topology, Map.empty),
        path.optional)
    }
    retPathList
  }

  def parsePathPattern(ctx: Path_patternContext, head: Element): GraphPath = {
    var pathName = defaultPathName
    if (ctx.path_variable() != null && !ctx.path_variable().isEmpty) {
      pathName = ctx.path_variable().getText
    }
    var optionalPath = false
    if (ctx.path_condition() != null && !ctx.path_condition().isEmpty) {
      optionalPath = true
    }
    val graphPath = parsePathPatternExpression(ctx.path_pattern_expression())
    var rootAlias = graphPath.graphPattern.rootAlias
    if (head != null) {
      if (head.alias != null) {
        if (graphPath.graphPattern.nodes.contains(head.alias)) {
          rootAlias = head.alias
        }
      } else {
        if (head.typeNames.nonEmpty) {
          for (node <- graphPath.graphPattern.nodes) {
            if (node._2.typeNames.intersect(head.typeNames).nonEmpty) {
              rootAlias = node._1
            }
          }
        }
      }
    }
    GraphPath(
      pathName,
      GraphPattern(
        rootAlias,
        graphPath.graphPattern.nodes,
        graphPath.graphPattern.edges,
        Map.empty),
      optionalPath)
  }

  def mergeGraphPath(a: GraphPath, b: GraphPath): GraphPath = {
    var nodes = Map[String, Element]()
    for (node <- a.graphPattern.nodes) {
      nodes += node
    }
    for (node <- b.graphPattern.nodes) {
      if (nodes.contains(node._1)) {
        nodes += (node._1 -> mergePatternElement(nodes(node._1), node._2))
      } else {
        nodes += (node._1 -> node._2)
      }
    }
    var topology = Map[String, Set[Connection]]()
    for (ele <- a.graphPattern.edges.keySet) {
      val node = nodes(ele)
      var edges = Set[Connection]()
      if (topology.contains(node.alias)) {
        edges = edges.union(topology(node.alias))
      }
      for (e <- a.graphPattern.edges(ele)) {
        edges += e.update(nodes(e.source).alias, nodes(e.target).alias)
      }
      topology += (ele -> edges)
    }
    for (ele <- b.graphPattern.edges.keySet) {
      val node = nodes(ele)
      var edges = Set[Connection]()
      if (topology.contains(node.alias)) {
        edges = edges.union(topology(node.alias))
      }
      for (e <- b.graphPattern.edges(ele)) {
        edges += e.update(nodes(e.source).alias, nodes(e.target).alias)
      }
      topology += (ele -> edges)
    }

    GraphPath(
      if (a.pathName == null || a.pathName.isEmpty) b.pathName else a.pathName,
      GraphPattern(
        if (a.graphPattern.rootAlias == null) b.graphPattern.rootAlias
        else a.graphPattern.rootAlias,
        nodes,
        topology,
        Map.empty),
      a.optional || b.optional)
  }

  def parsePathPatternExpression(ctx: Path_pattern_expressionContext): GraphPath = {
    val paths = ctx.element_pattern().asScala.map(x => parseElementPattern(x)).toList
    // merge path
    var mergedPath = paths.head
    for (i <- 1 until paths.size) {
      mergedPath = mergeGraphPath(mergedPath, paths(i))
    }
    mergedPath
  }

  def mergeEntityAndPatternElement(a: EntityElement, b: PatternElement): Element = {
    if (b.typeNames != null && b.typeNames.nonEmpty) {
      throw new KGDSLGrammarException(
        "PatternElement can not merge " + a.toString + " "
          + a.getClass.getName + " and " + b.toString + " " + b.getClass.getName)
    }
    a
  }

  def mergePatternElement(a: Element, b: Element): Element = {
    var rule = a.rule
    if (rule != null && b.rule != null) {
      rule.addDependency(b.rule)
    }
    if (rule == null) {
      rule = b.rule
    }

    a match {
      case pa: PatternElement =>
        b match {
          case c: PatternElement =>
            PatternElement(
              if (pa.alias.isEmpty) c.alias else pa.alias,
              pa.typeNames ++ c.typeNames,
              rule)
          case c: EntityElement =>
            mergeEntityAndPatternElement(c, pa)
        }
      case pa: EntityElement =>
        b match {
          case c: PatternElement =>
            mergeEntityAndPatternElement(pa, c)
          case c: EntityElement => c
        }
    }
  }

  def parseElementPattern(ctx: Element_patternContext): GraphPath = {
    var nodes = Map[String, Element]()
    var edges = Set[Connection]()
    ctx.getChild(0) match {
      case c: Node_patternContext =>
        val node = parseNodePattern(c)
        nodes += (node.alias -> node)
      case c: One_edge_patternContext =>
        val pathEdges = parseOneEdgePattern(c)
        pathEdges.foreach(x => {
          if (nodes.contains(x._1.alias)) {
            val mergedNode = mergePatternElement(nodes(x._1.alias), x._1)
            nodes += (x._1.alias -> mergedNode)
          } else {
            nodes += (x._1.alias -> x._1)
          }

          if (nodes.contains(x._2.alias)) {
            val mergedNode = mergePatternElement(nodes(x._2.alias), x._2)
            nodes += (x._2.alias -> mergedNode)
          } else {
            nodes += (x._2.alias -> x._2)
          }
          edges += x._3
        })

    }
    var topology = Map[String, Set[Connection]]()
    for (edge <- edges) {
      var newEdge = edge
      val sourceName = edge.source
      if (nodes.contains(sourceName)) {
        newEdge = newEdge.update(nodes(sourceName).alias, newEdge.target)
      }

      val targetName = edge.target
      if (nodes.contains(targetName)) {
        newEdge = newEdge.update(newEdge.source, nodes(targetName).alias)
      }

      if (topology.contains(newEdge.source)) {
        topology += (newEdge.source -> topology(newEdge.source).union(Set.apply(newEdge)))
      } else {
        topology += (newEdge.source -> Set.apply(newEdge))
      }
    }
    GraphPath(defaultPathName, GraphPattern(null, nodes, topology, Map.empty), false)
  }

  def parseOneEdgePattern(
      ctx: One_edge_patternContext): Set[Tuple3[Element, Element, Connection]] = {
    val edges = mutable.HashSet[Tuple3[Element, Element, Connection]]()
    var startEle = parseNodePattern(ctx.node_pattern(0))
    for (i <- 0 until ctx.edge_pattern().size()) {
      val endEle = parseNodePattern(ctx.node_pattern(i + 1))
      val edge = parseEdgePattern(ctx.edge_pattern(i))
      edges.add(Tuple3(startEle, endEle, edge.update(startEle.alias, endEle.alias)))
      startEle = endEle
    }
    edges.toSet
  }

  def parseGraphPatternQuantifier(
      ctx: Graph_pattern_quantifierContext): Tuple3[Boolean, Integer, Integer] = {
    if (ctx == null || ctx.isEmpty) {
      Tuple3(false, null, null)
    } else {
      var isOptional = false
      if (ctx.question_mark() != null && !ctx.question_mark().isEmpty) {
        isOptional = true
      }
      if (ctx.quantifier() != null && !ctx.quantifier().isEmpty) {
        val lowerBound = parseLowerBound(ctx.quantifier().lower_bound())
        val upperBound = parseUpperBound(ctx.quantifier().upper_bound())
        Tuple3(isOptional, lowerBound, upperBound)
      } else {
        Tuple3(isOptional, null, null)
      }
    }

  }

  def parseEdgePattern(ctx: Edge_patternContext): Connection = {
    val optionalAndRepeat = parseGraphPatternQuantifier(ctx.graph_pattern_quantifier())
    var edge = ctx.getChild(0) match {
      case c: Full_edge_patternContext => parseFullEdgePattern(c, optionalAndRepeat._1)
      case c: Abbreviated_edge_patternContext =>
        val direction = c.getChild(0) match {
          case _: Right_arrowContext => Direction.OUT
          case _: Left_arrowContext => Direction.IN
          case _ => Direction.BOTH
        }
        new PatternConnection(
          "edge_" + getDefaultName,
          null,
          Set.empty,
          null,
          direction,
          null,
          -1,
          true,
          optionalAndRepeat._1)
    }
    if (optionalAndRepeat._2 != null) {
      VariablePatternConnection(
        edge.alias,
        edge.source,
        edge.relTypes,
        edge.target,
        edge.direction,
        edge.asInstanceOf[PatternConnection].rule,
        edge.limit,
        optionalAndRepeat._2.intValue(),
        optionalAndRepeat._3.intValue())
    } else {
      edge
    }

  }

  def parseFullEdgePattern(ctx: Full_edge_patternContext, isOptional: Boolean): Connection = {
    ctx.getChild(0) match {
      case c: Full_edge_pointing_rightContext =>
        parseEdgeInfo(
          c.element_pattern_declaration_and_filler(),
          c.edge_pattern_pernodelimit_clause(),
          Direction.OUT,
          isOptional)
      case c: Full_edge_pointing_leftContext =>
        parseEdgeInfo(
          c.element_pattern_declaration_and_filler(),
          c.edge_pattern_pernodelimit_clause(),
          Direction.IN,
          isOptional)
      case c: Full_edge_any_directionContext =>
        parseEdgeInfo(
          c.element_pattern_declaration_and_filler(),
          c.edge_pattern_pernodelimit_clause(),
          Direction.BOTH,
          isOptional)
    }
  }

  def convertTypeNames2StrSet(typeNames: Set[LabelType]): Set[String] = {
    typeNames.map {
      case ConceptLabelType(label, id) =>
        throw new KGDSLGrammarException("edge not support concept " + label + "/" + id)
      case EntityLabelType(name) =>
        name
    }
  }

  def parseEdgeInfo(
      dec: Element_pattern_declaration_and_fillerContext,
      limitClause: Edge_pattern_pernodelimit_clauseContext,
      direction: Direction,
      isOptional: Boolean): Connection = {
    val declInfo = parseElePatternAndFilter(dec)
    var limit = -1
    if (limitClause != null && !limitClause.isEmpty) {
      limit = Integer.valueOf(limitClause.oC_IntegerLiteral().getText)
    }
    var rule: Rule = null
    if (declInfo.rule != null) {
      rule = LogicRule(declInfo.alias + "_where", declInfo.alias + " where clause", declInfo.rule)
    }
    if (declInfo.isInstanceOf[LinkedPatternDeclarationInfo]) {
      val linkedDeclInfo = declInfo.asInstanceOf[LinkedPatternDeclarationInfo]
      val linkedTypes = convertTypeNames2StrSet(linkedDeclInfo.typeNames)
      return LinkedPatternConnection(
        linkedDeclInfo.alias,
        null,
        linkedTypes,
        linkedDeclInfo.linkedEdge.name,
        linkedDeclInfo.linkedEdge.funcArgs,
        null,
        direction,
        null,
        limit)
    }
    val typeNames = convertTypeNames2StrSet(declInfo.typeNames)
    new PatternConnection(
      declInfo.alias,
      null,
      typeNames,
      null,
      direction,
      rule,
      limit,
      true,
      isOptional)
  }

  def parseNodePattern(ctx: Node_patternContext): Element = {
    val declarationInfo: PatternDeclarationInfo =
      parseElePatternAndFilter(ctx.element_pattern_declaration_and_filler())
    if (declarationInfo.isInstanceOf[LinkedPatternDeclarationInfo]) {
      throw new KGDSLGrammarException("Node pattern can not be expr")
    }
    // check concept type is only one
    val rule: Rule = if (declarationInfo.rule != null) {
      LogicRule(
        declarationInfo.alias + "_where",
        declarationInfo.alias + " where clause",
        declarationInfo.rule)
    } else {
      null
    }
    var conceptTypeNum = 0
    var entityTypeNum = 0
    declarationInfo.typeNames.foreach {
      case ConceptLabelType(_, _) => conceptTypeNum = conceptTypeNum + 1
      case EntityLabelType(_) => entityTypeNum = entityTypeNum + 1
    }
    if (conceptTypeNum > 2) {
      throw new KGDSLGrammarException("Node pattern only one concept type")
    }
    if (conceptTypeNum != 0 && entityTypeNum != 0) {
      throw new KGDSLGrammarException("Node pattern can not mix use node type and concept")
    }
    if (conceptTypeNum == 1) {
      val conceptInstance = declarationInfo.typeNames.head.asInstanceOf[ConceptLabelType]
      EntityElement(conceptInstance.id, conceptInstance.label, declarationInfo.alias)
    } else {
      PatternElement(
        declarationInfo.alias,
        convertTypeNames2StrSet(declarationInfo.typeNames),
        rule)
    }
  }

  def parseElePatternAndFilter(
      ctx: Element_pattern_declaration_and_fillerContext): PatternDeclarationInfo = {
    var eleName = getDefaultName
    if (ctx.element_variable_declaration() != null) {
      eleName = ctx.element_variable_declaration().getText
    }
    val whereClause = parseElePatternWhereClause(ctx.element_pattern_where_clause())
    if (null == ctx.element_lookup()) {
      return new PatternDeclarationInfo(eleName, Set[LabelType](), whereClause)
    }

    if (null != ctx.element_lookup().linked_edge()) {
      val linkedEdge = parseLinkedEdge(ctx.element_lookup().linked_edge())
      val entityLabelType = EntityLabelType(
        eleName.toLowerCase() + linkedEdge.name.capitalize + getDefaultAliasNum)
      val linkedEdgeDefaultType = Set.apply(entityLabelType.asInstanceOf[LabelType])
      return new LinkedPatternDeclarationInfo(
        eleName,
        linkedEdgeDefaultType,
        whereClause,
        linkedEdge)
    }
    val typeNames = parseLabelExpress(ctx.element_lookup().label_expression())
    new PatternDeclarationInfo(eleName, typeNames, whereClause)
  }

  def parseElePatternWhereClause(ctx: Element_pattern_where_clauseContext): Expr = {
    if (ctx == null || ctx.isEmpty) {
      null
    } else {
      parseSearchCondition(ctx.search_condition())
    }
  }

  def parseSearchCondition(ctx: Search_conditionContext): Expr = {
    exprParser.parseLogicValueExpression(ctx.logic_value_expression())
  }

  def parseLabelExpress(ctx: Label_expressionContext): Set[LabelType] = {
    var labels = Set[LabelType]()
    if (ctx != null && !ctx.isEmpty) {
      labels = labels ++ Set.apply(parseLabelName(ctx.label_name()))
      if (ctx.label_expression_lookup() != null && !ctx.label_expression_lookup().isEmpty) {
        labels = labels ++
          ctx.label_expression_lookup().asScala.map(x => parseLabelName(x.label_name()))
      }
    }
    labels
  }

  def parseLabelName(ctx: Label_nameContext): LabelType = {
    ctx.getChild(0) match {
      case c: Entity_typeContext => EntityLabelType(c.getText)
      case c: Concept_nameContext =>
        ConceptLabelType(
          parseIdentifier(c.meta_concept_type().identifier()),
          parseIdentifier(c.concept_instance_id().identifier()))
    }
  }

  def parseIdentifier(ctx: IdentifierContext): String = {
    ctx.oC_SymbolicName().EscapedSymbolicName() match {
      case null => ctx.oC_SymbolicName().getText
      case x => x.getText.replace("`", "")
    }
  }

  def parseLinkedEdge(ctx: Linked_edgeContext): FunctionExpr = {
    exprParser.parseFunctionExpr(ctx.function_expr()).asInstanceOf[FunctionExpr]
  }

}
