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
package com.antgroup.openspg.test.scheduler.translate;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.task.JobTaskDag;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.stereotype.Component;

/** scheduler Translate Local implementation class. SchedulerJob to TaskDag */
@Component("localExampleTranslate")
public class LocalExampleTranslateMock implements Translate {

  @Override
  public JobTaskDag translate(SchedulerJob schedulerJob) {
    return getTaskDag();
  }

  /** get Local Example TaskDag */
  public JobTaskDag getTaskDag() {

    List<JobTaskDag.Node> nodes = Lists.newArrayList();
    List<JobTaskDag.Edge> edges = Lists.newArrayList();

    JobTaskDag taskDag = new JobTaskDag();
    JobTaskDag.Node sync = new JobTaskDag.Node();
    String prdId = "1000001";
    sync.setId(prdId);
    sync.setName("Local Sync Task Example");
    sync.setTaskComponent("localExampleSyncTask");
    nodes.add(sync);

    JobTaskDag.Node async = new JobTaskDag.Node();
    String dryRunId = "2000001";
    async.setId(dryRunId);
    async.setName("Local Async Task Example");
    async.setTaskComponent("localExampleAsyncTask");
    nodes.add(async);

    JobTaskDag.Edge edge = new JobTaskDag.Edge();
    edge.setFrom(prdId);
    edge.setTo(dryRunId);
    edges.add(edge);

    taskDag.setNodes(nodes);
    taskDag.setEdges(edges);

    return taskDag;
  }
}
