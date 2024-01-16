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

package com.antgroup.openspg.reasoner.lube.block

import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode, IRPath, IRRepeatPath}
import com.antgroup.openspg.reasoner.lube.common.pattern.{Connection, GraphPath, VariablePatternConnection}

/**
 * parse from "GraphStructure" block to match path
 * @param dependencies
 * @param binds
 * @param patterns
 * @param graph
 */
final case class MatchBlock(
    dependencies: List[Block],
    patterns: Map[String, GraphPath])
    extends BasicBlock[Binds](BlockType("match")) {

  override def binds: Binds = {
    val props = patterns.values.head.graphPattern.properties
    val nodes = patterns.values
      .flatMap(path =>
        path.graphPattern.nodes.map(node => IRNode(node._1, props(node._1))))
      .toList
    val edges = patterns.values
      .flatMap(path =>
        path.graphPattern.edges
          .map(pair => pair._2.map(rel => edgeToIRField(rel, props)).toList)
          .flatten)
      .toList
    Fields(nodes.++(edges))
  }

  private def edgeToIRField(edge: Connection, props: Map[String, Set[String]]) = {
    edge match {
      case connection: VariablePatternConnection =>
        val start = IRNode(connection.source, props(connection.source))
        val end = IRNode(connection.target, props(connection.target))
        val irEdge = IREdge(connection.alias, props(connection.alias))
        val path = IRPath(connection.alias, List.apply(start, irEdge, end))
        IRRepeatPath(path, connection.lower, connection.upper)
      case _ =>
        IREdge(edge.alias, props(edge.alias))
    }
  }

}
