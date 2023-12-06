/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.translate.impl;

import java.util.List;

import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.translate.Translate;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

/**
 * @author yangjin
 * @version : localDryRunTranslate.java, v 0.1 2023年12月05日 14:33 yangjin Exp $
 */
@Component("localDryRun")
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
        preCheck.setId("1000001");
        preCheck.setName("前置校验");
        preCheck.setType("preCheckTask");
        preCheck.setX(250L);
        preCheck.setY(280L);
        nodes.add(preCheck);

        WorkflowDag.Node localDryRun = new WorkflowDag.Node();
        localDryRun.setId("2000001");
        localDryRun.setName("本地执行");
        localDryRun.setType("localDryRunTask");
        localDryRun.setX(500L);
        localDryRun.setY(280L);
        nodes.add(localDryRun);

        WorkflowDag.Edge edge = new WorkflowDag.Edge();
        edge.setFrom("1000001");
        edge.setTo("2000001");

        workflowGraph.setNodes(nodes);
        workflowGraph.setEdges(edges);

        return workflowGraph;
    }

}
