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

package com.antgroup.openspg.reasoner.util

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.catalog.{Catalog, SemanticPropertyGraph}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.VString
import com.antgroup.openspg.reasoner.lube.common.pattern.{EdgePattern, LinkedPatternConnection, PartialGraphPattern, Pattern, PatternConnection}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.PatternOps.PatternOps
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.warehouse.common.config.{EdgeLoaderConfig, GraphLoaderConfig, VertexLoaderConfig}
import scala.collection.JavaConverters._
import scala.collection.mutable

object LoaderUtil {

  def getLoaderConfig(
      logicalPlans: List[LogicalOperator],
      catalog: Catalog): GraphLoaderConfig = {
    val loaderConfig = new GraphLoaderConfig()
    for (logicalPlan <- logicalPlans) {
      loaderConfig.merge(getLoaderConfig(logicalPlan, catalog))
    }
    if (logicalPlans.size == 1) {
      val ruleMap = getFilterRule(logicalPlans.head)
      if (!ruleMap.isEmpty) {
        for (v <- loaderConfig.getVertexLoaderConfigs().asScala) {
          if (ruleMap.contains(v.getVertexType)) {
            v.setPropertiesFilterRules(List.apply(ruleMap(v.getVertexType)).asJava)
          }
        }
        for (e <- loaderConfig.getEdgeLoaderConfigs.asScala) {
          if (ruleMap.contains(e.getEdgeType)) {
            e.setPropertiesFilterRules(List.apply(ruleMap(e.getEdgeType)).asJava)
          }
        }
      }
    }
    loaderConfig.verify()
  }

  private def findAllTypesAllowIsolateVertex(logicalPlan: LogicalOperator): Set[String] = {
    logicalPlan.transform[Set[String]] {
      case (LinkedExpand(_, linkedEdgePattern), typeSets) =>
        typeSets.flatMap(_.headOption).toSet ++ linkedEdgePattern.src.typeNames
      case (_, typeSets) => typeSets.flatMap(_.headOption).toSet
    }
  }

  def merge(cur: SolvedModel, other: SolvedModel): SolvedModel = {
    val alias2Types = new mutable.HashMap[String, Set[String]]()
    val fields = new mutable.HashMap[String, Var]()
    for (pair <- cur.alias2Types) {
      alias2Types.put(pair._1, pair._2)
    }
    for (pair <- other.alias2Types) {
      if (alias2Types.contains(pair._1)) {
        alias2Types.put(pair._1, alias2Types(pair._1) ++ pair._2)
      } else {
        alias2Types.put(pair._1, pair._2)
      }
    }
    for (pair <- cur.fields) {
      fields.put(pair._1, pair._2)
    }
    for (pair <- other.fields) {
      if (fields.contains(pair._1)) {
        fields.put(pair._1, pair._2.merge(fields.get(pair._1)))
      } else {
        fields.put(pair._1, pair._2)
      }
    }
    cur.copy(alias2Types = alias2Types.toMap, fields = fields.toMap)
  }

  private def mergeDirection(d1: Direction, d2: Direction): Direction = {
    if (Direction.BOTH.equals(d1) || Direction.BOTH.equals(d2)) {
      Direction.BOTH
    } else if (Direction.IN.equals(d1) && Direction.OUT.equals(d2)) {
      Direction.BOTH
    } else if (Direction.OUT.equals(d1) && Direction.IN.equals(d2)) {
      Direction.BOTH
    } else if (null == d1) {
      d2
    } else {
      d1
    }
  }

  private def mergeDirectionMaps(
      directionMaps: List[Map[String, Direction]]): Map[String, Direction] = {
    directionMaps.foldLeft(Map.empty[String, Direction]) { (acc, map) =>
      acc ++ map.map { case (key, value) =>
        key -> mergeDirection(acc.getOrElse(key, null), value)
      }
    }
  }

  private def mergeVertexAlias2TypeMap(
      alis2TypesMapList: List[Map[String, Set[String]]]): Map[String, Set[String]] = {
    alis2TypesMapList.foldLeft(Map.empty[String, Set[String]]) { (acc, map) =>
      acc ++ map.map { case (key, value) =>
        key -> (acc.getOrElse(key, Set.empty[String]) ++ value)
      }
    }
  }

  private def getVertexAlias2TypeMapFromPattern(pattern: Pattern): Map[String, Set[String]] = {
    var alias2TypesMap = Map.apply(pattern.root.alias -> pattern.root.typeNames)
    pattern match {
      case partialGraphPattern: PartialGraphPattern =>
        val alias2TypesMap2 =
          partialGraphPattern.nodes.values.map(pe => (pe.alias, pe.typeNames)).toMap
        alias2TypesMap = mergeVertexAlias2TypeMap(List.apply(alias2TypesMap, alias2TypesMap2))
      case _ =>
    }
    alias2TypesMap
  }

