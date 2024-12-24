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

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexBizId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
public class Utils {

  public static List<LinkedUdtfResult> getAllRdfEntity(
      GraphState<IVertexId> graphState, IVertexId id, String rdfType) {

    List<LinkedUdtfResult> result = new ArrayList<>();

    // find vertex prop
    IVertex<IVertexId, IProperty> vertex = graphState.getVertex(id, null, null);
    if (null != vertex && null != vertex.getValue() && !"relation".equals(rdfType)) {
      // 提取属性
      log.info("vertex_property,{}", vertex);
      for (String propertyName : vertex.getValue().getKeySet()) {
        Object pValue = vertex.getValue().get(propertyName);
        if (null == pValue || propertyName.startsWith("_")) {
          continue;
        }
        IVertex<IVertexId, IProperty> propVertex = new Vertex<>();
        propVertex.setId(new VertexBizId(String.valueOf(pValue), "Text"));
        propVertex.setValue(
            new VertexVersionProperty(
                "id", String.valueOf(pValue),
                "name", String.valueOf(pValue)));
        graphState.addVertex(propVertex);
        LinkedUdtfResult udtfRes = new LinkedUdtfResult();
        udtfRes.getDirection().add(Direction.OUT.name());
        udtfRes.setEdgeType(propertyName);
        udtfRes.getTargetVertexIdList().add(String.valueOf(pValue));
        if (pValue instanceof Integer) {
          udtfRes.getTargetVertexTypeList().add("Int");
        } else if (pValue instanceof Double || pValue instanceof Float) {
          udtfRes.getTargetVertexTypeList().add("Float");
        } else {
          udtfRes.getTargetVertexTypeList().add("Text");
        }
        udtfRes.getEdgePropertyMap().put("value", pValue);
        result.add(udtfRes);
      }
    }

    // 查询边数据
    List<IEdge<IVertexId, IProperty>> edgeList =
        graphState.getEdges(id, null, null, null, Direction.BOTH, new HashMap<>());
    if (CollectionUtils.isNotEmpty(edgeList)) {
      for (IEdge<IVertexId, IProperty> edge : edgeList) {
        Object toIdObj = edge.getValue().get(Constants.EDGE_TO_ID_KEY);
        String dir = Direction.OUT.name();
        Object nodeIdObj = vertex.getValue().get(Constants.NODE_ID_KEY);
        String targetType = edge.getTargetId().getType();
        if (nodeIdObj.equals(toIdObj)) {
          toIdObj = edge.getValue().get(Constants.EDGE_FROM_ID_KEY);
          dir = Direction.IN.name();
          targetType = String.valueOf(edge.getValue().get(Constants.EDGE_FROM_ID_TYPE_KEY));
        }
        if (null == toIdObj) {
          continue;
        }
        SPO spo = new SPO(edge.getType());
        log.info("TargetRdfProperty,id={},,edgeType={}", id, edge.getType());
        LinkedUdtfResult udtfRes = new LinkedUdtfResult();
        udtfRes.setEdgeType(spo.getP());
        udtfRes.getTargetVertexIdList().add(String.valueOf(toIdObj));
        udtfRes.getTargetVertexTypeList().add(targetType);
        udtfRes.getDirection().add(dir);
        for (String propKey : edge.getValue().getKeySet()) {
          if (propKey.startsWith("_")) {
            continue;
          }
          udtfRes.getEdgePropertyMap().put(propKey, edge.getValue().get(propKey));
        }
        result.add(udtfRes);
      }
    }
    return result;
  }
}
