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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.translate.impl;

import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @version : localDryRunTranslate.java, v 0.1 2023-12-05 14:33 $
 */
@Component("localDryRunTranslate")
public class LocalDryRunTranslate implements Translate {

  @Override
  public WorkflowDag translate(SchedulerJob schedulerJob) {
    return getWorkflowDag();
  }

  public WorkflowDag getWorkflowDag() {

    List<WorkflowDag.Node> nodes = Lists.newArrayList();
    List<WorkflowDag.Edge> edges = Lists.newArrayList();

    WorkflowDag workflowGraph = new WorkflowDag();
    WorkflowDag.Node preCheck = new WorkflowDag.Node();
    String prdId = "1000001";
    preCheck.setId(prdId);
    preCheck.setName("Pre Check");
    preCheck.setType("preCheckTask");
    Long preX = 250L;
    Long preY = 280L;
    preCheck.setX(preX);
    preCheck.setY(preY);
    nodes.add(preCheck);

    WorkflowDag.Node localDryRun = new WorkflowDag.Node();
    String dryRunId = "2000001";
    localDryRun.setId(dryRunId);
    localDryRun.setName("Local DryRun");
    localDryRun.setType("localDryRunTask");
    Long dryRunX = 500L;
    Long dryRunY = 280L;
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
