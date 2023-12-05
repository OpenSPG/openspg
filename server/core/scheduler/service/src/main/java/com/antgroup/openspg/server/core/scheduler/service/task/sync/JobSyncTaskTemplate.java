/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.sync;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskTemplate;

/**
 * Job Sync task Template
 *
 * @author yangjin
 * @Title: JobSyncTaskTemplate.java
 * @Description:
 */
public abstract class JobSyncTaskTemplate extends JobTaskTemplate implements JobSyncTask {

    @Override
    public TaskStatus process(JobTaskContext context) {
        return submit(context);
    }

}