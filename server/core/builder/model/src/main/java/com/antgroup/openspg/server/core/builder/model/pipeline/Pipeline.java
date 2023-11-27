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

package com.antgroup.openspg.server.core.builder.model.pipeline;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.List;

/**
 * The knowledge processing pipeline constructed by knext sdk or the frontend based on business
 * needs.
 */
public class Pipeline extends BaseValObj {

  /** The list of nodes in the pipeline. */
  private final List<Node> nodes;

  /** The list of edge in the pipeline. */
  private final List<Edge> edges;

  public Pipeline(List<Node> nodes, List<Edge> edges) {
    this.nodes = nodes;
    this.edges = edges;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public List<Edge> getEdges() {
    return edges;
  }
}
