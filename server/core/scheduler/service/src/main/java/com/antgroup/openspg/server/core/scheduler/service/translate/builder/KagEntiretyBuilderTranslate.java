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

import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagEntiretyBuilderTranslate")
public class KagEntiretyBuilderTranslate implements Translate {

  @Autowired private BuilderJobService builderJobService;

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
  }

  /** get KAG Command Builder TaskDag */
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

    TaskExecuteDag.Node builder = new TaskExecuteDag.Node();
    String builderId = UUID.randomUUID().toString();
    builder.setId(builderId);
    builder.setName("Builder");
    builder.setTaskComponent("kagBuilderAsyncTask");
    nodes.add(builder);

    TaskExecuteDag.Node postProcessor = new TaskExecuteDag.Node();
    String postProcessorId = UUID.randomUUID().toString();
    postProcessor.setId(postProcessorId);
    postProcessor.setName("PostProcessor");
    postProcessor.setTaskComponent("kagCommandPostSyncTask");
    nodes.add(postProcessor);

    if (BuilderConstant.ODPS.equalsIgnoreCase(builderJob.getDataSourceType())) {
      TaskExecuteDag.Edge checkPartitionEdge = new TaskExecuteDag.Edge();
      checkPartitionEdge.setFrom(checkPartitionId);
      checkPartitionEdge.setTo(builderId);
      edges.add(checkPartitionEdge);
    }

    TaskExecuteDag.Edge edge = new TaskExecuteDag.Edge();
    edge.setFrom(builderId);
    edge.setTo(postProcessorId);
    edges.add(edge);

    taskDag.setNodes(nodes);
    taskDag.setEdges(edges);

    return taskDag;
  }
}
