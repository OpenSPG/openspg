package com.antgroup.openspg.server.core.scheduler.service.task;

import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;

/**
 * Job Task
 *
 * @author yangjin
 * @Title: JobTask.java
 * @Description:
 */
public interface JobTask {

    /**
     * process task
     *
     * @param context
     * @return
     */
    TaskStatus process(JobTaskContext context);
}
