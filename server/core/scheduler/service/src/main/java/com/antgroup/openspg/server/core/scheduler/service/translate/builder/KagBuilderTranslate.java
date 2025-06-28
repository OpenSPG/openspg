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
package com.antgroup.openspg.server.core.scheduler.service.translate.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.retrieval.RetrievalService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagBuilderTranslate")
public class KagBuilderTranslate implements Translate {

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private DefaultValue value;

  @Autowired private RetrievalService retrievalService;

  @Override
  public TaskExecuteDag translate(SchedulerJob schedulerJob) {
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(schedulerJob.getInvokerId()));
    return getTaskDag(builderJob);
  }

  @Override
  public void statusCallback(
      SchedulerJob job, SchedulerInstance instance, SchedulerEnum.InstanceStatus instanceStatus) {
    BuilderJob builderJob = new BuilderJob();
    builderJob.setId(Long.valueOf(job.getInvokerId()));
    builderJob.setStatus(instanceStatus.name());
    builderJobService.update(builderJob);
    if (SchedulerEnum.InstanceStatus.isFinished(instanceStatus)) {
      ObjectStorageClient objectStorageClient =
          ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
      List<SchedulerTask> tasks = taskService.queryByInstanceId(instance.getId());
      for (SchedulerTask task : tasks) {
        if (BuilderConstant.KAG_WRITER_ASYNC_TASK.equalsIgnoreCase(task.getType())) {
          continue;
        }
        if (StringUtils.isBlank(task.getOutput())) {
          continue;
        }
        objectStorageClient.removeObject(value.getBuilderBucketName(), task.getOutput());
      }
    }
  }

  /** get KAG Builder TaskDag */
  public TaskExecuteDag getTaskDag(BuilderJob builderJob) {

    List<TaskExecuteDag.Node> nodes = Lists.newArrayList();
    List<TaskExecuteDag.Edge> edges = Lists.newArrayList();
    String retrievals = builderJob.getRetrievals();
    List<Long> retrievalList = Lists.newArrayList();
    if (StringUtils.isNotBlank(retrievals)) {
      retrievalList = JSON.parseObject(retrievals, new TypeReference<List<Long>>() {});
    }

    TaskExecuteDag taskDag = new TaskExecuteDag();

    TaskExecuteDag.Node retrievalNode = new TaskExecuteDag.Node();
    String retrievalId = UUID.randomUUID().toString();
    retrievalNode.setId(retrievalId);
    retrievalNode.setName("Create Index");
    retrievalNode.setTaskComponent("retrievalSyncTask");
    nodes.add(retrievalNode);

    String checkPartitionId = UUID.randomUUID().toString();
    if (BuilderConstant.ODPS.equalsIgnoreCase(builderJob.getDataSourceType())) {
      TaskExecuteDag.Node checkPartition = new TaskExecuteDag.Node();
      checkPartition.setId(checkPartitionId);
      checkPartition.setName("Preprocess");
      checkPartition.setTaskComponent("checkPartitionSyncTask");
      nodes.add(checkPartition);
    }

    TaskExecuteDag.Node reader = new TaskExecuteDag.Node();
    String readerId = UUID.randomUUID().toString();
    reader.setId(readerId);
    reader.setName("Reader");
    reader.setTaskComponent("kagReaderSyncTask");
    nodes.add(reader);

    TaskExecuteDag.Node splitter = new TaskExecuteDag.Node();
    String splitterId = UUID.randomUUID().toString();
    splitter.setId(splitterId);
    splitter.setName("Splitter");
    splitter.setTaskComponent("kagSplitterAsyncTask");
    nodes.add(splitter);

    List<String> extractorIds = Lists.newArrayList();
    if (CollectionUtils.isEmpty(retrievalList)) {
      TaskExecuteDag.Node extractor = new TaskExecuteDag.Node();
      String extractorId = UUID.randomUUID().toString();
      extractorIds.add(extractorId);
      extractor.setId(extractorId);
      extractor.setName("Extractor");
      extractor.setTaskComponent("kagExtractorAsyncTask");
      JSONObject properties = new JSONObject();
      properties.put("retrievalName", "_default");
      extractor.setProperties(properties);
      nodes.add(extractor);
    } else {
      for (Long id : retrievalList) {
        Retrieval retrieval = retrievalService.getById(id);
        TaskExecuteDag.Node extractor = new TaskExecuteDag.Node();
        String extractorId = UUID.randomUUID().toString();
        extractorIds.add(extractorId);
        extractor.setId(extractorId);
        extractor.setName("Extractor(" + retrieval.getName() + ")");
        extractor.setTaskComponent("kagExtractorAsyncTask_" + retrieval.getName());
        JSONObject properties = new JSONObject();
        properties.put("retrievalId", retrieval.getId());
        properties.put("retrievalName", retrieval.getName());
        extractor.setProperties(properties);
        nodes.add(extractor);
      }
    }

    TaskExecuteDag.Node vectorizer = new TaskExecuteDag.Node();
    String vectorizerId = UUID.randomUUID().toString();
    vectorizer.setId(vectorizerId);
    vectorizer.setName("Vectorizer");
    vectorizer.setTaskComponent("kagVectorizerAsyncTask");
    nodes.add(vectorizer);

    TaskExecuteDag.Node alignment = new TaskExecuteDag.Node();
    String alignmentId = UUID.randomUUID().toString();
    alignment.setId(alignmentId);
    alignment.setName("Alignment");
    alignment.setTaskComponent("kagAlignmentAsyncTask");
    nodes.add(alignment);

    TaskExecuteDag.Node writer = new TaskExecuteDag.Node();
    String writerId = UUID.randomUUID().toString();
    writer.setId(writerId);
    writer.setName("Writer");
    writer.setTaskComponent("kagWriterAsyncTask");
    nodes.add(writer);

    TaskExecuteDag.Edge retrievalEdge = new TaskExecuteDag.Edge();
    retrievalEdge.setFrom(retrievalId);
    if (BuilderConstant.ODPS.equalsIgnoreCase(builderJob.getDataSourceType())) {
      retrievalEdge.setTo(checkPartitionId);
      edges.add(retrievalEdge);
      TaskExecuteDag.Edge checkPartitionEdge = new TaskExecuteDag.Edge();
      checkPartitionEdge.setFrom(checkPartitionId);
      checkPartitionEdge.setTo(readerId);
      edges.add(checkPartitionEdge);
    } else {
      retrievalEdge.setTo(readerId);
      edges.add(retrievalEdge);
    }

    TaskExecuteDag.Edge edge = new TaskExecuteDag.Edge();
    edge.setFrom(readerId);
    edge.setTo(splitterId);
    edges.add(edge);

    for (String extractorId : extractorIds) {
      TaskExecuteDag.Edge edge1 = new TaskExecuteDag.Edge();
      edge1.setFrom(splitterId);
      edge1.setTo(extractorId);
      edges.add(edge1);

      TaskExecuteDag.Edge edge2 = new TaskExecuteDag.Edge();
      edge2.setFrom(extractorId);
      edge2.setTo(vectorizerId);
      edges.add(edge2);
    }

    TaskExecuteDag.Edge edge3 = new TaskExecuteDag.Edge();
    edge3.setFrom(vectorizerId);
    edge3.setTo(alignmentId);
    edges.add(edge3);

    TaskExecuteDag.Edge edge4 = new TaskExecuteDag.Edge();
    edge4.setFrom(alignmentId);
    edge4.setTo(writerId);
    edges.add(edge4);

    taskDag.setNodes(nodes);
    taskDag.setEdges(edges);

    return taskDag;
  }
}
