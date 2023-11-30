/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.async;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Job Async Task Template
 *
 * @author yangjin
 * @Title: JobAsyncTaskTemplate.java
 * @Description:
 */
public abstract class JobAsyncTaskTemplate implements JobTask, JobAsyncTask {

    @Override
    public TaskStatus process(JobTaskContext context) {
        SchedulerTask task = context.getTask();
        String resource = task.getResource();

        if (StringUtils.isBlank(resource)) {
            context.addTraceLog("异步任务尚未提交！发起异步任务构建提交");
            resource = submit(context);
            if (StringUtils.isBlank(resource)) {
                return TaskStatus.RUNNING;
            }
            context.addTraceLog("异步任务提交成功！资源名称：%s", resource);
            //TODO: update task resource
            return TaskStatus.RUNNING;
        }
        context.addTraceLog("异步任务已提交！获取任务状态。资源名称：%s", resource);
        return getStatus(context, resource);
    }

}