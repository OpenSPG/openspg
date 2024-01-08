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

package com.antgroup.openspg.reasoner.lube.logical.planning

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.pattern.ElementOps.toPattenElement
import com.antgroup.openspg.reasoner.lube.logical.SolvedModel
import com.antgroup.openspg.reasoner.lube.logical.operators._

/**
 * QueryPath splitting
 * @param pattern GraphPath, some times are called QueryGraph
 */
class PatternMatchPlanner(val pattern: GraphPattern)(implicit context: LogicalPlannerContext) {

  def plan(dependency: LogicalOperator): LogicalOperator = {
    val chosenNodes = new mutable.HashSet[String]()
    val chosenEdges = new mutable.HashSet[Connection]()
    plan(dependency, chosenNodes, chosenEdges)
  }

  private def plan(
      dependency: LogicalOperator,
      chosenNodes: mutable.HashSet[String],
      chosenEdges: mutable.HashSet[Connection]): LogicalOperator = {
    val root = getRoot
    val parts: (GraphPattern, GraphPattern, Set[Connection]) =
      split(root, chosenNodes, chosenEdges)
    var lhsOperator = dependency
    if (parts._1 != null) {
      val lhsPlanner = new PatternMatchPlanner(parts._1)
      lhsOperator = lhsPlanner.plan(root, dependency, chosenNodes, chosenEdges)
    }
    if (!parts._3.isEmpty) {
      val optionalConnections = parts._3
        .filter(_.isInstanceOf[PatternConnection])
        .groupBy(c => getSrcAndDst(c, chosenNodes)._1)
      for (groupConnections <- optionalConnections) {
        val src = groupConnections._1
        var rhsOperator: LogicalOperator = null
        for (connection <- groupConnections._2) {
          if (!chosenEdges.contains(connection)) {
            val dst = getSrcAndDst(connection, chosenNodes)._2
            chosenEdges.add(connection)
            val driving = Driving(dependency.graph, src, dependency.solved)
            val scan = PatternScan(driving, buildEdgePattern(connection))
            val rhsPlanner = new PatternMatchPlanner(parts._2.copy(rootAlias = dst))
            val oneRhsOperator = rhsPlanner.plan(scan, chosenNodes.clone(), chosenEdges.clone())
            if (oneRhsOperator != null && rhsOperator != null) {
              rhsOperator = PatternJoin(rhsOperator, oneRhsOperator, FullOuterJoin)
            } else if (rhsOperator == null) {
              rhsOperator = oneRhsOperator
            }
          }
        }
        lhsOperator = if (rhsOperator != null) {
          Optional(lhsOperator, rhsOperator)
        } else {
          lhsOperator
        }
      }

      for (connection <- parts._3.filter(_.isInstanceOf[VariablePatternConnection])) {
        if (!chosenEdges.contains(connection)) {
          val (src, dst) = getSrcAndDst(connection, chosenNodes)
          chosenEdges.add(connection)
          val rhsPlanner = new PatternMatchPlanner(parts._2.copy(rootAlias = dst))
          val rhsOperator =
            rhsPlanner.plan(
              Driving(dependency.graph, dst, dependency.solved),
              chosenNodes,
              chosenEdges)
          lhsOperator = connection match {
            case conn: VariablePatternConnection =>
              val edgePattern =
                buildEdgePattern(conn).asInstanceOf[EdgePattern[VariablePatternConnection]]
              val repeatOperator = buildBoundVarLenExpand(src, edgePattern, lhsOperator)
              if (rhsOperator == null) {
                repeatOperator
              } else {
                PatternJoin(repeatOperator, rhsOperator, InnerJoin)
              }
            case _ => throw UnsupportedOperationException(s"unsupported $connection")
          }
        }
      }
    }
    if (lhsOperator.isInstanceOf[Driving]) {
      null
    } else {
      lhsOperator
    }
  }

  private def buildEdgePattern(conn: Connection) = {
    EdgePattern(pattern.getNode(conn.source), pattern.getNode(conn.target), conn)
  }

  private def getSrcAndDst(
      connection: Connection,
      chosenNodes: mutable.HashSet[String]): (String, String) = {
    if (chosenNodes.contains(connection.source)) {
      (connection.source, connection.target)
    } else {
      (connection.target, connection.source)
    }
  }