  private def getVertexAlias2TypeMap(logicalPlan: LogicalOperator): Map[String, Set[String]] = {
    logicalPlan.transform[Map[String, Set[String]]] {
      case (PatternScan(_, pattern), alis2TypesMapList) =>
        mergeVertexAlias2TypeMap(alis2TypesMapList :+ getVertexAlias2TypeMapFromPattern(pattern))
      case (ExpandInto(_, _, pattern), alis2TypesMapList) =>
        mergeVertexAlias2TypeMap(alis2TypesMapList :+ getVertexAlias2TypeMapFromPattern(pattern))
      case (LinkedExpand(_, linkedEdgePattern), alis2TypesMapList) =>
        val srcAlias2TypesMap =
          Map.apply(linkedEdgePattern.src.alias -> linkedEdgePattern.src.typeNames)
        val dstAlias2TypesMap =
          Map.apply(linkedEdgePattern.dst.alias -> linkedEdgePattern.dst.typeNames)
        mergeVertexAlias2TypeMap(alis2TypesMapList :+ srcAlias2TypesMap :+ dstAlias2TypesMap)
      case (_, alis2TypesMapList) => mergeVertexAlias2TypeMap(alis2TypesMapList)
    }
  }

  private def getEdgeLoadDirectionMapFromPattern(
      pattern: Pattern,
      alias2TypesMap: Map[String, Set[String]],
      graph: SemanticPropertyGraph): Map[String, Direction] = {

    val resultMap = new mutable.HashMap[String, Direction]()
    val rootAlias = pattern.root.alias
    pattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .map(pc => PatternConnection.insureDirection(rootAlias, pc))
      .foreach(pc => {
        val srcTypeSet = alias2TypesMap.getOrElse(pc.source, Set.empty)
        val dstTypeSet = alias2TypesMap.getOrElse(pc.target, Set.empty)

        srcTypeSet
          .flatMap(s =>
            dstTypeSet.flatMap(o =>
              pc.relTypes.flatMap(p => {
                if (Direction.IN.equals(pc.direction)) { List((o, p, s, Direction.IN)) }
                else if (Direction.OUT.equals(pc.direction)) { List((s, p, o, Direction.OUT)) }
                else {
                  val spo = new SPO(s, p, o)
                  scala.util.control.Exception.ignoring(classOf[NoSuchElementException]) {
                    val edge = graph.getEdge(spo.toString)
                    if (null != edge && edge.resolved) {
                      List((s, p, o, Direction.OUT), (s, p, o, Direction.IN))
                    }
                  }
                  scala.util.control.Exception.ignoring(classOf[NoSuchElementException]) {
                    val ops = new SPO(o, p, s)
                    val edgeR = graph.getEdge(ops.toString)
                    if (null != edgeR && edgeR.resolved) {
                      List((o, p, s, Direction.OUT), (o, p, s, Direction.IN))
                    }
                  }
                  List.empty
                }
              })))
          .foreach(tuple => {
            val spo = new SPO(tuple._1, tuple._2, tuple._3)
            resultMap.put(
              spo.toString,
              mergeDirection(resultMap.getOrElse(spo.toString, null), tuple._4))
          })
      })

    resultMap.toMap
  }

  private def getEdgeLoadDirectionMap(logicalPlan: LogicalOperator): Map[String, Direction] = {
    val alias2TypesMap = getVertexAlias2TypeMap(logicalPlan)
    logicalPlan.transform[Map[String, Direction]] {
      case (PatternScan(_, pattern), directionMaps) =>
        mergeDirectionMaps(
          directionMaps :+ getEdgeLoadDirectionMapFromPattern(
            pattern,
            alias2TypesMap,
            logicalPlan.graph))
      case (ExpandInto(_, _, pattern), directionMaps) =>
        mergeDirectionMaps(
          directionMaps :+ getEdgeLoadDirectionMapFromPattern(
            pattern,
            alias2TypesMap,
            logicalPlan.graph))
      case (_, directionMaps) => mergeDirectionMaps(directionMaps)
    }
  }

  private def getEdgeEndVertexAliasSet(logicalPlan: LogicalOperator): Map[String, Set[String]] = {
    logicalPlan.transform[Map[String, Set[String]]] {
      case (PatternScan(_, pattern), maps) =>
        mergeEdgeEndVertexAliasMap(maps :+ getPatternEdgeEndVertexAliasMap(pattern))
      case (ExpandInto(_, _, pattern), maps) =>
        mergeEdgeEndVertexAliasMap(maps :+ getPatternEdgeEndVertexAliasMap(pattern))
      case (_, maps) => mergeEdgeEndVertexAliasMap(maps)
    }
  }

