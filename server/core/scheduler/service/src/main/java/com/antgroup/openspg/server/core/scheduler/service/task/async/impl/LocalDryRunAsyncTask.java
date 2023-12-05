/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.async.impl;

import java.util.UUID;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.async.JobAsyncTaskTemplate;
import org.springframework.stereotype.Component;

/**
 * @author yangjin
 * @version : LocalAsyncTask.java, v 0.1 2023年12月05日 14:24 yangjin Exp $
 */
@Component("localDryRun")
public class LocalDryRunAsyncTask extends JobAsyncTaskTemplate {

    @Override
    public String submit(JobTaskContext context) {
        String resource = UUID.randomUUID().toString();
        context.addTraceLog("发起本地构建测试任务，资源名：%s", resource);
        return resource;
    }

    @Override
    public TaskStatus getStatus(JobTaskContext context, String resource) {
        context.addTraceLog("检查本地构建测试任务状态，资源名：%s", resource);
        SchedulerTask task = context.getTask();
        return task.getExecuteNum() > 2 ? TaskStatus.FINISH : TaskStatus.RUNNING;
    }

    @Override
    public Boolean stop(JobTaskContext context, String resource) {
        context.addTraceLog("停止本地构建测试任务，资源名：%s", resource);
        return true;
    }

}
