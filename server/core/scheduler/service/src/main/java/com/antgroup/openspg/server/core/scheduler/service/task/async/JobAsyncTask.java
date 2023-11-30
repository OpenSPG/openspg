/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.async;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;

/**
 * Async scheduler Task
 *
 * @author yangjin
 * @Title: JobAsyncTask.java
 * @Description:
 */
public interface JobAsyncTask {
    /**
     * Async submit task, return null and retry
     *
     * @param context
     * @return
     */
    String submit(JobTaskContext context);

    /**
     * get task Status
     *
     * @param context
     * @param resource
     * @return
     */
    TaskStatus getStatus(JobTaskContext context, String resource);

    /**
     * stop Task
     *
     * @param context
     * @param resource
     * @return
     */
    Boolean stop(JobTaskContext context, String resource);
}