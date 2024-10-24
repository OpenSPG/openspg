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

package com.antgroup.openspg.cloudext.impl.graphstore.neo4j;

import com.antgroup.openspg.cloudext.impl.graphstore.neo4j.util.Neo4jConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.*;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.ComputeResultRow;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.BaseLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGSchema;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;
import com.antgroup.openspg.common.util.neo4j.Neo4jAdminUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jCommonUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jDataUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jGraphUtils;
import com.antgroup.openspg.common.util.neo4j.model.RelationWithAdjacentNodes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.neo4j.driver.types.Node;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class Neo4jStoreClient extends BaseLPGGraphStoreClient {

  private final Neo4jDataUtils dataUtil;
  private final Neo4jGraphUtils graphUtil;

  @Getter private final String connUrl;

  public Neo4jStoreClient(String connUrl) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(connUrl).build();
    this.connUrl = connUrl;
    Neo4jAdminUtils neo4jClient = initNeo4jClient(uriComponents);
    this.dataUtil = neo4jClient.neo4jData;
    this.graphUtil = neo4jClient.neo4jGraph;
  }

  private Neo4jAdminUtils initNeo4jClient(UriComponents uriComponents) {
    String host =
        String.format(
            "%s://%s:%s",
            uriComponents.getScheme(), uriComponents.getHost(), uriComponents.getPort());
    String user = uriComponents.getQueryParams().getFirst(Neo4jCommonUtils.USER);
    String password = uriComponents.getQueryParams().getFirst(Neo4jCommonUtils.PASSWORD);
    String database = uriComponents.getQueryParams().getFirst(Neo4jCommonUtils.DATABASE);
    return new Neo4jAdminUtils(host, user, password, database);
  }

  @Override
  public void close() throws Exception {}

  @Override
  public LPGSchema querySchema() {
    return null;
  }

  @Override
  public List<String> queryAllVertexLabels() {
    return this.graphUtil.getAllLabels();
  }

  @Override
  public boolean createVertexType(CreateVertexTypeOperation operation) {
    return true;
  }

  @Override
  public boolean createEdgeType(CreateEdgeTypeOperation operation) {
    return true;
  }

  @Override
  public boolean alterVertexType(AlterVertexTypeOperation operation) {
    return true;
  }

  @Override
  public boolean alterEdgeType(AlterEdgeTypeOperation operation) {
    return true;
  }

  @Override
  public boolean dropVertexType(DropVertexTypeOperation operation) {
    return true;
  }

  @Override
  public boolean dropEdgeType(DropEdgeTypeOperation operation) {
    return true;
  }

  @Override
  public boolean batchTransactionalSchemaOperations(List<BaseLPGSchemaOperation> operations) {
    return false;
  }

  @Override
  public void upsertVertex(@NonNull String vertexTypeName, List<VertexRecord> vertexRecords)
      throws Exception {
    Long statr = System.currentTimeMillis();
    if (CollectionUtils.isEmpty(vertexRecords)) {
      return;
    }
    String label = vertexRecords.get(0).getVertexType();

    List<Map<String, Object>> propertiesList =
        vertexRecords.stream()
            .map(
                record -> {
                  Map<String, Object> property = record.toPropertyMap();
                  property.put(Neo4jCommonUtils.ID, record.getId());
                  return property;
                })
            .collect(Collectors.toList());

    Set<String> extraLabelsInNeo4j = Sets.newHashSet("Entity");

    if (vertexRecords.size() == 1) {
      dataUtil.upsertNode(label, propertiesList.get(0), Neo4jCommonUtils.ID, extraLabelsInNeo4j);
    } else {
      dataUtil.upsertNodes(label, propertiesList, Neo4jCommonUtils.ID, extraLabelsInNeo4j);
    }
    log.info(String.format("upsertVertex cons:%s", System.currentTimeMillis() - statr));
  }

  @Override
  public void deleteVertex(@NonNull String vertexTypeName, List<VertexRecord> vertexRecords)
      throws Exception {
    if (CollectionUtils.isEmpty(vertexRecords)) {
      return;
    }
    String label = vertexRecords.get(0).getVertexType();

    if (vertexRecords.size() == 1) {
      dataUtil.deleteNode(label, vertexRecords.get(0).getId(), Neo4jCommonUtils.ID);
    } else {
      List<String> idValues =
          vertexRecords.stream().map(VertexRecord::getId).collect(Collectors.toList());
      dataUtil.deleteNodes(label, idValues, Neo4jCommonUtils.ID);
    }
  }

  @Override
  public void upsertEdge(@NonNull String edgeTypeName, List<EdgeRecord> edgeRecords)
      throws Exception {
    upsertEdge(edgeTypeName, edgeRecords, true);
  }

  @Override
  public void upsertEdge(
      @NonNull String edgeTypeName, List<EdgeRecord> edgeRecords, boolean upsertAdjacentVertices)
      throws Exception {
    if (CollectionUtils.isEmpty(edgeRecords)) {
      return;
    }
    String srcNodeLabel = edgeRecords.get(0).getEdgeType().getStartVertexType();
    String dstNodeLabel = edgeRecords.get(0).getEdgeType().getEndVertexType();
    String edgeLabel = edgeRecords.get(0).getEdgeType().getEdgeLabel();

    if (edgeRecords.size() == 1) {
      EdgeRecord edgeRecord = edgeRecords.get(0);
      dataUtil.upsertRelationship(
          srcNodeLabel,
          edgeRecord.getSrcId(),
          dstNodeLabel,
          edgeRecord.getDstId(),
          edgeLabel,
          edgeRecord.toPropertyMap(),
          upsertAdjacentVertices,
          Neo4jCommonUtils.ID,
          Neo4jCommonUtils.ID);
    } else {
      List<Map<String, Object>> relationships =
          edgeRecords.stream()
              .map(
                  record -> {
                    Map<String, Object> relationship = Maps.newHashMap();
                    relationship.put(Neo4jDataUtils.KEY_START_NODE_ID, record.getSrcId());
                    relationship.put(Neo4jDataUtils.KEY_END_NODE_ID, record.getDstId());
                    relationship.put(Neo4jDataUtils.KEY_PROPERTIES, record.toPropertyMap());
                    return relationship;
                  })
              .collect(Collectors.toList());
      dataUtil.upsertRelationships(
          srcNodeLabel,
          dstNodeLabel,
          edgeLabel,
          relationships,
          upsertAdjacentVertices,
          Neo4jCommonUtils.ID,
          Neo4jCommonUtils.ID);
    }
  }

  @Override
  public void deleteEdge(@NonNull String edgeTypeName, List<EdgeRecord> edgeRecords)
      throws Exception {
    if (CollectionUtils.isEmpty(edgeRecords)) {
      return;
    }
    String srcNodeLabel = edgeRecords.get(0).getEdgeType().getStartVertexType();
    String dstNodeLabel = edgeRecords.get(0).getEdgeType().getEndVertexType();
    String edgeLabel = edgeRecords.get(0).getEdgeType().getEdgeLabel();

    if (edgeRecords.size() == 1) {
      dataUtil.deleteRelationship(
          srcNodeLabel,
          edgeRecords.get(0).getSrcId(),
          dstNodeLabel,
          edgeRecords.get(0).getDstId(),
          edgeLabel,
          Neo4jCommonUtils.ID,
          Neo4jCommonUtils.ID);
    } else {
      List<String> srcNodeIdValues =
          edgeRecords.stream().map(EdgeRecord::getSrcId).collect(Collectors.toList());
      List<String> dstNodeIdValues =
          edgeRecords.stream().map(EdgeRecord::getDstId).collect(Collectors.toList());
      dataUtil.deleteRelationships(
          srcNodeLabel,
          srcNodeIdValues,
          dstNodeLabel,
          dstNodeIdValues,
          edgeLabel,
          Neo4jCommonUtils.ID,
          Neo4jCommonUtils.ID);
    }
  }

  @Override
  public BaseLPGRecordStruct queryRecord(@NonNull BaseLPGRecordQuery query) {
    switch (query.getQueryType()) {
      case VERTEX:
        VertexLPGRecordQuery vertexQuery = (VertexLPGRecordQuery) query;
        return querySingleVertex(vertexQuery.getVertexId(), vertexQuery.getVertexName());
      case BATCH_VERTEX:
        BatchVertexLPGRecordQuery batchVertexQuery = (BatchVertexLPGRecordQuery) query;
        return batchQueryVertex(batchVertexQuery.getVertexIds(), batchVertexQuery.getVertexName());
      case SCAN:
        ScanLPGRecordQuery scanQuery = (ScanLPGRecordQuery) query;
        return scanVertex((String) scanQuery.getTypeName(), scanQuery.getLimit());
      case ONE_HOP_SUBGRAPH:
        OneHopLPGRecordQuery oneHopQuery = (OneHopLPGRecordQuery) query;
        return queryOneHop(
            oneHopQuery.getSrcVertexId(),
            oneHopQuery.getSrcVertexName(),
            oneHopQuery.getDirection(),
            oneHopQuery.getEdgeNames());
      default:
        throw new NotImplementedException("unsupported query type:" + query.getQueryType());
    }
  }

  private GraphLPGRecordStruct querySingleVertex(@NonNull String bizId, @NonNull String typeName) {
    GraphLPGRecordStruct queryResult = new GraphLPGRecordStruct();

    Node node = dataUtil.querySingleNode(Neo4jCommonUtils.ID, bizId, typeName);
    if (node != null) {
      VertexRecord vertexRecord = Neo4jConvertor.convert2VertexRecord(node);
      queryResult.getVertices().add(vertexRecord);
    }

    return queryResult;
  }

  private GraphLPGRecordStruct batchQueryVertex(
      @NonNull Set<String> bizIdList, @NonNull String typeName) {
    GraphLPGRecordStruct queryResult = new GraphLPGRecordStruct();

    List<Node> nodes = dataUtil.queryNodeByIdValues(Neo4jCommonUtils.ID, bizIdList, typeName);
    if (CollectionUtils.isNotEmpty(nodes)) {
      for (Node node : nodes) {
        VertexRecord vertexRecord = Neo4jConvertor.convert2VertexRecord(node);
        queryResult.getVertices().add(vertexRecord);
      }
    }

    return queryResult;
  }

  private GraphLPGRecordStruct scanVertex(@NonNull String typeName, Integer limit) {
    GraphLPGRecordStruct queryResult = new GraphLPGRecordStruct();

    List<Node> resultList = dataUtil.scanNodes(typeName, limit);
    if (CollectionUtils.isNotEmpty(resultList)) {
      resultList.forEach(
          node -> {
            VertexRecord vertexRecord = Neo4jConvertor.convert2VertexRecord(node);
            queryResult.getVertices().add(vertexRecord);
          });
    }

    return queryResult;
  }

  private GraphLPGRecordStruct queryOneHop(
      @NonNull String bizId,
      @NonNull String typeName,
      @NonNull Direction direction,
      Set<EdgeTypeName> edgeTypeNames) {
    GraphLPGRecordStruct queryResult = new GraphLPGRecordStruct();

    // query in-edge
    if (direction == Direction.IN || direction == Direction.BOTH) {
      GraphLPGRecordStruct inEdgeQueryResult =
          queryRelationships(
              bizId,
              typeName,
              Direction.IN,
              filterBySourceAndDirection(edgeTypeNames, typeName, Direction.IN));
      queryResult.addAll(inEdgeQueryResult);
    }

    // query out-edge
    if (direction == Direction.OUT || direction == Direction.BOTH) {
      GraphLPGRecordStruct outEdgeQueryResult =
          queryRelationships(
              bizId,
              typeName,
              Direction.OUT,
              filterBySourceAndDirection(edgeTypeNames, typeName, Direction.OUT));
      queryResult.addAll(outEdgeQueryResult);
    }
    return queryResult;
  }

  private Set<EdgeTypeName> filterBySourceAndDirection(
      Set<EdgeTypeName> edgeTypeNames, String sourceVertexTypeName, Direction direction) {
    if (edgeTypeNames == null) {
      return null;
    }
    Set<EdgeTypeName> result = new HashSet<>();
    for (EdgeTypeName edgeTypeName : edgeTypeNames) {
      if (direction != Direction.IN
          && sourceVertexTypeName.equals(edgeTypeName.getStartVertexType())) {
        result.add(edgeTypeName);
      } else if (direction != Direction.OUT
          && sourceVertexTypeName.equals(edgeTypeName.getEndVertexType())) {
        result.add(edgeTypeName);
      }
    }
    return result;
  }

  private GraphLPGRecordStruct queryRelationships(
      @NonNull String bizId,
      @NonNull String typeName,
      @NonNull Direction direction,
      Set<EdgeTypeName> edgeTypeConstraint) {
    if (edgeTypeConstraint != null && edgeTypeConstraint.isEmpty()) {
      return querySingleVertex(bizId, typeName);
    }
    GraphLPGRecordStruct result = new GraphLPGRecordStruct();

    List<RelationWithAdjacentNodes> queryResult =
        dataUtil.queryRelationships(
            Neo4jCommonUtils.ID,
            bizId,
            typeName,
            Direction.OUT == direction,
            edgeTypeConstraint == null
                ? null
                : edgeTypeConstraint.stream()
                    .map(Neo4jConvertor::convert2RelationLabelConstraint)
                    .collect(Collectors.toSet()));

    if (CollectionUtils.isEmpty(queryResult)) {
      return result;
    }

    Map<String, VertexRecord> vertexRecords = Maps.newHashMap();
    Map<String, EdgeRecord> edgeRecords = Maps.newHashMap();

    for (RelationWithAdjacentNodes relationWithAdjacentNodes : queryResult) {
      VertexRecord startVertex =
          Neo4jConvertor.convert2VertexRecord(relationWithAdjacentNodes.getStartNode());
      vertexRecords.put(startVertex.generateUniqueString(), startVertex);

      VertexRecord endVertex =
          Neo4jConvertor.convert2VertexRecord(relationWithAdjacentNodes.getEndNode());
      vertexRecords.put(endVertex.generateUniqueString(), endVertex);

      EdgeRecord edge =
          Neo4jConvertor.buildEdgeRecord(
              startVertex, relationWithAdjacentNodes.getRelationship(), endVertex);
      edgeRecords.put(edge.generateUniqueString(), edge);
    }
    return new GraphLPGRecordStruct(
        new ArrayList<>(vertexRecords.values()), new ArrayList<>(edgeRecords.values()));
  }

  @Override
  public List<ComputeResultRow> runPageRank(PageRankCompete compete) {
    List<Map<String, String>> startNodes =
        compete.getStartVertices().stream()
            .map(
                vertexRecord -> {
                  Map<String, String> startNode = Maps.newHashMap();
                  startNode.put(Neo4jCommonUtils.ID, vertexRecord.getId());
                  startNode.put(Neo4jCommonUtils.TYPE, vertexRecord.getVertexType());
                  return startNode;
                })
            .collect(Collectors.toList());
    List<Map<String, Object>> pageRankResults =
        graphUtil.getPageRankScores(startNodes, compete.getTargetVertexType());
    return pageRankResults.stream()
        .map(
            result -> {
              String id = (String) result.get(Neo4jCommonUtils.ID);
              double score = (double) result.get(Neo4jCommonUtils.SCORE);
              return new ComputeResultRow(
                  new VertexRecord(id, compete.getTargetVertexType(), Lists.newArrayList()), score);
            })
        .collect(Collectors.toList());
  }
}
