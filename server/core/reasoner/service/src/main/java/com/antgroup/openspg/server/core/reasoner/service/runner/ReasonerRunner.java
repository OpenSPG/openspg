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
package com.antgroup.openspg.server.core.reasoner.service.runner;

import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.server.common.model.reasoner.ReasonerTask;
import com.antgroup.openspg.server.common.model.reasoner.StatusEnum;
import com.antgroup.openspg.server.common.model.reasoner.result.Edge;
import com.antgroup.openspg.server.common.model.reasoner.result.GraphResult;
import com.antgroup.openspg.server.common.model.reasoner.result.Node;
import com.antgroup.openspg.server.common.model.reasoner.result.TableResult;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;

@Slf4j
public class ReasonerRunner {

  private final ReasonerTask request;

  private final String schemaUrl;

  public ReasonerRunner(ReasonerTask request, String schemaUrl) {
    this.schemaUrl = schemaUrl;
    this.request = request;
  }

  /** run reasoner local task */
  public ReasonerTask run(Catalog catalog) {
    LocalReasonerTask task = new LocalReasonerTask();
    task.setId(String.valueOf(request.getTaskId()));
    task.setDsl(request.getDsl());
    task.setConnInfo(new KgSchemaConnectionInfo(schemaUrl, ""));
    task.setCatalog(catalog);
    String graphStateClass = "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState";
    task.setGraphStateClassName(graphStateClass);
    task.setGraphStateInitString(request.getGraphStoreUrl());
    task.setStartIdList(getStartIdListFromParams());
    task.setParams(getTaskParams());
    task.setExecutorTimeoutMs(3 * 60 * 1000);
    task.setExecutionRecorder(new DefaultRecorder());

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    String errorMsg = null;
    if (null == result) {
      errorMsg = "";
    } else if (StringUtils.isNotEmpty(result.getErrMsg())) {
      errorMsg = result.getErrMsg();
    }
    if (null != errorMsg) {
      request.setStatus(StatusEnum.ERROR);
      request.setResultMessage(errorMsg);
      return request;
    }
    log.info(task.getExecutionRecorder().toReadableString());
    // success
    request.setStatus(StatusEnum.FINISH);
    TableResult resultTableResult = getTableResult(result);
    request.setResultTableResult(resultTableResult);
    GraphResult graphResult = getGraphResult(result);
    request.setResultNodes(graphResult.getNodeList());
    request.setResultEdges(graphResult.getEdgeList());
    request.setResultPaths(graphResult.getPathList());
    return request;
  }

  protected GraphResult getGraphResult(LocalReasonerResult result) {
    GraphResult graphResult = new GraphResult();
    List<IVertex<IVertexId, IProperty>> vertexList = result.getVertexList();
    List<Node> nodeList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(vertexList)) {
      for (IVertex<IVertexId, IProperty> vertex : vertexList) {
        nodeList.add(convert2Node(vertex));
      }
    }
    graphResult.setNodeList(nodeList);
    List<IEdge<IVertexId, IProperty>> edgeList = result.getEdgeList();
    List<Edge> resultEdgeList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(edgeList)) {
      for (IEdge<IVertexId, IProperty> edge : edgeList) {
        Edge newEdge = convert2Edge(edge);
        resultEdgeList.add(newEdge);
      }
    }
    graphResult.setEdgeList(resultEdgeList);
    return graphResult;
  }

  protected List<Tuple2<String, String>> getStartIdListFromParams() {
    if (this.request.getParams() == null) {
      return Collections.emptyList();
    }
    String id = this.request.getParams().get("id");
    String type = this.request.getParams().get("type");
    if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
      return Collections.emptyList();
    }
    return Lists.newArrayList(new Tuple2<>(id, type));
  }

  protected Map<String, Object> getTaskParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("projId", String.valueOf(this.request.getProjectId()));
    params.put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, "false");
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, "true");

    if (this.request.getParams() != null) {
      params.putAll(this.request.getParams());
    }
    return params;
  }

  protected TableResult getTableResult(LocalReasonerResult result) {
    TableResult tableResult = new TableResult();
    if (null != result.getColumns()) {
      tableResult.setHeader(result.getColumns().toArray(new String[0]));
    }
    if (null != result.getRows()) {
      tableResult.setRows(result.getRows());
      tableResult.setTotal(tableResult.getRows().size());
    }
    return tableResult;
  }

  protected Node convert2Node(IVertex<IVertexId, IProperty> vertex) {
    Node node = new Node();
    node.setId(String.valueOf(vertex.getId().getInternalId()));
    node.setBizId(String.valueOf(vertex.getValue().get(Constants.NODE_ID_KEY)));
    node.setLabel(vertex.getId().getType());
    if (null != vertex.getValue()) {
      Object name = vertex.getValue().get("name");
      if (null == name) {
        name = node.getBizId();
      }
      node.setName(String.valueOf(name));
      for (String key : vertex.getValue().getKeySet()) {
        node.getProperties().put(key, vertex.getValue().get(key));
      }
    }
    return node;
  }

  protected Edge convert2Edge(IEdge<IVertexId, IProperty> edge) {
    Edge e = new Edge();
    e.setId(UUID.randomUUID().toString());
    e.setFrom(String.valueOf(edge.getSourceId().getInternalId()));
    e.setFromType(edge.getSourceId().getType());
    e.setTo(String.valueOf(edge.getTargetId().getInternalId()));
    e.setToType(edge.getTargetId().getType());
    if (Direction.IN.equals(edge.getDirection())) {
      e.setFrom(String.valueOf(edge.getTargetId().getInternalId()));
      e.setFromType(edge.getTargetId().getType());
      e.setTo(String.valueOf(edge.getSourceId().getInternalId()));
      e.setToType(edge.getSourceId().getType());
    }
    e.setLabel(edge.getType());
    e.setVersion(edge.getVersion());
    if (null != edge.getValue()) {
      e.setFromId(String.valueOf(edge.getValue().get(Constants.EDGE_FROM_ID_KEY)));
      e.setToId(String.valueOf(edge.getValue().get(Constants.EDGE_TO_ID_KEY)));
      if (Direction.IN.equals(edge.getDirection())) {
        e.setFromId(String.valueOf(edge.getValue().get(Constants.EDGE_TO_ID_KEY)));
        e.setToId(String.valueOf(edge.getValue().get(Constants.EDGE_FROM_ID_KEY)));
      }
      for (String key : edge.getValue().getKeySet()) {
        if (Constants.EDGE_TO_ID_KEY.equals(key) || Constants.EDGE_FROM_ID_KEY.equals(key)) {
          continue;
        }
        e.getProperties().put(key, edge.getValue().get(key));
      }
    }
    return e;
  }
}
