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
package com.antgroup.openspg.server.core.scheduler.model.task;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Task Dag Model ,Contains nodes and edges */
@Getter
@Setter
@ToString
public class JobTaskDag {

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
      boolean select = (next && edge.getFrom().equals(id)) || (!next && edge.getTo().equals(id));
      if (select) {
        idList.add(next ? edge.getTo() : edge.getFrom());
      }
    }
    return this.nodes.stream()
        .filter(node -> idList.contains(node.getId()))
        .collect(Collectors.toList());
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
    private JSONObject properties;
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
