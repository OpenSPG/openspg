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
import com.antgroup.openspg.builder.core.physical.process.BaseProcessor;
import com.antgroup.openspg.builder.core.physical.process.ExtractProcessor;
import com.antgroup.openspg.builder.core.physical.process.MappingProcessor;
import com.antgroup.openspg.builder.model.pipeline.config.ExtractNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@SuppressWarnings({"UnstableApiUsage"})
public class PhysicalPlan implements Serializable {

  /** DAG (Directed Acyclic Graph) of the physical execution plan. */
  private final Graph<BaseProcessor<?>> dag;

  /**
   * Translating the logical execution plan into a physical execution plan.
   *
   * @param logicalPlan: Logical execution plan.
   * @return Physical execution plan
   */
  public static PhysicalPlan plan(LogicalPlan logicalPlan) {
    // first, remove the source and sink of the logical plan
    logicalPlan = logicalPlan.removeSourceAndSink();

    ImmutableGraph.Builder<BaseProcessor<?>> immutable =
        GraphBuilder.directed().allowsSelfLoops(false).immutable();

    Queue<BaseLogicalNode<?>> queue = new LinkedList<>();
    Map<String, BaseProcessor<?>> id2Node = new HashMap<>(logicalPlan.size());
    for (BaseLogicalNode<?> node : logicalPlan.startNodes()) {
      queue.add(node);
      BaseProcessor<?> physicalNode = parse(node);
      immutable.addNode(physicalNode);
      id2Node.put(physicalNode.getId(), physicalNode);
    }

    while (!queue.isEmpty()) {
      BaseLogicalNode<?> cur = queue.poll();

      for (BaseLogicalNode<?> successor : logicalPlan.successors(cur)) {
        queue.add(successor);
        BaseProcessor<?> curPhysicalNode = id2Node.get(successor.getId());
        if (curPhysicalNode == null) {
          curPhysicalNode = parse(successor);
          immutable.addNode(curPhysicalNode);
          id2Node.put(curPhysicalNode.getId(), curPhysicalNode);
        }

        BaseProcessor<?> prePhysicalNode = id2Node.get(cur.getId());
        immutable.putEdge(prePhysicalNode, curPhysicalNode);
      }
    }
    return new PhysicalPlan(immutable.build());
  }

  /**
   * Parse logical execution node into physical execution processor.
   *
   * @param node: Logical execution node.
   * @return Physical execution node.
   */
  private static BaseProcessor<?> parse(BaseLogicalNode<?> node) {
    switch (node.getType()) {
      case EXTRACT:
        return new ExtractProcessor(
            node.getId(), node.getName(), (ExtractNodeConfig) node.getNodeConfig());
      case MAPPING:
        return new MappingProcessor(
            node.getId(), node.getName(), (MappingNodeConfig) node.getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal type=" + node.getType());
    }
  }

  public Set<BaseProcessor<?>> nodes() {
    return dag.nodes();
  }

  public Set<BaseProcessor<?>> startProcessor() {
    return dag.nodes().stream().filter(x -> dag.inDegree(x) == 0).collect(Collectors.toSet());
  }

  public Set<BaseProcessor<?>> successors(BaseProcessor<?> physicalNode) {
    return dag.successors(physicalNode);
  }
}
