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

package com.antgroup.openspg.server.core.scheduler.service.translate.impl;

import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.stereotype.Component;

/** scheduler Translate Local implementation class. SchedulerJob to WorkflowDag */
@Component("localDryRunTranslate")
public class LocalDryRunTranslate implements Translate {

  @Override
  public WorkflowDag translate(SchedulerJob schedulerJob) {
    return getWorkflowDag();
  }

  /** get Local DryRun WorkflowDag */
  public WorkflowDag getWorkflowDag() {

    List<WorkflowDag.Node> nodes = Lists.newArrayList();
    List<WorkflowDag.Edge> edges = Lists.newArrayList();

    WorkflowDag workflowGraph = new WorkflowDag();
    WorkflowDag.Node preCheck = new WorkflowDag.Node();
    String prdId = "1000001";
    Long preX = 250L;
    Long preY = 280L;
    preCheck.setId(prdId);
    preCheck.setName("Pre Check");
    preCheck.setType("preCheckTask");
    preCheck.setX(preX);
    preCheck.setY(preY);
    nodes.add(preCheck);

    WorkflowDag.Node localDryRun = new WorkflowDag.Node();
    String dryRunId = "2000001";
    Long dryRunX = 500L;
    Long dryRunY = 280L;
    localDryRun.setId(dryRunId);
    localDryRun.setName("Local DryRun");
    localDryRun.setType("localDryRunTask");
    localDryRun.setX(dryRunX);
    localDryRun.setY(dryRunY);
    nodes.add(localDryRun);

    WorkflowDag.Edge edge = new WorkflowDag.Edge();
    edge.setFrom(prdId);
    edge.setTo(dryRunId);
    edges.add(edge);

    workflowGraph.setNodes(nodes);
    workflowGraph.setEdges(edges);

    return workflowGraph;
  }
}
