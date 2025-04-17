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

import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagStructureBuilderTranslate")
public class KagStructureBuilderTranslate implements Translate {

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private DefaultValue value;

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

  /** get KAG Structure Builder TaskDag */
  public TaskExecuteDag getTaskDag(BuilderJob builderJob) {

    List<TaskExecuteDag.Node> nodes = Lists.newArrayList();
    List<TaskExecuteDag.Edge> edges = Lists.newArrayList();

    TaskExecuteDag taskDag = new TaskExecuteDag();

    String checkPartitionId = UUID.randomUUID().toString();
    if (BuilderConstant.ODPS.equalsIgnoreCase(builderJob.getDataSourceType())) {
      TaskExecuteDag.Node checkPartition = new TaskExecuteDag.Node();
      checkPartition.setId(checkPartitionId);
      checkPartition.setName("Preprocess");
      checkPartition.setTaskComponent("checkPartitionSyncTask");
      nodes.add(checkPartition);
    }

    TaskExecuteDag.Node scan = new TaskExecuteDag.Node();
    String scannerId = UUID.randomUUID().toString();
    scan.setId(scannerId);
    scan.setName("Scanner");
    scan.setTaskComponent("kagScannerSyncTask");
    nodes.add(scan);

    TaskExecuteDag.Node mapping = new TaskExecuteDag.Node();
    String mappingId = UUID.randomUUID().toString();
    mapping.setId(mappingId);
    mapping.setName("Mapping");
    mapping.setTaskComponent("kagMappingSyncTask");
    nodes.add(mapping);

    TaskExecuteDag.Node vectorizer = new TaskExecuteDag.Node();
    String vectorizerId = UUID.randomUUID().toString();
    vectorizer.setId(vectorizerId);
    vectorizer.setName("Vectorizer");
    vectorizer.setTaskComponent("kagVectorizerAsyncTask");
    nodes.add(vectorizer);

    TaskExecuteDag.Node writer = new TaskExecuteDag.Node();
    String writerId = UUID.randomUUID().toString();
    writer.setId(writerId);
    writer.setName("Writer");
    writer.setTaskComponent("kagWriterAsyncTask");
    nodes.add(writer);

    if (BuilderConstant.ODPS.equalsIgnoreCase(builderJob.getDataSourceType())) {
      TaskExecuteDag.Edge checkPartitionEdge = new TaskExecuteDag.Edge();
      checkPartitionEdge.setFrom(checkPartitionId);
      checkPartitionEdge.setTo(scannerId);
      edges.add(checkPartitionEdge);
    }

    TaskExecuteDag.Edge edge = new TaskExecuteDag.Edge();
    edge.setFrom(scannerId);
    edge.setTo(mappingId);
    edges.add(edge);

    TaskExecuteDag.Edge edge2 = new TaskExecuteDag.Edge();
    edge2.setFrom(mappingId);
    edge2.setTo(vectorizerId);
    edges.add(edge2);

    TaskExecuteDag.Edge edge4 = new TaskExecuteDag.Edge();
    edge4.setFrom(vectorizerId);
    edge4.setTo(writerId);
    edges.add(edge4);

    taskDag.setNodes(nodes);
    taskDag.setEdges(edges);

    return taskDag;
  }
}
