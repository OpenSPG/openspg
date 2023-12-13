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

import com.antgroup.openspg.server.core.scheduler.model.common.TaskDag;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.stereotype.Component;

/** scheduler Translate Local implementation class. SchedulerJob to TaskDag */
@Component("localExampleTranslate")
public class LocalExampleTranslateTest implements Translate {

  @Override
  public TaskDag translate(SchedulerJob schedulerJob) {
    return getTaskDag();
  }

  /** get Local Example TaskDag */
  public TaskDag getTaskDag() {

    List<TaskDag.Node> nodes = Lists.newArrayList();
    List<TaskDag.Edge> edges = Lists.newArrayList();

    TaskDag taskDag = new TaskDag();
    TaskDag.Node sync = new TaskDag.Node();
    String prdId = "1000001";
    sync.setId(prdId);
    sync.setName("Local Sync Task Example");
    sync.setType("localExampleSyncTask");
    nodes.add(sync);

    TaskDag.Node async = new TaskDag.Node();
    String dryRunId = "2000001";
    async.setId(dryRunId);
    async.setName("Local Async Task Example");
    async.setType("localExampleAsyncTask");
    nodes.add(async);

    TaskDag.Edge edge = new TaskDag.Edge();
    edge.setFrom(prdId);
    edge.setTo(dryRunId);
    edges.add(edge);

    taskDag.setNodes(nodes);
    taskDag.setEdges(edges);

    return taskDag;
  }
}