  private def mergeEdgeEndVertexAliasMap(
      list: List[Map[String, Set[String]]]): Map[String, Set[String]] = {
    list.foldLeft(Map[String, Set[String]]()) { (acc, m) =>
      m.foldLeft(acc) { (accInner, kv) =>
        val (key, valueSet) = kv
        accInner + (key -> (accInner.getOrElse(key, Set.empty[String]) ++ valueSet))
      }
    }
  }

  private def getPatternEdgeEndVertexAliasMap(pattern: Pattern): Map[String, Set[String]] = {
    pattern.topology.values
      .flatMap(pcs => pcs.map(pc => pc))
      .map(pc => (pc.relTypes, pc.source, pc.target))
      .flatMap { case (set, str1, str2) =>
        for {
          s <- set
        } yield (s, str1, str2)
      }
      .groupBy(_._1)
      .map { case (key, tuples) =>
        key -> tuples.flatMap(t => Set(t._2, t._3)).toSet
      }
  }

  private def getAllowIsolateVertexFromEdgeLoadDirectionMap(
      edgeLoadDirectionMap: Map[String, Direction]): Set[String] = {
    val isolateVertexMap = new mutable.HashMap[String, mutable.HashMap[SPO, Direction]]()

    edgeLoadDirectionMap.toList
      .flatMap(entry =>
        if (Direction.IN.equals(entry._2)) {
          val spo = new SPO(entry._1)
          List((spo.getS, spo, entry._2))
        } else if (Direction.OUT.equals(entry._2)) {
          val spo = new SPO(entry._1)
          List((spo.getO, spo, entry._2))
        } else {
          List.empty
        })
      .foreach(item => {
        val spo2DirectionMap =
          isolateVertexMap.getOrElseUpdate(item._1, new mutable.HashMap[SPO, Direction]())
        val direction = spo2DirectionMap.getOrElse(item._2, null)
        if (Direction.BOTH.equals(direction) || Direction.BOTH.equals(item._3)) {
          spo2DirectionMap.put(item._2, Direction.BOTH)
        } else if (null == direction) {
          spo2DirectionMap.put(item._2, item._3)
        } else if (Direction.IN.equals(direction)) {
          if (Direction.OUT.equals(item._3)) {
            spo2DirectionMap.put(item._2, Direction.BOTH)
          }
        } else if (Direction.OUT.equals(direction)) {
          if (Direction.IN.equals(item._3)) {
            spo2DirectionMap.put(item._2, Direction.BOTH)
          }
        }
      })

    isolateVertexMap.toList
      .filter(p => !p._2.values.forall(d => Direction.BOTH.equals(d)))
      .map(item => item._1)
      .toSet
  }