  /**
   * Split graph pattern into two parts.
   * @return
   */
  private def split(
      root: String,
      chosenNodes: mutable.HashSet[String],
      chosenEdges: mutable.HashSet[Connection]): (GraphPattern, GraphPattern, Set[Connection]) = {
    val queue = new mutable.Queue[String]()
    val visited = new mutable.HashSet[String]()
    visited.++=(chosenNodes)
    queue.enqueue(root)
    val lhsNodes = new mutable.HashMap[String, Element]()
    val lhsEdges = new mutable.HashMap[String, Set[Connection]]()
    val splitEdges = new mutable.HashSet[Connection]()
    while (!queue.isEmpty) {
      val r = queue.dequeue()
      if (!visited.contains(r)) {
        if (pattern.nodes.contains(r)) {
          lhsNodes.put(r, pattern.getNode(r))
        }
        if (pattern.edges.contains(r)) {
          // add out edges
          val normalEdges = pattern.edges(r).filter(!isSplitConnection(_))
          splitEdges ++= pattern.edges(r).filter(isSplitConnection(_))
          lhsEdges.put(r, normalEdges)
          normalEdges.map(_.target).foreach(queue.enqueue(_))
        }
        for (pair <- pattern.edges) {
          if (!pair._1.equals(r) && !visited.contains(r)) {
            val connections = pair._2.filter(_.target.equals(r))
            val normalEdges = connections.filter(!isSplitConnection(_))
            splitEdges ++= connections.filter(isSplitConnection(_))
            if (!lhsEdges.contains(pair._1)) {
              lhsEdges.put(pair._1, normalEdges)
            } else {
              lhsEdges.put(pair._1, lhsEdges(pair._1) ++ normalEdges)
            }
            if (!normalEdges.isEmpty) {
              queue.enqueue(pair._1)
            }
          }
        }
      }
      visited.add(r)
    }
    val rhsNodes =
      pattern.nodes.filter(p => !lhsNodes.contains(p._1) && !chosenNodes.contains(p._1))
    val rhsEdges = pattern.edges
      .map(p =>
        (
          p._1,
          p._2
            .diff(lhsEdges.getOrElse(p._1, Set.empty))
            .diff(splitEdges)
            .filter(e => !chosenEdges.contains(e))))
      .filter(p => p._2 != null && !p._2.isEmpty)

    val lhsPattern = if (lhsNodes.isEmpty && lhsEdges.values.flatten.isEmpty) {
      null
    } else {
      pattern.copy(rootAlias = root, nodes = lhsNodes.toMap, edges = lhsEdges.toMap)
    }
    val rhsPattern = if (rhsNodes.isEmpty && rhsEdges.values.flatten.isEmpty) {
      null
    } else {
      pattern.copy(nodes = rhsNodes, edges = rhsEdges)
    }
    (lhsPattern, rhsPattern, splitEdges.toSet)
  }

  private def isSplitConnection(connection: Connection): Boolean = {
    connection match {
      case connection: VariablePatternConnection => true
      case PatternConnection(_, _, _, _, _, _, _, _, true) => true
      case _ => false
    }
  }

  private def plan(
      root: String,
      dependency: LogicalOperator,
      chosenNodes: mutable.HashSet[String],
      chosenEdges: mutable.HashSet[Connection]): LogicalOperator = {
    chosenNodes.add(root)
    val patternList = buildPattern(root, chosenNodes, chosenEdges)
    var in: LogicalOperator =
      constructLogicalOperator(patternList, pattern.getNode(root), dependency)

    var nextRoot = getMaxDegree(chosenNodes)
    while (nextRoot != null) {
      val expandIntoPattern = buildPattern(nextRoot, chosenNodes, chosenEdges)
      val targetNode = pattern.getNode(nextRoot)
      in = constructLogicalOperator(expandIntoPattern, targetNode, in)
      chosenNodes.add(nextRoot)
      nextRoot = getMaxDegree(chosenNodes)
    }
    in
  }

  private def getRoot: String = {
    if (pattern.rootAlias != null) {
      pattern.rootAlias
    } else if (context.params.contains(Constants.START_ALIAS)) {
      context.params(Constants.START_ALIAS).asInstanceOf[String]
    } else {
      getMaxDegree
    }
  }

  private def constructLogicalOperator(
      patternList: List[Pattern],
      targetNode: PatternElement,
      dependency: LogicalOperator): LogicalOperator = {
    var in: LogicalOperator = dependency
    for (pattern <- patternList) {
      if (pattern.isInstanceOf[EdgePattern[_ <: Connection]]) {
        in = buildEdgePattern(pattern.asInstanceOf[EdgePattern[Connection]], in)
      } else {
        in = in match {
          case start: Start =>
            PatternScan(
              in.asInstanceOf[Start]
                .copy(types = pattern.root.typeNames, alias = pattern.root.alias),
              pattern)
          case driving: Driving =>
            PatternScan(in, pattern)
          case _ => ExpandInto(in, targetNode, pattern)
        }
      }
    }
    in
  }

  private def buildEdgePattern(
      pattern: EdgePattern[Connection],
      dependency: LogicalOperator): LogicalOperator = {
    pattern.edge match {
      case conn: LinkedPatternConnection =>
        LinkedExpand(dependency, pattern.asInstanceOf[EdgePattern[LinkedPatternConnection]])
      case conn: PatternConnection =>
        val patternScan = PatternScan(dependency, pattern)
        ExpandInto(patternScan, pattern.dst, NodePattern(pattern.dst))
      case _ =>
        dependency
    }
  }

