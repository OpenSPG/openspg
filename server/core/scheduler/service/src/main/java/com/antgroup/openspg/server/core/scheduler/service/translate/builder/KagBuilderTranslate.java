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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagBuilderTranslate")
public class KagBuilderTranslate implements Translate {

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private DefaultValue value;

  @Override
  public TaskExecuteDag translate(SchedulerJob schedulerJob) {
    return getTaskDag();
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
        objectStorageClient.removeObject(value.getBuilderBucketName(), task.getOutput());
      }
    }
  }

  /** get KAG Builder TaskDag */
  public TaskExecuteDag getTaskDag() {

    List<TaskExecuteDag.Node> nodes = Lists.newArrayList();
    List<TaskExecuteDag.Edge> edges = Lists.newArrayList();

    TaskExecuteDag taskDag = new TaskExecuteDag();
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

    TaskExecuteDag.Node extractor = new TaskExecuteDag.Node();
    String extractorId = UUID.randomUUID().toString();
    extractor.setId(extractorId);
    extractor.setName("Extractor");
    extractor.setTaskComponent("kagExtractorAsyncTask");
    nodes.add(extractor);

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

    TaskExecuteDag.Edge edge = new TaskExecuteDag.Edge();
    edge.setFrom(readerId);
    edge.setTo(splitterId);
    edges.add(edge);

    TaskExecuteDag.Edge edge1 = new TaskExecuteDag.Edge();
    edge1.setFrom(splitterId);
    edge1.setTo(extractorId);
    edges.add(edge1);

    TaskExecuteDag.Edge edge2 = new TaskExecuteDag.Edge();
    edge2.setFrom(extractorId);
    edge2.setTo(vectorizerId);
    edges.add(edge2);

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
