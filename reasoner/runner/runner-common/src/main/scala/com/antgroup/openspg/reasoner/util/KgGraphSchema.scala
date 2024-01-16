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

package com.antgroup.openspg.reasoner.util

import com.antgroup.openspg.reasoner.batching.DynamicBatchSize
import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.graph.`type`.GraphItemType
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, Var}
import com.antgroup.openspg.reasoner.rdg.common.FoldRepeatEdgeInfo
import org.slf4j.LoggerFactory
import scala.collection.mutable

object KgGraphSchema {
  private val logger = LoggerFactory.getLogger(classOf[DynamicBatchSize])

  /**
   * convert pattern to KgGraph schema
   *
   * @param pattern
   * @return
   */
  def convert2KgGraphSchema(pattern: Pattern): PartialGraphPattern = {
    val patternConnectionList = pattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .map(pc => { PatternConnection.insureDirection(pattern.root.alias, pc) })
      .toSet

    val topology =
      convert2Topology(patternConnectionList, Map.apply((pattern.root.alias -> pattern.root)))

    val nodes = topology.values
      .flatMap(pcs => { pcs.map(pc => (pc.source, pc.target)) })
      .flatMap(x => List(x._1, x._2))
      .map(x => (x, pattern.getNode(x)))
      .toMap ++ Map.apply((pattern.root.alias -> pattern.root))

    PartialGraphPattern(pattern.root.alias, nodes, topology)
  }

  /**
   * change KgGraph schema root
   *
   * @param pattern
   * @param target
   * @return
   */
  def schemaChangeRoot(pattern: Pattern, targetAlias: String): PartialGraphPattern = {
    if (null == targetAlias) {
      val vertexMap = new mutable.HashMap[String, PatternElement]()
      pattern.topology.values
        .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
        .flatMap(x => List(x._1, x._2))
        .foreach(k => vertexMap.put(k, pattern.getNode(k)))
      return PartialGraphPattern(null, vertexMap.toMap, pattern.topology)
    }
    expandSchema(
      pattern,
      PartialGraphPattern(
        targetAlias,
        Map.apply((targetAlias -> pattern.getNode(targetAlias))),
        Map.empty))
  }

  /**
   * get after joni schema
   */
  def getAfterJoinSchema(
      leftSchema: PartialGraphPattern,
      rightSchema: PartialGraphPattern): PartialGraphPattern = {
    val edgeDirectionDiff = getEdgeDirectionDiff(leftSchema, rightSchema)
    val leftEgeSet = leftSchema.topology.values
      .flatMap(pcs => { pcs.map(pc => pc) })
      .filter(pc => !edgeDirectionDiff.contains(pc.alias))
      .toSet ++
      rightSchema.topology.values
        .flatMap(pcs => { pcs.map(pc => pc) })
        .filter(pc => edgeDirectionDiff.contains(pc.alias))
        .toSet

    val leftNodeMap = leftEgeSet
      .flatMap(pc => List(leftSchema.getNode(pc.source), leftSchema.getNode(pc.target)))
      .map(pe => PatternElement(pe.alias, pe.typeNames, null))
      .map(pe => (pe.alias, pe))
      .toMap

    val topology = convert2Topology(leftEgeSet, leftNodeMap)
    KgGraphSchema.mergeSchema(
      PartialGraphPattern(leftSchema.rootAlias, leftNodeMap, topology),
      rightSchema)
  }

