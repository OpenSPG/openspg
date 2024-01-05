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

package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.Edge;
import com.antgroup.openspg.builder.model.pipeline.Node;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.pipeline.config.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

@AllArgsConstructor
public class LogicalPlan implements Serializable {

  /** DAG (Directed Acyclic Graph) of the logical execution plan. */
  private final Graph<BaseLogicalNode<?>, DefaultEdge> dag;

  /**
   * Converting the user-defined pipeline configuration into a logical execution plan.
   *
   * @param pipeline builder pipeline.
   * @return Logical execution plan.
   */
  public static LogicalPlan parse(Pipeline pipeline) {
    Graph<BaseLogicalNode<?>, DefaultEdge> graph = newGraph();
    Map<String, BaseLogicalNode<?>> visited = new HashMap<>(pipeline.getNodes().size());
    for (Node node : pipeline.getNodes()) {
      BaseLogicalNode<?> baseNode = parse(node);

      if (visited.containsKey(node.getId())) {
        throw new IllegalArgumentException("pipeline config is error");
      } else {
        graph.addVertex(baseNode);
        visited.put(node.getId(), baseNode);
      }
    }

    for (Edge edge : pipeline.getEdges()) {
      BaseLogicalNode<?> fromNode = visited.get(edge.getFrom());
      BaseLogicalNode<?> toNode = visited.get(edge.getTo());
      if (fromNode == null || toNode == null) {
        throw new IllegalArgumentException("pipeline config is error");
      }
      graph.addEdge(fromNode, toNode);
    }
    return new LogicalPlan(graph);
  }

  private static Graph<BaseLogicalNode<?>, DefaultEdge> newGraph() {
    return GraphTypeBuilder.<BaseLogicalNode<?>, DefaultEdge>directed()
        .allowingSelfLoops(false)
        .allowingMultipleEdges(false)
        .edgeClass(DefaultEdge.class)
        .weighted(false)
        .buildGraph();
  }

  /**
   * Node instantiation is done through node configuration.
   *
   * @param node Node config.
   * @return Instantiated node.
   */
  private static BaseLogicalNode<?> parse(Node node) {
    switch (node.getType()) {
      case CSV_SOURCE:
        return new CsvSourceNode(
            node.getId(), node.getName(), (CsvSourceNodeConfig) node.getNodeConfig());
      case USER_DEFINED_EXTRACT:
        return new UserDefinedExtractNode(
            node.getId(), node.getName(), (UserDefinedExtractNodeConfig) node.getNodeConfig());
      case LLM_BASED_EXTRACT:
        return new LLMBasedExtractNode(
            node.getId(), node.getName(), (LLMBasedExtractNodeConfig) node.getNodeConfig());
      case SPG_TYPE_MAPPINGS:
        return new SPGTypeMappingNode(
            node.getId(), node.getName(), (SPGTypeMappingNodeConfigs) node.getNodeConfig());
      case RELATION_MAPPING:
        return new RelationMappingNode(
            node.getId(), node.getName(), (RelationMappingNodeConfig) node.getNodeConfig());
      case GRAPH_SINK:
        return new GraphStoreSinkNode(
            node.getId(), node.getName(), (GraphStoreSinkNodeConfig) node.getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + node.getType());
    }
  }

  public Set<BaseLogicalNode<?>> sourceNodes() {
    return dag.vertexSet().stream()
        .filter(node -> dag.inDegreeOf(node) == 0)
        .collect(Collectors.toSet());
  }

  public Set<BaseLogicalNode<?>> sinkNodes() {
    return dag.vertexSet().stream()
        .filter(node -> dag.outDegreeOf(node) == 0)
        .collect(Collectors.toSet());
  }

  public Set<BaseLogicalNode<?>> successors(BaseLogicalNode<?> cur) {
    return new HashSet<>(Graphs.successorListOf(dag, cur));
  }

  public int size() {
    return dag.vertexSet().size();
  }

  public LogicalPlan removeSourceAndSinkNodes() {
    Graph<BaseLogicalNode<?>, DefaultEdge> newGraph = newGraph();
    Graphs.addGraph(newGraph, dag);
    newGraph.removeAllVertices(sourceNodes());
    newGraph.removeAllVertices(sinkNodes());
    return new LogicalPlan(newGraph);
  }
}
