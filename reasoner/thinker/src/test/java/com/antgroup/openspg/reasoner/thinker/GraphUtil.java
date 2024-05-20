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

package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import java.util.*;

public class GraphUtil {
  public static Edge<IVertexId, IVersionProperty> makeEdge(
      Vertex<IVertexId, IProperty> from,
      Vertex<IVertexId, IProperty> to,
      String edgeType,
      Object... kvs) {
    Map<String, TreeMap<Long, Object>> props = new HashMap<>();
    Map<String, Object> propertyMap = Utils.convert2Property(kvs);
    propertyMap.put(Constants.EDGE_FROM_ID_TYPE_KEY, from.getId().getType());
    propertyMap.put(Constants.EDGE_TO_ID_TYPE_KEY, to.getId().getType());
    propertyMap.put(Constants.EDGE_FROM_ID_KEY, from.getValue().get(Constants.NODE_ID_KEY));
    propertyMap.put(Constants.EDGE_TO_ID_KEY, to.getValue().get(Constants.NODE_ID_KEY));
    for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
      TreeMap<Long, Object> valueMap = new TreeMap<>();
      valueMap.put(0L, entry.getValue());
      props.put(entry.getKey(), valueMap);
    }
    return new Edge<>(
        from.getId(), to.getId(), new VertexVersionProperty(props), 0L, Direction.OUT, edgeType);
  }

  public static Vertex<IVertexId, IProperty> makeVertex(String id, String type, Object... kvs) {
    Map<String, TreeMap<Long, Object>> props = new HashMap<>();
    Map<String, Object> propertyMap = Utils.convert2Property(kvs);
    propertyMap.put(Constants.NODE_ID_KEY, id);
    for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
      TreeMap<Long, Object> valueMap = new TreeMap<>();
      valueMap.put(0L, entry.getValue());
      props.put(entry.getKey(), valueMap);
    }
    return new Vertex<>(IVertexId.from(id, type), new VertexVersionProperty(props));
  }

  public static MemGraphState buildMemState(
      List<IVertex<IVertexId, IProperty>> vertexList, List<IEdge<IVertexId, IProperty>> edgeList) {
    MemGraphState graphState = new MemGraphState();
    for (IVertex<IVertexId, IProperty> v : vertexList) {
      graphState.addVertex(v);
      List<IEdge<IVertexId, IProperty>> outEdges = new LinkedList<>();
      List<IEdge<IVertexId, IProperty>> inEdges = new LinkedList<>();
      for (IEdge<IVertexId, IProperty> e : edgeList) {
        if (v.getId().equals(e.getSourceId())) {
          outEdges.add(e);
        } else if (v.getId().equals(e.getTargetId())) {
          inEdges.add(e.reverse());
        }
      }
      graphState.addEdges(v.getId(), inEdges, outEdges);
    }
    return graphState;
  }
}
