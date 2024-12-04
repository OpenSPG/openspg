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
package com.antgroup.openspg.server.core.reasoner.service.impl;

import com.antgroup.openspg.common.util.tuple.Tuple2;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexBizId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author donghai.ydh
 * @version Utils.java, v 0.1 2024-04-17 10:32 donghai.ydh
 */
@Slf4j
public class Utils {

  public static Map<String, Set<Tuple2<String, String>>> getAllRdfEntity(
      GraphState<IVertexId> graphState, IVertexId id) {

    Map<String, Set<Tuple2<String, String>>> result = new HashMap<>();

    // find vertex prop
    IVertex<IVertexId, IProperty> vertex = graphState.getVertex(id, null, null);
    if (null != vertex && null != vertex.getValue()) {
      // 提取属性
      log.info("vertex_property,{}", vertex);
      for (String propertyName : vertex.getValue().getKeySet()) {
        Object pValue = vertex.getValue().get(propertyName);
        if (null == pValue || propertyName.startsWith("_")) {
          continue;
        }
        IVertex<IVertexId, IProperty> propVertex = new Vertex<>();
        propVertex.setId(new VertexBizId(String.valueOf(pValue), "Text"));
        propVertex.setValue(new VertexVersionProperty(
                "id", String.valueOf(pValue),
                "name", String.valueOf(pValue)
        ));
        graphState.addVertex(propVertex);
        result
            .computeIfAbsent(propertyName, k -> new HashSet<>())
            .add(new Tuple2<>(String.valueOf(pValue), "Text"));
      }
    }

    // 查询边数据
    List<IEdge<IVertexId, IProperty>> edgeList =
        graphState.getEdges(id, null, null, null, Direction.BOTH, new HashMap<>());
    if (CollectionUtils.isNotEmpty(edgeList)) {
      for (IEdge<IVertexId, IProperty> edge : edgeList) {
        Object toIdObj = edge.getValue().get(Constants.EDGE_TO_ID_KEY);
        if (null == toIdObj) {
          continue;
        }
        SPO spo = new SPO(edge.getType());
        log.info("TargetRdfProperty,id={},,edgeType={}", id, edge.getType());
        result
            .computeIfAbsent(spo.getP(), k -> new HashSet<>())
            .add(new Tuple2<>(String.valueOf(toIdObj), edge.getTargetId().getType()));
      }
    }
    return result;
  }
}
