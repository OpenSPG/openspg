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

package com.antgroup.openspg.server.biz.service.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.OneHopLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.PageRankCompete;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.VertexLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.ComputeResultRow;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.server.api.facade.dto.service.request.*;
import com.antgroup.openspg.server.api.facade.dto.service.response.ExpendOneHopResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.ManipulateDataResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.PageRankScoreInstance;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.service.GraphManager;
import com.antgroup.openspg.server.biz.service.convertor.InstanceConvertor;
import com.google.common.collect.Lists;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphManagerImpl implements GraphManager {

  @Autowired private ProjectManager projectManager;

  @Override
  public List<String> getAllLabels(GetAllLabelsRequest request) {
    String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    BaseLPGGraphStoreClient lpgGraphStoreClient =
        (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);
    return lpgGraphStoreClient.queryAllVertexLabels();
  }

  @Override
  public ManipulateDataResponse upsertVertex(UpsertVertexRequest request) {
    if (CollectionUtils.isEmpty(request.getVertices())) {
      return ManipulateDataResponse.ofSuccess();
    }

    try {
      String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
      BaseLPGGraphStoreClient lpgGraphStoreClient =
          (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

      String type = request.getVertices().get(0).getType();
      List<VertexRecord> records =
          request.getVertices().stream()
              .map(InstanceConvertor::toVertexRecord)
              .collect(Collectors.toList());

      lpgGraphStoreClient.upsertVertex(type, records);
    } catch (Exception e) {
      return ManipulateDataResponse.ofFailure(e.getMessage());
    }

    return ManipulateDataResponse.ofSuccess();
  }

  @Override
  public ManipulateDataResponse deleteVertex(DeleteVertexRequest request) {
    if (CollectionUtils.isEmpty(request.getVertices())) {
      return ManipulateDataResponse.ofSuccess();
    }

    try {
      String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
      BaseLPGGraphStoreClient lpgGraphStoreClient =
          (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

      String type = request.getVertices().get(0).getType();
      List<VertexRecord> records =
          request.getVertices().stream()
              .map(InstanceConvertor::toVertexRecord)
              .collect(Collectors.toList());

      lpgGraphStoreClient.deleteVertex(type, records);
    } catch (Exception e) {
      return ManipulateDataResponse.ofFailure(e.getMessage());
    }

    return ManipulateDataResponse.ofSuccess();
  }

  @Override
  public ManipulateDataResponse upsertEdge(UpsertEdgeRequest request) {
    if (CollectionUtils.isEmpty(request.getEdges())) {
      return ManipulateDataResponse.ofSuccess();
    }

    try {
      String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
      BaseLPGGraphStoreClient lpgGraphStoreClient =
          (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

      List<EdgeRecord> records =
          request.getEdges().stream()
              .map(InstanceConvertor::toEdgeRecord)
              .collect(Collectors.toList());
      String type = records.get(0).getEdgeType().toString();

      lpgGraphStoreClient.upsertEdge(type, records, request.getUpsertAdjacentVertices());
    } catch (Exception e) {
      return ManipulateDataResponse.ofFailure(e.getMessage());
    }

    return ManipulateDataResponse.ofSuccess();
  }

  @Override
  public ManipulateDataResponse deleteEdgeRequest(DeleteEdgeRequest request) {
    if (CollectionUtils.isEmpty(request.getEdges())) {
      return ManipulateDataResponse.ofSuccess();
    }

    try {
      String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
      BaseLPGGraphStoreClient lpgGraphStoreClient =
          (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

      List<EdgeRecord> records =
          request.getEdges().stream()
              .map(InstanceConvertor::toEdgeRecord)
              .collect(Collectors.toList());
      String type = records.get(0).getEdgeType().toString();

      lpgGraphStoreClient.deleteEdge(type, records);
    } catch (Exception e) {
      return ManipulateDataResponse.ofFailure(e.getMessage());
    }

    return ManipulateDataResponse.ofSuccess();
  }

  @Override
  public List<PageRankScoreInstance> getPageRankScores(GetPageRankScoresRequest request) {
    String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    BaseLPGGraphStoreClient lpgGraphStoreClient =
        (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

    List<VertexRecord> startNode =
        request.getStartNodes().stream()
            .map(InstanceConvertor::toVertexRecord)
            .collect(Collectors.toList());
    PageRankCompete compete = new PageRankCompete(startNode, request.getTargetVertexType());
    List<ComputeResultRow> executeResult = lpgGraphStoreClient.runPageRank(compete);

    return executeResult.stream()
        .map(
            row ->
                new PageRankScoreInstance(
                    row.getNode().getVertexType(), row.getNode().getId(), row.getScore()))
        .collect(Collectors.toList());
  }

  @Override
  public ExpendOneHopResponse expendOneHop(ExpendOneHopRequest request) {
    String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    BaseLPGGraphStoreClient lpgGraphStoreClient =
        (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

    Set<EdgeTypeName> edgeTypeNameSet =
        request.getEdgeTypeNameConstraint() == null
            ? null
            : new HashSet<>(request.getEdgeTypeNameConstraint());
    OneHopLPGRecordQuery query =
        new OneHopLPGRecordQuery(
            request.getBizId(), request.getTypeName(), edgeTypeNameSet, Direction.BOTH);
    GraphLPGRecordStruct struct = (GraphLPGRecordStruct) lpgGraphStoreClient.queryRecord(query);
    return convert2ExpendOneHopResponse(request.getTypeName(), request.getBizId(), struct);
  }

  private ExpendOneHopResponse convert2ExpendOneHopResponse(
      String type, String id, GraphLPGRecordStruct graphLPGRecordStruct) {
    if (graphLPGRecordStruct == null) {
      return null;
    }
    ExpendOneHopResponse response = new ExpendOneHopResponse();
    if (CollectionUtils.isEmpty(graphLPGRecordStruct.getVertices())
        && CollectionUtils.isEmpty(graphLPGRecordStruct.getEdges())) {
      return response;
    }

    Map<String, VertexRecord> vertexRecordMap = new HashMap<>();
    for (VertexRecord vertexRecord : graphLPGRecordStruct.getVertices()) {
      vertexRecordMap.put(vertexRecord.generateUniqueString(), vertexRecord);
    }

    String sourceVertexUniqueString = new VertexRecord(id, type, null).generateUniqueString();
    response.setVertex(vertexRecordMap.get(sourceVertexUniqueString));

    response.setEdges(graphLPGRecordStruct.getEdges());

    List<VertexRecord> adjacentVertices = Lists.newArrayList();
    for (EdgeRecord edgeRecord : graphLPGRecordStruct.getEdges()) {
      String startVertexUniqueString =
          new VertexRecord(edgeRecord.getSrcId(), edgeRecord.getEdgeType().getStartVertexType())
              .generateUniqueString();
      if (sourceVertexUniqueString.equals(startVertexUniqueString)) {
        String endVertexUniqueString =
            new VertexRecord(edgeRecord.getDstId(), edgeRecord.getEdgeType().getEndVertexType())
                .generateUniqueString();
        adjacentVertices.add(vertexRecordMap.get(endVertexUniqueString));
      } else {
        adjacentVertices.add(vertexRecordMap.get(startVertexUniqueString));
      }
    }
    response.setAdjacentVertices(adjacentVertices);

    return response;
  }

  @Override
  public VertexRecord queryVertex(QueryVertexRequest request) {
    String graphStoreUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    BaseLPGGraphStoreClient lpgGraphStoreClient =
        (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);

    VertexLPGRecordQuery query =
        new VertexLPGRecordQuery(request.getBizId(), request.getTypeName());
    GraphLPGRecordStruct struct = (GraphLPGRecordStruct) lpgGraphStoreClient.queryRecord(query);
    if (struct == null || struct.getVertices().isEmpty()) {
      return null;
    }
    return struct.getVertices().get(0);
  }
}