  /**
   * change schema vertex alias
   */
  def schemaAliasMapping(pattern: Pattern, schemaMapping: Map[Var, Var]): PartialGraphPattern = {
    val vertexAliasMapping = schemaMapping
      .filter(kv => {
        kv._1.isInstanceOf[NodeVar] && kv._2.isInstanceOf[NodeVar]
      })
      .map(kv => { (kv._1.asInstanceOf[NodeVar].name, kv._2.asInstanceOf[NodeVar].name) })
      .filter(kv => !kv._1.equals(kv._2))

    val edgeAliasMapping = schemaMapping
      .filter(kv => {
        kv._1.isInstanceOf[EdgeVar] && kv._2.isInstanceOf[EdgeVar]
      })
      .map(kv => { (kv._1.asInstanceOf[EdgeVar].name, kv._2.asInstanceOf[EdgeVar].name) })
      .filter(kv => !kv._1.equals(kv._2))

    val patternConnectionSet = pattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .map {
        case connection: PatternConnection =>
          var patternConnection = connection
          val renameSource = vertexAliasMapping.get(patternConnection.source)
          if (renameSource.isDefined) {
            patternConnection = PatternConnection(
              patternConnection.alias,
              renameSource.get,
              patternConnection.relTypes,
              patternConnection.target,
              patternConnection.direction,
              patternConnection.rule,
              patternConnection.limit,
              patternConnection.exists,
              patternConnection.optional)
          }
          val renameTarget = vertexAliasMapping.get(patternConnection.target)
          if (renameTarget.isDefined) {
            patternConnection = PatternConnection(
              patternConnection.alias,
              patternConnection.source,
              patternConnection.relTypes,
              renameTarget.get,
              patternConnection.direction,
              patternConnection.rule,
              patternConnection.limit,
              patternConnection.exists,
              patternConnection.optional)
          }
          val renameEdge = edgeAliasMapping.get(patternConnection.alias)
          if (renameEdge.isDefined) {
            patternConnection = PatternConnection(
              renameEdge.get,
              patternConnection.source,
              patternConnection.relTypes,
              patternConnection.target,
              patternConnection.direction,
              patternConnection.rule,
              patternConnection.limit,
              patternConnection.exists,
              patternConnection.optional)
          }
          patternConnection
        case connection: PathConnection =>
          var pathConnection = connection
          val renameSource = vertexAliasMapping.get(pathConnection.source)
          if (renameSource.isDefined) {
            pathConnection = PathConnection(
              pathConnection.alias,
              renameSource.get,
              pathConnection.relTypes,
              pathConnection.target,
              pathConnection.direction,
              pathConnection.rule,
              pathConnection.vertexSchemaList,
              pathConnection.edgeSchemaList)
          }
          val renameTarget = vertexAliasMapping.get(pathConnection.target)
          if (renameTarget.isDefined) {
            pathConnection = PathConnection(
              pathConnection.alias,
              pathConnection.source,
              pathConnection.relTypes,
              renameSource.get,
              pathConnection.direction,
              pathConnection.rule,
              pathConnection.vertexSchemaList,
              pathConnection.edgeSchemaList)
          }
          val renameEdge = edgeAliasMapping.get(pathConnection.alias)
          if (renameEdge.isDefined) {
            pathConnection = PathConnection(
              renameEdge.get,
              pathConnection.source,
              pathConnection.relTypes,
              pathConnection.target,
              pathConnection.direction,
              pathConnection.rule,
              pathConnection.vertexSchemaList,
              pathConnection.edgeSchemaList)
          }
          pathConnection
        case pc =>
          pc
      }
      .toSet

    val vertexMap = new mutable.HashMap[String, PatternElement]()
    pattern.topology.values
      .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
      .flatMap(x => List(x._1, x._2))
      .foreach(k => {
        val oldPatternElement = pattern.getNode(k)
        val renameAlias = vertexAliasMapping.get(k)
        if (renameAlias.isEmpty) {
          vertexMap.put(k, oldPatternElement)
        } else {
          val newPatternElement =
            PatternElement(renameAlias.get, oldPatternElement.typeNames, oldPatternElement.rule)
          vertexMap.put(renameAlias.get, newPatternElement)
        }
      })
    val rootRenameAlias = vertexAliasMapping.get(pattern.root.alias)
    if (rootRenameAlias.isEmpty) {
      vertexMap.put(pattern.root.alias, pattern.root)
    } else {
      val newPatternElement =
        PatternElement(rootRenameAlias.get, pattern.root.typeNames, pattern.root.rule)
      vertexMap.put(rootRenameAlias.get, newPatternElement)
    }

    val topology = convert2Topology(patternConnectionSet, vertexMap.toMap)

    var rootAlias = pattern.root.alias
    val renameAlias = vertexAliasMapping.get(rootAlias)
    if (renameAlias.isDefined) {
      rootAlias = renameAlias.get
    }

    PartialGraphPattern(rootAlias, vertexMap.toMap, topology)
  }

  /**
   * After executing expandInto in RDG, expand the schema
   * @param existingPattern
   * @param addedPattern
   * @return
   */
  def expandSchema(existingPattern: Pattern, addedPattern: Pattern): PartialGraphPattern = {
    val patternConnectionSet = addedPattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .map(pc => { PatternConnection.insureDirection(addedPattern.root.alias, pc) })
      .toSet ++
      existingPattern.topology.values
        .flatMap(pcs => {
          pcs.map(pc => pc)
        })
        .toSet

    val vertexMap = new mutable.HashMap[String, PatternElement]()
    addedPattern.topology.values
      .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
      .flatMap(x => List(x._1, x._2))
      .foreach(k => vertexMap.put(k, addedPattern.getNode(k)))
    existingPattern.topology.values
      .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
      .flatMap(x => List(x._1, x._2))
      .foreach(k => vertexMap.put(k, existingPattern.getNode(k)))
    vertexMap.put(addedPattern.root.alias, addedPattern.root)

    val topology = convert2Topology(patternConnectionSet, vertexMap.toMap)

    PartialGraphPattern(addedPattern.root.alias, vertexMap.toMap, topology)
  }

