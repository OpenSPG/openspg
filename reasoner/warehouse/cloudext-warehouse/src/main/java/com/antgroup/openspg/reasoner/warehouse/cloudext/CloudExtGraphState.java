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

package com.antgroup.openspg.reasoner.warehouse.cloudext;

import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.OneHopLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.ScanLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.VertexLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexBizId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexId;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.graphstate.model.MergeTypeEnum;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
public class CloudExtGraphState extends MemGraphState {

  private final BaseLPGGraphStoreClient lpgGraphStoreClient;

  public CloudExtGraphState(String connUrl) {
    lpgGraphStoreClient =
        (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(connUrl);
  }

  @Override
  public void init(Map<String, String> param) {
    super.init(param);
  }

  @Override
  public IVertex<IVertexId, IProperty> getVertex(IVertexId id, Long version) {
    IVersionProperty iProperty = (IVersionProperty) vertexMap.get(id);
    if (iProperty != null) {
      return super.getVertex(id, version);
    }

    GraphLPGRecordStruct recordStruct =
        (GraphLPGRecordStruct)
            lpgGraphStoreClient.queryRecord(
                new VertexLPGRecordQuery(((VertexBizId) id).getBizId(), id.getType()));

    if (recordStruct.isEmpty()) {
      return null;
    }

    for (VertexRecord vertexRecord : recordStruct.getVertices()) {
      VertexVersionProperty property =
          new VertexVersionProperty(vertexRecord.toPropertyMapWithIdAndVersion());
      return new Vertex<>(
          new VertexBizId(vertexRecord.getId(), vertexRecord.getVertexType()), property);
    }
    return null;
  }

  @Override
  public void mergeVertexProperty(
      IVertexId id, Map<String, Object> property, MergeTypeEnum mergeType, Long version) {
    IProperty iProperty = vertexMap.get(id);
    if (iProperty == null) {
      IVertex<IVertexId, IProperty> vertex = getVertex(id, version);
      addVertex(vertex, version);
    }
    super.mergeVertexProperty(id, property, mergeType, version);
  }

  @Override
  public void mergeEdgeProperty(
      IVertexId s,
      String p,
      IVertexId o,
      Long version,
      Direction direction,
      Map<String, Object> property,
      MergeTypeEnum mergeType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<IVertex<IVertexId, IProperty>> getVertexIterator(Set<String> vertexTypes) {
    if (CollectionUtils.isEmpty(vertexTypes)) {
      return Collections.emptyIterator();
    }

    List<VertexRecord> vertexRecords = new ArrayList<>();
    for (String vertexType : vertexTypes) {
      GraphLPGRecordStruct recordStruct =
          (GraphLPGRecordStruct)
              lpgGraphStoreClient.queryRecord(new ScanLPGRecordQuery(vertexType, null));
      vertexRecords.addAll(recordStruct.getVertices());
    }

    if (CollectionUtils.isEmpty(vertexRecords)) {
      return Collections.emptyIterator();
    }

    return vertexRecords.stream()
        .map(
            vertexRecord -> {
              VertexVersionProperty property =
                  new VertexVersionProperty(vertexRecord.toPropertyMapWithIdAndVersion());
              return (IVertex<IVertexId, IProperty>)
                  new Vertex<IVertexId, IProperty>(
                      new VertexBizId(vertexRecord.getId(), vertexRecord.getVertexType()),
                      property);
            })
        .iterator();
  }

  @Override
  public List<IEdge<IVertexId, IProperty>> getEdges(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    List<IEdge<IVertexId, IProperty>> edges =
        super.getEdges(vertexId, startVersion, endVersion, types, direction);
    if (CollectionUtils.isNotEmpty(edges)) {
      return edges;
    }

    GraphLPGRecordStruct recordStruct =
        (GraphLPGRecordStruct)
            lpgGraphStoreClient.queryRecord(
                new OneHopLPGRecordQuery(
                    ((VertexBizId) vertexId).getBizId(),
                    vertexId.getType(),
                    types.stream().map(EdgeTypeName::parse).collect(Collectors.toSet()),
                    com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction.valueOf(
                        direction.name())));

    List<EdgeRecord> edgeRecords = recordStruct.getEdges();
    List<IEdge<IVertexId, IProperty>> results = new ArrayList<>(edgeRecords.size());
    for (EdgeRecord edgeRecord : edgeRecords) {
      VertexId srcVertexId =
          new VertexBizId(edgeRecord.getSrcId(), edgeRecord.getEdgeType().getStartVertexType());
      VertexId dstVertexId =
          new VertexBizId(edgeRecord.getDstId(), edgeRecord.getEdgeType().getEndVertexType());
      if (Direction.IN.equals(direction)) {
        // 当是IN边时，这里交换下src和dst，由于图存的方向和kgdsl需要的方向不一致
        VertexId tmp = srcVertexId;
        srcVertexId = dstVertexId;
        dstVertexId = tmp;
      }
      results.add(
          new Edge<>(
              srcVertexId,
              dstVertexId,
              new EdgeProperty(edgeRecord.toPropertyMapWithId()),
              0L,
              direction,
              edgeRecord.getEdgeType().toString()));
    }
    return results;
  }
}
