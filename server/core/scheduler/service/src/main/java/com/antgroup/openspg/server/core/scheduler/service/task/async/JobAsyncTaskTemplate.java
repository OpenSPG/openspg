/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.async;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job Async Task Template
 *
 * @author yangjin
 * @Title: JobAsyncTaskTemplate.java
 * @Description:
 */
public abstract class JobAsyncTaskTemplate extends JobTaskTemplate implements JobAsyncTask {

    @Autowired
    SchedulerTaskService schedulerTaskService;

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
            SchedulerTask updateTask = new SchedulerTask();
            updateTask.setId(task.getId());
            updateTask.setResource(resource);
            schedulerTaskService.update(updateTask);
            return TaskStatus.RUNNING;
        }
        context.addTraceLog("异步任务已提交！获取任务状态。资源名称：%s", resource);
        return getStatus(context, resource);
    }

}