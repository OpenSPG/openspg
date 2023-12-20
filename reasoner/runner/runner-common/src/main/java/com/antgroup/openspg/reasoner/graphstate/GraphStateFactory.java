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

package com.antgroup.openspg.reasoner.graphstate;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GraphStateFactory {
  private static final Map<Integer, GraphState<IVertexId>> GRAPH_STATE_MAP =
      new ConcurrentHashMap<>();

  /** return graph state on worker */
  public static GraphState<IVertexId> getGraphState(int index) {
    return GRAPH_STATE_MAP.get(index);
  }

  /** init graph state */
  public static void putGraphState(int index, GraphState<IVertexId> graphState) {
    GRAPH_STATE_MAP.put(index, graphState);
  }
}
