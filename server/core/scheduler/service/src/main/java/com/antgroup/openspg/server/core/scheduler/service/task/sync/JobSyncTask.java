/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.sync;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;

/**
 * Job Sync task
 *
 * @author yangjin
 * @Title: JobSyncProcess.java
 * @Description:
 */
public interface JobSyncTask {

    /**
     * Sync submit task
     *
     * @param context
     * @return
     */
    TaskStatus submit(JobTaskContext context);
}