  private def buildBoundVarLenExpand(
      curRoot: String,
      edgePattern: EdgePattern[VariablePatternConnection],
      dependency: LogicalOperator): LogicalOperator = {
    var preRoot = dependency
    val finalEdgePattern = if (edgePattern.src.alias.equals(curRoot)) {
      edgePattern
    } else {
      EdgePattern(
        edgePattern.dst,
        edgePattern.src,
        edgePattern.edge.reverse.asInstanceOf[VariablePatternConnection])
    }
    for (i <- 1 to finalEdgePattern.edge.upper) {
      val rhs = varLenLogicalOperator(dependency.graph, dependency.solved, finalEdgePattern, i)
      preRoot = BoundedVarLenExpand(preRoot, rhs, finalEdgePattern, i)
    }
    preRoot
  }

  private def varLenLogicalOperator(
      graph: SemanticPropertyGraph,
      solved: SolvedModel,
      edgePattern: EdgePattern[VariablePatternConnection],
      index: Int): LogicalOperator = {
    val startAlias = if (index == 1) {
      edgePattern.src.alias
    } else {
      edgePattern.dst.alias
    }
    val aliasSet = mutable.HashSet[String]()
    aliasSet.add(edgePattern.src.alias)
    aliasSet.add(edgePattern.edge.alias)
    aliasSet.add(edgePattern.dst.alias)
    val alias2Type = solved.alias2Types.filter(a => aliasSet.contains(a._1))
    val fields = solved.fields.filter(a => aliasSet.contains(a._1))
    val start = Driving(graph, startAlias, SolvedModel(alias2Type, fields, Map.empty))
    val edge = PatternConnection(
      edgePattern.edge.alias,
      edgePattern.src.alias,
      edgePattern.edge.relTypes,
      edgePattern.dst.alias,
      edgePattern.edge.direction,
      edgePattern.edge.rule,
      edgePattern.edge.limit)
    val targetElement = edgePattern.dst.copy(alias = edge.target)
    val patternScan = PatternScan(start, EdgePattern(edgePattern.src, targetElement, edge))
    ExpandInto(patternScan, targetElement, NodePattern(targetElement))
  }

  private def getMaxDegree: String = {
    val degree = new mutable.HashMap[String, Int]()
    pattern.edges.foreach(pair =>
      pair._2.foreach(conn => {
        degree.put(conn.source, degree.getOrElse(conn.source, 0) + 1)
        degree.put(conn.target, degree.getOrElse(conn.target, 0) + 1)
      }))
    if (degree.isEmpty) {
      pattern.nodes.head._1
    } else {
      degree.maxBy(_._2)._1
    }
  }

  private def getMaxDegree(chosen: mutable.HashSet[String]): String = {
    val degree = new mutable.HashMap[String, Int]()
    pattern.edges.foreach(pair =>
      pair._2.foreach(conn => {
        if (chosen.contains(conn.source) && !chosen.contains(conn.target)) {
          degree.put(conn.target, degree.getOrElse(conn.target, 0) + 1)
        } else if (!chosen.contains(conn.source) && chosen.contains(conn.target)) {
          degree.put(conn.source, degree.getOrElse(conn.source, 0) + 1)
        }
      }))
    if (degree.isEmpty) {
      null
    } else {
      degree.maxBy(_._2)._1
    }
  }

  private def buildPattern(
      root: String,
      chosenNodes: mutable.HashSet[String],
      chosenEdges: mutable.HashSet[Connection]): List[Pattern] = {
    val connections = mutable.HashSet[Connection]()
    val nodes = mutable.HashMap[String, PatternElement]()
    val specialConnections = new mutable.HashSet[Connection]()
    pattern.edges.foreach(pair =>
      pair._2.foreach(conn => {
        conn match {
          case PatternConnection(_, _, _, _, _, _, _, _, false) =>
            if (conn.source.equals(root) && !chosenNodes.contains(conn.target)) {
              connections.add(conn)
              nodes.put(conn.target, pattern.getNode(conn.target))
            } else if (conn.target.equals(root) && !chosenNodes.contains(conn.source)) {
              connections.add(conn.reverse)
              nodes.put(conn.source, pattern.getNode(conn.source))
            }
          case _ =>
            if (conn.source.equals(root) && !chosenNodes.contains(conn.target)) {
              specialConnections.add(conn)
            } else if (conn.target.equals(root) && !chosenNodes.contains(conn.source)) {
              specialConnections.add(conn.reverse)
            }
        }
        chosenEdges.add(conn)
      }))

    val result = new ListBuffer[Pattern]()
    if (connections.isEmpty) {
      result.append(NodePattern(pattern.getNode(root)))
    } else {
      nodes.put(root, pattern.getNode(root))
      result.append(PartialGraphPattern(root, nodes.toMap, Map.apply((root, connections.toSet))))
    }

    for (conn <- specialConnections) {
      result.append(EdgePattern(pattern.getNode(root), pattern.getNode(conn.target), conn))
    }
    result.toList
  }

}