  def getConceptEdgeExpandSolvedModel(graph: SemanticPropertyGraph,
      edgePattern: EdgePattern[LinkedPatternConnection]): SolvedModel = {
    val conceptMap = getConceptHypernym(graph, edgePattern.dst.typeNames)
    val hypernymEdgeAlias = getHypernymEdgeAlias()
    val alias2Types: Map[String, Set[String]] =
      Map
        .apply(
          edgePattern.src.alias -> edgePattern.src.typeNames,
          edgePattern.dst.alias -> edgePattern.dst.typeNames,
          edgePattern.edge.alias ->
            generateEdgeTypeSet(
              edgePattern.src.typeNames,
              edgePattern.dst.typeNames,
              Set(edgePattern.edge.params.apply(1).asInstanceOf[VString].value),
              edgePattern.edge.direction),
          { hypernymEdgeAlias -> conceptMap.values.toSet })
    val fields: Map[String, Var] = Map.apply(
      edgePattern.edge.alias ->
        EdgeVar(
          edgePattern.edge.alias,
          Set(
            new Field(Constants.EDGE_FROM_ID_KEY, KTString, true),
            new Field(Constants.EDGE_TO_ID_KEY, KTString, true))),
      hypernymEdgeAlias -> EdgeVar(
        hypernymEdgeAlias,
        Set(
          new Field(Constants.EDGE_FROM_ID_KEY, KTString, true),
          new Field(Constants.EDGE_TO_ID_KEY, KTString, true))),
      edgePattern.src.alias -> NodeVar(
        edgePattern.src.alias,
        Set(new Field(Constants.NODE_ID_KEY, KTString, true))),
      edgePattern.dst.alias -> NodeVar(
        edgePattern.dst.alias,
        Set(new Field(Constants.NODE_ID_KEY, KTString, true))))
    SolvedModel(alias2Types, fields, Map.empty)
  }
  private def generateEdgeTypeSet(
                                   srcTypeSet: Set[String],
                                   dstTypeSet: Set[String],
                                   edgeSet: Set[String],
                                   direction: Direction): Set[String] = {
    var set1 = srcTypeSet;
    var set3 = dstTypeSet
    if (Direction.IN.equals(direction)) {
      set1 = dstTypeSet
      set3 = srcTypeSet
    }
    val separator = "_"
    for {
      s1 <- set1
      s2 <- edgeSet
      s3 <- set3
    } yield s"$s1$separator$s2$separator$s3"
  }
  var index = 0;
  private def getHypernymEdgeAlias(): String = {
    index = index + 1
    "hypernym_" + index
  }
  private def getConceptHypernym(
                                  graph: SemanticPropertyGraph,
                                  conceptTypeSet: Set[String]): Map[String, String] = {
    val r = generateEdgeTypeSet(
      conceptTypeSet,
      conceptTypeSet,
      Convert2ScalaUtil.toScalaImmutableSet(Constants.CONCEPT_HYPERNYM_EDGE_TYPE_SET),
      Direction.OUT)
      .filter(e => {
        try {
          graph.getEdge(e)
          true
        } catch {
          case _: NoSuchElementException => false
        }
      })
      .map(t => {
        val spo = new SPO(t)
        (spo.getS, spo.toString)
      })
      .toMap
    r
  }
  def getConceptEdgeExpandMap(logicalPlan: LogicalOperator): Map[String, String] = {
    logicalPlan.transform[Map[String, String]] {
      case (LinkedExpand(_, edgePattern), list) =>
        if (edgePattern.edge.funcName.equals(Constants.CONCEPT_EDGE_EXPAND_FUNC_NAME)) {
          getConceptHypernym(logicalPlan.graph, edgePattern.dst.typeNames)
        } else {
          list.flatten.toMap
        }
      case (_, list) =>
        if (list.isEmpty) {
          Map.empty
        } else {
          list.flatten.toMap
        }
    }
  }


