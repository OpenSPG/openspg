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

package com.antgroup.openspg.reasoner.lube.block

import scala.collection.mutable

import com.antgroup.openspg.reasoner.lube.common.graph.{IRArray, IREdge, IRGraph, IRNode, IRPath}
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  Connection,
  GraphPath,
  VariablePatternConnection
}

/**
 * parse from "GraphStructure" block to match path
 * @param dependencies
 * @param binds
 * @param patterns
 * @param graph
 */
final case class MatchBlock(
    dependencies: List[Block],
    patterns: Map[String, GraphPath],
    graph: IRGraph)
    extends BasicBlock[Binds](BlockType("match")) {

  override def binds: Binds = {
    val nodes = patterns.values
      .flatMap(path =>
        path.graphPattern.nodes.map(node => IRNode(node._1, new mutable.HashSet[String]())))
      .toList
    val edges = patterns.values
      .flatMap(path =>
        path.graphPattern.edges
          .map(pair => pair._2.map(rel => edgeToIRField(rel)).toList)
          .flatten)
      .toList
    Fields(nodes.++(edges))
  }

  private def edgeToIRField(edge: Connection) = {
    edge match {
      case connection: VariablePatternConnection =>
        val start = IRNode(connection.source, new mutable.HashSet[String]())
        val end = IRNode(connection.target, new mutable.HashSet[String]())
        val irEdge = IREdge(connection.alias, new mutable.HashSet[String]())
        val path = IRPath(connection.alias, List.apply(start, irEdge, end))
        IRArray(path)
      case _ =>
        IREdge(edge.alias, new mutable.HashSet[String]())
    }
  }

}
