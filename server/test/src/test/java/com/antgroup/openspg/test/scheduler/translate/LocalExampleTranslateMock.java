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
package com.antgroup.openspg.test.scheduler.translate;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.stereotype.Component;

/** scheduler Translate Local implementation class. SchedulerJob to TaskDag */
@Component("localExampleTranslate")
public class LocalExampleTranslateMock implements Translate {

  @Override
  public TaskExecuteDag translate(SchedulerJob schedulerJob) {
    return getTaskDag();
  }

  /** get Local Example TaskDag */
  public TaskExecuteDag getTaskDag() {

    List<TaskExecuteDag.Node> nodes = Lists.newArrayList();
    List<TaskExecuteDag.Edge> edges = Lists.newArrayList();

    TaskExecuteDag taskDag = new TaskExecuteDag();
    TaskExecuteDag.Node sync = new TaskExecuteDag.Node();
    String prdId = "1000001";
    sync.setId(prdId);
    sync.setName("Local Sync Task Example");
    sync.setTaskComponent("localExampleSyncTask");
    nodes.add(sync);

    TaskExecuteDag.Node async = new TaskExecuteDag.Node();
    String dryRunId = "2000001";
    async.setId(dryRunId);
    async.setName("Local Async Task Example");
    async.setTaskComponent("localExampleAsyncTask");
    nodes.add(async);

    TaskExecuteDag.Edge edge = new TaskExecuteDag.Edge();
    edge.setFrom(prdId);
    edge.setTo(dryRunId);
    edges.add(edge);

    taskDag.setNodes(nodes);
    taskDag.setEdges(edges);

    return taskDag;
  }
}
