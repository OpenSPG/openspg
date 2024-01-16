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

package com.antgroup.openspg.reasoner.lube.common.pattern

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph

sealed trait Pattern {

  /**
   * The root node element contains in the pattern graph.
   *
   * @return
   */
  def root: PatternElement

  /**
   * Get pattern element by node alias
   * @param alias
   * @return
   */
  def getNode(alias: String): PatternElement

  /**
   * The patterns graph, describing connections between node elements via relationships.
   *
   * @return
   */
  def topology: Map[String, Set[Connection]]
}

case class NodePattern(node: PatternElement) extends Pattern {

  /**
   * The root node element contains in the pattern graph.
   *
   * @return
   */
  override def root: PatternElement = node

  /**
   * The patterns graph, describing connections between node elements via relationships.
   *
   * @return
   */
  override def topology: Map[String, Set[Connection]] = Map.empty

  /**
   * Get pattern element by node alias
   *
   * @param alias
   * @return
   */
  override def getNode(alias: String): PatternElement = node
}

case class EdgePattern[E <: Connection](src: PatternElement, dst: PatternElement, edge: E)
    extends Pattern {

  /**
   * The root node element contains in the pattern graph.
   *
   * @return
   */
  override def root: PatternElement = src

  /**
   * Get pattern element by node alias
   *
   * @param alias
   * @return
   */
  override def getNode(alias: String): PatternElement =
    if (src.alias.equals(alias)) src else if (dst.alias.equals(alias)) dst else null

  /**
   * The patterns graph, describing connections between node elements via relationships.
   *
   * @return
   */
  override def topology: Map[String, Set[Connection]] =
    Map.apply((src.alias, Set.apply(edge)))

}

case class PartialGraphPattern(
    rootAlias: String,
    nodes: Map[String, PatternElement],
    edges: Map[String, Set[Connection]])
    extends Pattern {

  /**
   * The root node element contains in the pattern graph.
   *
   * @return
   */
  override def root: PatternElement = getNode(rootAlias)

  /**
   * Get pattern element by node alias
   *
   * @param alias
   * @return
   */
  override def getNode(alias: String): PatternElement = {
    nodes.getOrElse(alias, null)
  }

  /**
   * The patterns graph, describing connections between node elements via relationships.
   *
   * @return
   */
  override def topology: Map[String, Set[Connection]] = edges
}

case class GraphPattern(
    rootAlias: String,
    nodes: Map[String, Element],
    edges: Map[String, Set[Connection]],
    properties: Map[String, Set[String]]) {

  /**
   * Get pattern element by node alias
   *
   * @param alias
   * @return
   */
  def getNode(alias: String): Element = {
    nodes.getOrElse(alias, null)
  }

  def addTypeItem(
      map: mutable.HashMap[String, Set[String]],
      key: String,
      value: Set[String]): Unit = {
    map.getOrElseUpdate(key, Set()) match {
      case existingValue: Set[String] =>
        map.update(key, value ++ existingValue)
    }
  }

  def containsLabels(labels: Set[String], label: String): String = {
    if (labels.isEmpty) {
      return label
    }
    for (nodeLabel <- labels) {
      var compareLabel = nodeLabel
      if (label.equals(compareLabel)) {
        return label
      }
      if (nodeLabel.contains("/")) {
        compareLabel = nodeLabel.split("/")(0)
      }
      if (label.equals(compareLabel)) {
        return nodeLabel
      }
    }
    null
  }

  def generateGraphPatternTypesBySchema(
      graph: SemanticPropertyGraph): Map[String, Set[String]] = {
    val typeMap = new mutable.HashMap[String, Set[String]]()
    val nodeTypeMap = new mutable.HashMap[String, Set[String]]()
    nodes.foreach(node => {
      val sourceSet = node._2.typeNames
      nodeTypeMap.put(node._2.alias, sourceSet)
    })
    val patternEleEdge = edges.flatMap(e => {
      e._2.map {
        case c: LinkedPatternConnection =>
          val sourceSet = nodeTypeMap(c.source)
          val typeSet = c.relTypes
          val targetSet = nodeTypeMap(c.target)
          val spoSet = sourceSet.flatMap(x =>
            typeSet.flatMap(y =>
              targetSet.map(z => {
                if (c.direction == Direction.OUT) {
                  new SPO(x, y, z).toString
                } else {
                  new SPO(z, y, x).toString
                }
              })))
          typeMap.put(c.alias, spoSet)
          typeMap.put(c.source, sourceSet)
          typeMap.put(c.target, targetSet)
          null
        case x => x
      }.filter(_ != null)
    })

    if (patternEleEdge.isEmpty) {
      nodes.foreach(node => {
        if (node._2.typeNames.isEmpty) {
          typeMap.put(node._1, graph.graphSchema.nodes.keySet.toSet)
        } else {
          typeMap.put(node._1, node._2.typeNames)
        }
      })
    } else {
      for (conn <- patternEleEdge) {

        val typeSet = conn.relTypes
        var directions = new mutable.HashSet[Direction]()
        if (conn.direction == Direction.BOTH) {
          directions = directions ++ Set.apply(Direction.OUT, Direction.IN)
        } else {
          directions = directions + conn.direction
        }

        var spoSet = Set[String]()

        directions.foreach(direction => {
          var sourceSet = nodeTypeMap(conn.source)
          var targetSet = nodeTypeMap(conn.target)
          if (direction != Direction.OUT) {
            sourceSet = nodeTypeMap(conn.target)
            targetSet = nodeTypeMap(conn.source)
          }

          var finalTargetSet = Set[String]()
          var finalSourceSet = Set[String]()

          graph.graphSchema.edges.foreach(spo =>
            if (typeSet.isEmpty || typeSet.contains(spo._1.getP)) {

              val sourceLabel = containsLabels(sourceSet, spo._1.getS)
              val targetLabel = containsLabels(targetSet, spo._1.getO)

              if (sourceLabel != null && targetLabel != null) {
                finalSourceSet = finalSourceSet + sourceLabel
                finalTargetSet = finalTargetSet + targetLabel
                spoSet = spoSet + new SPO(sourceLabel, spo._1.getP, targetLabel).toString

              }
            })

          if (direction != Direction.OUT) {
            addTypeItem(typeMap, conn.target, finalSourceSet)
            addTypeItem(typeMap, conn.source, finalTargetSet)
          } else {
            addTypeItem(typeMap, conn.target, finalTargetSet)
            addTypeItem(typeMap, conn.source, finalSourceSet)
          }
        })
        typeMap.put(conn.alias, spoSet)
      }
    }

    typeMap.toMap
  }

  def patternTypes(): Map[String, Set[String]] = {
    val typeMap = new mutable.HashMap[String, Set[String]]()
    nodes.foreach(node => {
      val sourceSet = node._2.typeNames
      typeMap.put(node._2.alias, sourceSet)
    })

    for (edge <- edges) {
      edge._2.foreach(conn => {
        val sourceSet = typeMap(conn.source)
        val typeSet = conn.relTypes
        val targetSet = typeMap(conn.target)
        val spoSet = sourceSet.flatMap(x =>
          typeSet.flatMap(y =>
            targetSet.map(z => {
              if (conn.direction == Direction.OUT) {
                new SPO(x, y, z).toString
              } else {
                new SPO(z, y, x).toString
              }
            })))
        typeMap.put(conn.alias, spoSet)
      })
    }
    typeMap.toMap
  }

}

/**
 * a path from "GraphStructure" define
 * @param pathName
 * @param graphPattern
 * @param optional
 */
case class GraphPath(pathName: String, graphPattern: GraphPattern, optional: Boolean)
