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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import scala.Tuple2;

public class KgGraphEdgeDirectionImpl implements Serializable {

  private final Map<String, Tuple2<Direction, Direction>> diffEdgeDirectionMap;

  public KgGraphEdgeDirectionImpl(Map<String, Tuple2<Direction, Direction>> diffEdgeDirectionMap) {
    this.diffEdgeDirectionMap = diffEdgeDirectionMap;
  }

  public KgGraph<IVertexId> convert(KgGraphImpl kgGraph) {
    for (String edgeAlias : this.diffEdgeDirectionMap.keySet()) {
      Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>();
      for (IEdge<IVertexId, IProperty> edge : kgGraph.getAlias2EdgeMap().get(edgeAlias)) {
        newEdgeSet.add(edge.reverse());
      }
      kgGraph.getAlias2EdgeMap().put(edgeAlias, newEdgeSet);
    }
    return kgGraph;
  }
}
