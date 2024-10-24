/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.biz.service.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.PageRankCompete;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.ComputeResultRow;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.*;
import com.antgroup.openspg.server.api.facade.dto.service.response.ManipulateDataResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.PageRankScoreInstance;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.service.GraphManager;
import com.antgroup.openspg.server.biz.service.convertor.InstanceConvertor;
import java.util.List;
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
}