  private def getLoaderConfig(logicalPlan: LogicalOperator, catalog: Catalog) = {
    val solvedModel = logicalPlan.transform[SolvedModel] {
      case (Start(_, _, _, solvedModel), list) =>
        if (list == null || list.isEmpty) {
          solvedModel
        } else {
          merge(solvedModel, list.head)
        }
      case (LinkedExpand(_, edgePattern), list) =>
        if (edgePattern.edge.funcName.equals(Constants.CONCEPT_EDGE_EXPAND_FUNC_NAME)) {
          merge(getConceptEdgeExpandSolvedModel(logicalPlan.graph, edgePattern), list.head)
        } else {
          list.foldLeft(list.head)((l, r) => merge(l, r))
        }
      case (Driving(_, _, solvedModel), list) =>
        if (list == null || list.isEmpty) {
          solvedModel
        } else {
          merge(solvedModel, list.head)
        }
      case (_, list) =>
        if (list.isEmpty) {
          SolvedModel(Map.empty, Map.empty, Map.empty)
        } else {
          list.foldLeft(list.head)((l, r) => merge(l, r))
        }
    }
    val broadcastConceptMap = getConceptEdgeExpandMap(logicalPlan)
    val edgeLoadDirectionMap = getEdgeLoadDirectionMap(logicalPlan)
    val typesAllowIsolateVertex = findAllTypesAllowIsolateVertex(
      logicalPlan) ++ getAllowIsolateVertexFromEdgeLoadDirectionMap(edgeLoadDirectionMap)
    val loaderConf = new GraphLoaderConfig()
    val vertexLoaderConfigMap = new mutable.HashMap[String, VertexLoaderConfig]()
    val edgeLoaderConfigMap = new mutable.HashMap[String, EdgeLoaderConfig]()
    val edgeEndVertexAliasSet = getEdgeEndVertexAliasSet(logicalPlan)
    for (field <- solvedModel.fields.values.map(f => f.flatten).flatten) {
      field match {
        case nodeVar: NodeVar =>
          for (typeName <- solvedModel.getTypes(field.name)) {
            val node = logicalPlan.graph.getNode(typeName.split("/")(0))
            if (node.resolved) {
              val vertexLoaderConfig = new VertexLoaderConfig()
              if (typesAllowIsolateVertex.contains(node.typeName)) {
                vertexLoaderConfig.setAllowIsolateVertex(true)
              }
              vertexLoaderConfig.setVertexType(node.typeName)
              vertexLoaderConfig.setNeedProperties(
                nodeVar.fields.filter(_.resolved).map(_.name).asJava)
              vertexLoaderConfig.setConnection(
                new java.util.HashSet(catalog.getConnection(node.typeName).asJava))
              if (vertexLoaderConfigMap.contains(node.typeName)) {
                val oldVertexLoaderConfig = vertexLoaderConfigMap(node.typeName)
                vertexLoaderConfig.merge(oldVertexLoaderConfig)
              }
              if (broadcastConceptMap.contains(node.typeName)) {
                vertexLoaderConfig.setConceptHypernym(broadcastConceptMap(node.typeName))
              }
              vertexLoaderConfigMap.put(node.typeName, vertexLoaderConfig)
            }
          }
        case edgeVar: EdgeVar =>
          for (typeName <- solvedModel.getTypes(field.name)) {
            val edge = logicalPlan.graph.getEdge(typeName)
            if (edge.resolved) {
              val edgeTypeName = edge.startNode + "_" + edge.typeName + "_" + edge.endNode
              val edgeLoaderConfig = new EdgeLoaderConfig()
              edgeLoaderConfig.setEdgeType(edgeTypeName)
              edgeLoaderConfig.setNeedProperties(
                edgeVar.fields.filter(_.resolved).map(_.name).asJava)
              edgeLoaderConfig.setConnection(
                new java.util.HashSet(catalog.getConnection(edgeTypeName).asJava))
              if (edgeLoaderConfigMap.contains(edgeTypeName)) {
                val oldEdgeLoaderConfig = edgeLoaderConfigMap(edgeTypeName)
                edgeLoaderConfig.merge(oldEdgeLoaderConfig)
              }
              edgeLoaderConfig.setLoadDirection(
                edgeLoadDirectionMap.getOrElse(edgeTypeName, Direction.BOTH))
              edgeLoaderConfig.addEndVertexAliasSet(edgeEndVertexAliasSet.get(edge.typeName))
              edgeLoaderConfigMap.put(edgeTypeName, edgeLoaderConfig)
            }
          }
        case _ =>
      }
    }
    loaderConf.setEdgeLoaderConfigs(edgeLoaderConfigMap.values.toSet.asJava)
    loaderConf.setVertexLoaderConfigs(vertexLoaderConfigMap.values.toSet.asJava)
    loaderConf
  }

  private def getFilterRule(logicalPlan: LogicalOperator): Map[String, Rule] = {
    val rulesMap: Map[String, mutable.MutableList[Rule]] =
      logicalPlan.transform[Map[String, mutable.MutableList[Rule]]] {
        case (scan: PatternScan, list) =>
          val curRuleMap = getRuleMap(scan.pattern)
          mergeRuleMap(list :+ curRuleMap)
        case (expand: ExpandInto, list) =>
          val curRuleMap = getRuleMap(expand.pattern)
          mergeRuleMap(list :+ curRuleMap)
        case (_, list) =>
          if (list != null && !list.isEmpty) {
            mergeRuleMap(list)
          } else {
            Map.empty
          }
      }
    rulesMap.toList
      .map(kv => (kv._1, kv._2.toList))
      .filterNot(kv => kv._2.size > 1 || null == kv._2.head)
      .map(kv => (kv._1, kv._2.head))
      .toMap
  }

  private def mergeRuleMap(ruleMapList: List[Map[String, mutable.MutableList[Rule]]])
      : Map[String, mutable.MutableList[Rule]] = {
    val rstRuleMap = new mutable.HashMap[String, mutable.MutableList[Rule]]()
    ruleMapList.foreach(map =>
      map.toList.foreach(kv =>
        rstRuleMap.getOrElseUpdate(kv._1, new mutable.MutableList[Rule]()) ++= kv._2))
    rstRuleMap.toMap
  }

  private def getRuleMap(pattern: Pattern): Map[String, mutable.MutableList[Rule]] = {
    val aliasToTypes = pattern.patternTypes
    val ruleMap = new mutable.HashMap[String, mutable.MutableList[Rule]]()
    pattern.root.typeNames.foreach(t => {
      ruleMap.getOrElseUpdate(t, new mutable.MutableList[Rule]()) += pattern.root.rule
    })

    for (conn <- pattern.topology.values.flatten) {
      aliasToTypes(conn.alias).foreach(t => {
        ruleMap.getOrElseUpdate(t, new mutable.MutableList[Rule]()) += conn.rule
      })
    }
    ruleMap.toMap
  }

}
