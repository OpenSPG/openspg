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

package com.antgroup.openspg.core.spgbuilder.engine.logical;

import com.antgroup.openspg.core.spgbuilder.model.pipeline.Edge;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.Node;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.Pipeline;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.CsvSourceNodeConfig;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.ExtractNodeConfig;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.GraphStoreSinkNodeConfig;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.MappingNodeConfig;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

@AllArgsConstructor
@SuppressWarnings({"UnstableApiUsage"})
public class LogicalPlan implements Serializable {

  /** DAG (Directed Acyclic Graph) of the logical execution plan. */
  private final Graph<BaseNode<?>> dag;

  /**
   * Converting the user-defined pipeline configuration into a logical execution plan.
   *
   * @param pipeline Spgbuilder pipeline.
   * @return Logical execution plan.
   */
  public static LogicalPlan parse(Pipeline pipeline) {
    ImmutableGraph.Builder<BaseNode<?>> immutable =
        GraphBuilder.directed().allowsSelfLoops(false).immutable();
    Map<String, BaseNode<?>> visited = new HashMap<>(pipeline.getNodes().size());
    for (Node node : pipeline.getNodes()) {
      BaseNode<?> baseNode = parse(node);

      if (visited.containsKey(node.getId())) {
        throw new IllegalArgumentException("pipeline config is error");
      } else {
        immutable.addNode(baseNode);
        visited.put(node.getId(), baseNode);
      }
    }

    for (Edge edge : pipeline.getEdges()) {
      BaseNode<?> fromNode = visited.get(edge.getFrom());
      BaseNode<?> toNode = visited.get(edge.getTo());
      if (fromNode == null || toNode == null) {
        throw new IllegalArgumentException("pipeline config is error");
      }
      immutable.putEdge(fromNode, toNode);
    }
    return new LogicalPlan(immutable.build());
  }

  /**
   * Node instantiation is done through node configuration.
   *
   * @param node Node config.
   * @return Instantiated node.
   */
  private static BaseNode<?> parse(Node node) {
    switch (node.getType()) {
      case CSV_SOURCE:
        return new CsvSourceNode(
            node.getId(), node.getName(), (CsvSourceNodeConfig) node.getNodeConfig());
      case EXTRACT:
        return new ExtractNode(
            node.getId(), node.getName(), (ExtractNodeConfig) node.getNodeConfig());
      case MAPPING:
        return new MappingNode(
            node.getId(), node.getName(), (MappingNodeConfig) node.getNodeConfig());
      case GRAPH_SINK:
        return new GraphStoreSinkNode(
            node.getId(), node.getName(), (GraphStoreSinkNodeConfig) node.getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + node.getType());
    }
  }

  public Set<BaseNode<?>> startNodes() {
    return dag.nodes().stream()
        .filter(node -> CollectionUtils.isEmpty(dag.predecessors(node)))
        .collect(Collectors.toSet());
  }

  public Set<BaseNode<?>> successors(BaseNode<?> cur) {
    return dag.successors(cur);
  }

  public int size() {
    return dag.nodes().size();
  }
}