  /**
   * merge two schema
   */
  def mergeSchema(existingPattern: Pattern, addedPattern: Pattern): PartialGraphPattern = {
    val patternConnectionSet = addedPattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .toSet ++
      existingPattern.topology.values
        .flatMap(pcs => {
          pcs.map(pc => pc)
        })
        .toSet

    val vertexMap = new mutable.HashMap[String, PatternElement]()
    addedPattern.topology.values
      .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
      .flatMap(x => List(x._1, x._2))
      .foreach(k => vertexMap.put(k, addedPattern.getNode(k)))
    existingPattern.topology.values
      .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
      .flatMap(x => List(x._1, x._2))
      .foreach(k => vertexMap.put(k, existingPattern.getNode(k)))
    vertexMap.put(addedPattern.root.alias, addedPattern.root)

    val topology = convert2Topology(patternConnectionSet, vertexMap.toMap)

    PartialGraphPattern(addedPattern.root.alias, vertexMap.toMap, topology)
  }

  def foldPathEdgeSchema(
      existingPattern: Pattern,
      windEdge: FoldRepeatEdgeInfo): PartialGraphPattern = {

    val foldConnection = existingPattern.topology.values
      .flatMap(pcs => { pcs.map(pc => pc) })
      .filter(p => p.alias.equals(windEdge.getFromEdgeAlias))
      .toList
      .head
      .asInstanceOf[PatternConnection]

    val existingPathConnection = existingPattern.topology.values
      .flatMap(pcs => { pcs.map(pc => pc) })
      .filter(p => p.alias.equals(windEdge.getToEdgeAlias))
      .toList
      .headOption

    var newPathConnection: PathConnection = null
    var rootAlias: String = null
    if (existingPathConnection.isEmpty) {
      val sourceAlias = foldConnection.source
      val targetAlias = windEdge.getToVertexAlias
      newPathConnection = PathConnection(
        windEdge.getToEdgeAlias,
        sourceAlias,
        foldConnection.relTypes,
        targetAlias,
        foldConnection.direction,
        null,
        List.empty,
        List.apply(foldConnection))
      rootAlias = existingPattern.root.alias
      if (rootAlias.equals(windEdge.getFromVertexAlias)) {
        rootAlias = windEdge.getToVertexAlias
      }
    } else {
      val opc = existingPathConnection.get.asInstanceOf[PathConnection]
      val oldFoldEdgeSchema = opc.edgeSchemaList.last
      val nextFoldEdgeSchema = PatternConnection(
        foldConnection.alias,
        oldFoldEdgeSchema.target,
        foldConnection.relTypes,
        foldConnection.target,
        foldConnection.direction,
        foldConnection.rule)

      val foldVertexSchema = existingPattern.getNode(windEdge.getToVertexAlias)
      val nextFoldVertexSchema = PatternElement(
        nextFoldEdgeSchema.source,
        foldVertexSchema.typeNames,
        foldVertexSchema.rule)
      newPathConnection = PathConnection(
        opc.alias,
        opc.source,
        opc.relTypes,
        opc.target,
        opc.direction,
        opc.rule,
        opc.vertexSchemaList :+ nextFoldVertexSchema,
        opc.edgeSchemaList :+ nextFoldEdgeSchema)
      rootAlias = newPathConnection.alias + "." + newPathConnection.edgeSchemaList.last.source
    }

    val existVertexPattern = existingPattern.getNode(windEdge.getFromVertexAlias)
    val newVertexPattern =
      PatternElement(
        windEdge.getToVertexAlias,
        existVertexPattern.typeNames,
        existVertexPattern.rule)

    val nodes = existingPattern.topology.values
      .flatMap(pcs => { pcs.map(pc => (pc.source, pc.target)) })
      .flatMap(x => List(x._1, x._2))
      .filter(p => !p.equals(windEdge.getFromVertexAlias))
      .map(x => (x, existingPattern.getNode(x)))
      .toMap ++ Map.apply((newVertexPattern.alias, newVertexPattern))

    val patternConnectionSet = existingPattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .filter(pc => !pc.alias.equals(windEdge.getFromEdgeAlias))
      .filter(pc => !pc.alias.equals(windEdge.getToEdgeAlias))
      .toSet ++ Set.apply(newPathConnection)

    val topology = convert2Topology(patternConnectionSet, nodes)

    PartialGraphPattern(rootAlias, nodes, topology)
  }

