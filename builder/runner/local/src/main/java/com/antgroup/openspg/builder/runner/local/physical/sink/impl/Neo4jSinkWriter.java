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

package com.antgroup.openspg.builder.runner.local.physical.sink.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.runner.local.physical.sink.BaseSinkWriter;
import com.antgroup.openspg.cloudext.impl.graphstore.neo4j.Neo4jStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.server.common.model.project.Project;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Neo4jSinkWriter extends BaseSinkWriter<Neo4jSinkNodeConfig> {

  private static final int NUM_THREADS = 60;

  private ExecuteNode node = new ExecuteNode();
  private Neo4jStoreClient client;
  private Project project;
  private static final String DOT = ".";

  private static RejectedExecutionHandler handler =
      (r, executor) -> {
        try {
          executor.getQueue().put(r);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      };
  private static ExecutorService executor =
      new ThreadPoolExecutor(
          NUM_THREADS,
          NUM_THREADS,
          2 * 60L,
          TimeUnit.SECONDS,
          new LinkedBlockingQueue<>(100),
          handler);

  public Neo4jSinkWriter(String id, String name, Neo4jSinkNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    if (context.getExecuteNodes() != null) {
      this.node = context.getExecuteNodes().get(getId());
    }
    client = new Neo4jStoreClient(context.getGraphStoreUrl());
    project = JSON.parseObject(context.getProject(), Project.class);
  }

  @Override
  public void write(List<BaseRecord> records) {
    if (!config.getIsWriter()) {
      return;
    }
    if (node != null) {
      node.setStatus(StatusEnum.RUNNING);
    }
    batchWriteToNeo4j(records);
  }

  private void batchWriteToNeo4j(List<BaseRecord> records) {
    for (BaseRecord record : records) {
      node.addTraceLog("Start Writer processor...");
      SubGraphRecord subGraphRecord = (SubGraphRecord) record;
      writeToNeo4j(subGraphRecord);
      node.addTraceLog(
          "Writer processor succeed node:%s edge:%s",
          subGraphRecord.getResultNodes().size(), subGraphRecord.getResultEdges().size());
    }
  }

  public void writeToNeo4j(SubGraphRecord subGraphRecord) {
    subGraphRecord.getResultNodes().forEach(node -> convertProperties(node.getProperties()));
    subGraphRecord.getResultEdges().forEach(edge -> convertProperties(edge.getProperties()));
    try {
      node.addTraceLog("Start Writer Nodes processor...");
      List<Future<Void>> nodeFutures =
          submitTasks(executor, subGraphRecord.getResultNodes(), this::writeNode);
      awaitAllTasks(nodeFutures);
      node.addTraceLog("Writer Nodes succeed");
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Error during node upsert", e);
    }
    try {
      node.addTraceLog("Start Writer Edges processor...");
      List<Future<Void>> edgeFutures =
          submitTasks(executor, subGraphRecord.getResultEdges(), this::writeEdge);
      awaitAllTasks(edgeFutures);
      node.addTraceLog("Writer Edges succeed");
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Error during edge upsert", e);
    }
  }

  private void convertProperties(Map<String, Object> properties) {
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      if (entry.getValue() instanceof JSONArray) {
        JSONArray jsonArray = (JSONArray) entry.getValue();
        List<Double> doubleList = new ArrayList<>();
        for (Object item : jsonArray) {
          if (item instanceof Number) {
            doubleList.add(((Number) item).doubleValue());
          }
        }
        entry.setValue(doubleList);
      }
    }
  }

  private <T> List<Future<Void>> submitTasks(
      ExecutorService executor, List<T> elements, Consumer<T> task) {
    List<Future<Void>> futures = new ArrayList<>();
    for (T element : elements) {
      futures.add(
          executor.submit(
              () -> {
                task.accept(element);
                return null;
              }));
    }
    return futures;
  }

  private void awaitAllTasks(List<Future<Void>> futures)
      throws InterruptedException, ExecutionException {
    for (Future<Void> future : futures) {
      future.get();
    }
  }

  private void writeNode(SubGraphRecord.Node node) {
    try {
      Long statr = System.currentTimeMillis();
      RecordAlterOperationEnum operation = context.getOperation();
      if (StringUtils.isBlank(node.getId())
          || StringUtils.isBlank(node.getName())
          || StringUtils.isBlank(node.getLabel())) {
        log.info(String.format("write Node ignore node:%s", JSON.toJSONString(node)));
        return;
      }
      String label = labelPrefix(node.getLabel());
      node.getProperties().put("id", node.getId());
      node.getProperties().put("name", node.getName());
      List<VertexRecord> vertexRecords = Lists.newArrayList();
      List<LPGPropertyRecord> properties = Lists.newArrayList();
      for (Map.Entry<String, Object> entry : node.getProperties().entrySet()) {
        properties.add(new LPGPropertyRecord(entry.getKey(), entry.getValue()));
      }
      VertexRecord vertexRecord = new VertexRecord(node.getId(), label, properties);
      vertexRecords.add(vertexRecord);
      if (operation == RecordAlterOperationEnum.UPSERT) {
        client.upsertVertex(label, vertexRecords);
      } else if (operation == RecordAlterOperationEnum.DELETE) {
        client.deleteVertex(label, vertexRecords);
      }
      log.info(
          String.format(
              "write Node succeed id:%s cons:%s",
              node.getId(), System.currentTimeMillis() - statr));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String labelPrefix(String label) {
    if (label.contains(DOT)) {
      return label;
    }
    return project.getNamespace() + DOT + label;
  }

  private void writeEdge(SubGraphRecord.Edge edge) {
    try {
      Long statr = System.currentTimeMillis();
      RecordAlterOperationEnum operation = context.getOperation();
      if (StringUtils.isBlank(edge.getFrom())
          || StringUtils.isBlank(edge.getTo())
          || StringUtils.isBlank(edge.getLabel())) {
        log.info(String.format("write Edge ignore edge:%s", JSON.toJSONString(edge)));
        return;
      }
      List<EdgeRecord> edgeRecords = Lists.newArrayList();
      List<LPGPropertyRecord> properties = Lists.newArrayList();
      for (Map.Entry<String, Object> entry : edge.getProperties().entrySet()) {
        properties.add(new LPGPropertyRecord(entry.getKey(), entry.getValue()));
      }

      EdgeTypeName edgeTypeName =
          new EdgeTypeName(
              labelPrefix(edge.getFromType()), edge.getLabel(), labelPrefix(edge.getToType()));
      EdgeRecord edgeRecord =
          new EdgeRecord(edge.getFrom(), edge.getTo(), edgeTypeName, properties);
      edgeRecords.add(edgeRecord);
      if (operation == RecordAlterOperationEnum.UPSERT) {
        client.upsertEdge(edge.getLabel(), edgeRecords);
      } else if (operation == RecordAlterOperationEnum.DELETE) {
        client.deleteEdge(edge.getLabel(), edgeRecords);
      }
      log.info(
          String.format(
              "write Edge succeed from:%s to:%s cons:%s",
              edge.getFrom(), edge.getTo(), System.currentTimeMillis() - statr));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws Exception {
    if (node != null) {
      node.setStatus(StatusEnum.FINISH);
    }
  }
}
