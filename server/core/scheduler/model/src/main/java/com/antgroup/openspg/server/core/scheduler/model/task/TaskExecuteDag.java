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
package com.antgroup.openspg.server.core.scheduler.model.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Task Dag Model ,Contains nodes and edges,scheduler is executed step by step according to DAG */
@Getter
@Setter
@ToString
public class TaskExecuteDag {

  /** dag nodes List */
  private List<Node> nodes = Collections.emptyList();

  /** dag edges List */
  private List<Edge> edges = Collections.emptyList();

  /** dag extend */
  private String extend;

  /** get Next/Pre Nodes */
  public List<Node> getRelatedNodes(String id, boolean next) {
    List<String> idList = Lists.newArrayList();
    for (Edge edge : edges) {
      if ((next && edge.getFrom().equals(id)) || (!next && edge.getTo().equals(id))) {
        idList.add(next ? edge.getTo() : edge.getFrom());
      }
    }
    return this.nodes.stream()
        .filter(node -> idList.contains(node.getId()))
        .collect(Collectors.toList());
  }

  @JSONField(serialize = false)
  public List<Node> getNodesByType(String nodeType) {
    List<Node> nodes = Lists.newArrayList();
    if (nodeType == null) {
      return nodes;
    }
    for (Node node : this.nodes) {
      if (node == null) {
        continue;
      }
      if (nodeType.equals(node.getTaskComponent())) {
        nodes.add(node);
      }
    }
    return nodes;
  }

  @JSONField(serialize = false)
  public List<Node> getSuccessorNodes(String startNodeId) {
    Set<String> visited = new HashSet<>();
    List<Node> result = new ArrayList<>();
    dfs(startNodeId, visited, result);
    return result;
  }

  private void dfs(String nodeId, Set<String> visited, List<Node> result) {
    if (visited.contains(nodeId)) {
      return;
    }
    visited.add(nodeId);

    List<Edge> outgoingEdges =
        edges.stream().filter(edge -> edge.getFrom().equals(nodeId)).collect(Collectors.toList());
    for (Edge edge : outgoingEdges) {
      Optional<Node> targetNodeOptional =
          nodes.stream().filter(node -> node.getId().equals(edge.getTo())).findFirst();
      if (targetNodeOptional.isPresent()) {
        Node targetNode = targetNodeOptional.get();
        result.add(targetNode);
        dfs(targetNode.getId(), visited, result);
      }
    }
  }

  @JSONField(serialize = false)
  public Node getNode(String nodeId) {
    for (Node node : nodes) {
      if (node.getId().equals(nodeId)) {
        return node;
      }
    }
    return null;
  }

  @Getter
  @Setter
  @ToString
  public static class Node {
    /** id */
    private String id;

    /** name */
    private String name;

    /** JobTask Component name */
    private String taskComponent;

    /** properties */
    private JSONObject properties = new JSONObject();
  }

  @Getter
  @Setter
  @ToString
  public static class Edge {
    /** from id */
    private String from;

    /** to id */
    private String to;
  }
}