  /**
   * get alias is edge or vertex
   */
  def alias2Type(pattern: PartialGraphPattern): Map[String, GraphItemType] = {
    pattern.nodes.values
      .map(pe => (pe.alias, GraphItemType.VERTEX))
      .toMap ++
      pattern.topology.values
        .flatMap(pcs => pcs.map(pc => pc))
        .map(pc => (pc.alias, GraphItemType.EDGE))
        .toMap
  }

  private def convert2Topology(
      patternConnectionSet: Set[Connection],
      vertexMap: Map[String, PatternElement]): Map[String, Set[Connection]] = {
    val topologyOut = patternConnectionSet
      .map(pc => connectionRemoveRule(pc))
      .groupBy(pc => pc.source)
      .mapValues(list => list)

    val topologyIn = patternConnectionSet
      .map(pc => connectionRemoveRule(pc))
      .groupBy(pc => pc.target)
      .mapValues(list => list)

    val topology = topologyOut.foldLeft(topologyIn) { case (m, (key, value)) =>
      m + (key -> (m.getOrElse(key, Set.empty[PatternConnection]) ++ value))
    }

    topology.filterKeys(key => vertexMap.contains(key)).view.force
  }

  def connectionRemoveRule(connection: Connection): Connection = {
    connection match {
      case pc: PatternConnection =>
        PatternConnection(
          pc.alias,
          pc.source,
          pc.relTypes,
          pc.target,
          pc.direction,
          null,
          pc.limit,
          pc.exists,
          pc.optional)
      case ppc: PathConnection =>
        PathConnection(
          ppc.alias,
          ppc.source,
          ppc.relTypes,
          ppc.target,
          ppc.direction,
          null,
          ppc.vertexSchemaList,
          ppc.edgeSchemaList)
      case lpc: LinkedPatternConnection =>
        LinkedPatternConnection(
          lpc.alias,
          lpc.source,
          lpc.relTypes,
          lpc.funcName,
          lpc.params,
          lpc.target,
          lpc.direction,
          null,
          lpc.limit)
      case _ =>
        throw UnsupportedOperationException("unknown type " + connection.getClass.getName)
    }
  }

  def getNodesAlias(pattern: Pattern): Set[String] = {
    pattern.topology.keySet ++
      pattern.topology.values
        .flatMap(pcs => pcs.map(pc => (pc.source, pc.target)))
        .flatMap(x => List(x._1, x._2))
        .toSet
  }

  def getEdgesAlias(pattern: Pattern): Set[String] = {
    pattern.topology.values
      .flatMap(pcs => pcs.map(pc => pc.alias))
      .toSet
  }

  def getPatternConnection(pattern: Pattern, edgeAlias: String): Connection = {
    val result: Option[Connection] = pattern.topology.values
      .flatMap(pcs => pcs.toList)
      .find(_.alias.equals(edgeAlias))
    result match {
      case Some(value) => value
      case None => null
    }
  }

  def getNeighborEdges(pattern: Pattern, vertexAlias: String): Set[Connection] = {
    pattern.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .filter(pc => { pc.source.equals(vertexAlias) || pc.target.equals(vertexAlias) })
      .toSet
  }

  def getOverlapSchema(
      left: PartialGraphPattern,
      right: PartialGraphPattern): (Set[PatternElement], Set[Connection]) = {
    val overlapVertexSet = left.nodes.keySet.flatMap(alias => right.nodes.get(alias))

    val leftConnectSet = left.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .toSet

    val overlapConnectSet = right.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .filter(pc => leftConnectSet.contains(pc))
      .toSet
    Tuple2(overlapVertexSet, overlapConnectSet)
  }

  def getEdgeDirectionDiff(
      left: PartialGraphPattern,
      right: PartialGraphPattern): Map[String, (Direction, Direction)] = {

    val leftConnectMap = left.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .map(pc => (pc.alias, pc))
      .toMap

    right.topology.values
      .flatMap(pcs => {
        pcs.map(pc => pc)
      })
      .filter(pc =>
        leftConnectMap.contains(pc.alias) && !leftConnectMap
          .getOrElse(pc.alias, pc)
          .direction
          .equals(pc.direction))
      .map(pc =>
        (
          pc.alias,
          (
            if (Direction.OUT.equals(pc.direction)) { Direction.IN }
            else { Direction.OUT },
            pc.direction)))
      .toMap
  }

}
