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

package com.antgroup.openspg.builder.core.physical;

import com.antgroup.openspg.builder.core.logical.BaseLogicalNode;
import com.antgroup.openspg.builder.core.logical.LogicalPlan;
import com.antgroup.openspg.builder.core.physical.process.*;
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
public class PhysicalPlan implements Serializable {

  /** DAG (Directed Acyclic Graph) of the physical execution plan. */
  private final Graph<BaseProcessor<?>, DefaultEdge> dag;

  /**
   * Translating the logical execution plan into a physical execution plan.
   *
   * @param logicalPlan: Logical execution plan.
   * @return Physical execution plan
   */
  public static PhysicalPlan plan(LogicalPlan logicalPlan) {
    // first, remove the source and sink of the logical plan
    logicalPlan = logicalPlan.removeSourceAndSinkNodes();

    Graph<BaseProcessor<?>, DefaultEdge> graph = newGraph();
    Queue<BaseLogicalNode<?>> queue = new LinkedList<>();
    Map<String, BaseProcessor<?>> id2Node = new HashMap<>(logicalPlan.size());
    for (BaseLogicalNode<?> node : logicalPlan.sourceNodes()) {
      queue.add(node);
      BaseProcessor<?> physicalNode = parse(node);
      graph.addVertex(physicalNode);
      id2Node.put(physicalNode.getId(), physicalNode);
    }

    while (!queue.isEmpty()) {
      BaseLogicalNode<?> cur = queue.poll();

      for (BaseLogicalNode<?> successor : logicalPlan.successors(cur)) {
        queue.add(successor);
        BaseProcessor<?> curPhysicalNode = id2Node.get(successor.getId());
        if (curPhysicalNode == null) {
          curPhysicalNode = parse(successor);
          graph.addVertex(curPhysicalNode);
          id2Node.put(curPhysicalNode.getId(), curPhysicalNode);
        }

        BaseProcessor<?> prePhysicalNode = id2Node.get(cur.getId());
        graph.addEdge(prePhysicalNode, curPhysicalNode);
      }
    }
    return new PhysicalPlan(graph);
  }

  private static Graph<BaseProcessor<?>, DefaultEdge> newGraph() {
    return GraphTypeBuilder.<BaseProcessor<?>, DefaultEdge>directed()
        .allowingSelfLoops(false)
        .allowingMultipleEdges(false)
        .edgeClass(DefaultEdge.class)
        .weighted(false)
        .buildGraph();
  }

  /**
   * Parse logical execution node into physical execution processor.
   *
   * @param node: Logical execution node.
   * @return Physical execution node.
   */
  private static BaseProcessor<?> parse(BaseLogicalNode<?> node) {
    switch (node.getType()) {
      case USER_DEFINED_EXTRACT:
        return new UserDefinedExtractProcessor(
            node.getId(), node.getName(), (UserDefinedExtractNodeConfig) node.getNodeConfig());
      case LLM_BASED_EXTRACT:
        return new LLMBasedExtractProcessor(
            node.getId(), node.getName(), (LLMBasedExtractNodeConfig) node.getNodeConfig());
      case SPG_TYPE_MAPPING:
        return new SPGTypeMappingProcessor(
            node.getId(), node.getName(), (SPGTypeMappingNodeConfig) node.getNodeConfig());
      case RELATION_MAPPING:
        return new RelationMappingProcessor(
            node.getId(), node.getName(), (RelationMappingNodeConfig) node.getNodeConfig());
      case SUBGRAPH_MAPPING:
        return new SubgraphMappingProcessor(
            node.getId(), node.getName(), (SubGraphMappingNodeConfig) node.getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal type=" + node.getType());
    }
  }

  public Set<BaseProcessor<?>> nodes() {
    return dag.vertexSet();
  }

  public Set<BaseProcessor<?>> sourceNodes() {
    return dag.vertexSet().stream()
        .filter(node -> dag.inDegreeOf(node) == 0)
        .collect(Collectors.toSet());
  }

  public Set<BaseProcessor<?>> successors(BaseProcessor<?> cur) {
    return new HashSet<>(Graphs.successorListOf(dag, cur));
  }
}
