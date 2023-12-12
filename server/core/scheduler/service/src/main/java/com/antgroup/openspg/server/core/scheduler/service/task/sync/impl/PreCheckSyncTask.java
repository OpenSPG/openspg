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

package com.antgroup.openspg.server.core.scheduler.service.task.sync.impl;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.MergeMode;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.JobSyncTaskTemplate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** pre check Task */
@Component("preCheckTask")
public class PreCheckSyncTask extends JobSyncTaskTemplate {

  /** scheduler max days */
  private static final long SCHEDULER_MAX_DAYS = 5;

  @Autowired SchedulerValue schedulerValue;
  @Autowired SchedulerInstanceService schedulerInstanceService;

  @Override
  public TaskStatus submit(JobTaskContext context) {
    TaskStatus status = getTaskStatus(context);
    if (TaskStatus.isFinished(status)) {
      SchedulerInstance instance = context.getInstance();
      SchedulerInstance updateInstance = new SchedulerInstance();
      updateInstance.setId(instance.getId());
      updateInstance.setStatus(InstanceStatus.RUNNING.name());
      updateInstance.setGmtModified(instance.getGmtModified());
      schedulerInstanceService.update(updateInstance);
    }
    return status;
  }

  /** Is pre-check required */
  private TaskStatus getTaskStatus(JobTaskContext context) {
    SchedulerInstance instance = context.getInstance();

    long days =
        TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - instance.getGmtCreate().getTime());
    Integer lastDays = schedulerValue.getExecuteMaxDay();
    if (days > SCHEDULER_MAX_DAYS) {
      context.addTraceLog(
          "The pre-check has not passed for more than %s days. It will not be scheduled after more than %s days",
          days, lastDays);
    }
    Date schedulerDate = instance.getSchedulerDate();
    Date now = new Date();
    if (now.before(schedulerDate)) {
      context.addTraceLog(
          "Execution time not reached! Start scheduling date:%s",
          DateTimeUtils.getDate2LongStr(schedulerDate));
      return TaskStatus.RUNNING;
    }

    if (LifeCycle.REAL_TIME.name().equals(instance.getLifeCycle())) {
      return processBySkip(context);
    }

    if (LifeCycle.ONCE.name().equals(instance.getLifeCycle())) {
      return processBySnapshot(context);
    }

    if (MergeMode.SNAPSHOT.name().equals(instance.getMergeMode())) {
      return processBySnapshot(context);
    } else {
      return processByMerge(context);
    }
  }

  /** Skip pre-check */
  private TaskStatus processBySkip(JobTaskContext context) {
    context.addTraceLog("No pre-check required");
    return TaskStatus.FINISH;
  }

  /** Snapshot instance pre-check */
  private TaskStatus processBySnapshot(JobTaskContext context) {
    context.addTraceLog("The current task does not depend on the completion of the last instance");
    return checkPreInstance(context);
  }

  /** Merge instance pre-check */
  public TaskStatus processByMerge(JobTaskContext context) {
    context.addTraceLog("The current task depends on the completion of the last instance");
    SchedulerInstance instance = context.getInstance();
    SchedulerJob job = context.getJob();
    Date preSchedulerDate =
        CommonUtils.getPreviousValidTime(job.getSchedulerCron(), instance.getSchedulerDate());
    String preUniqueId = CommonUtils.getUniqueId(job.getId(), preSchedulerDate);
    SchedulerInstance preInstance = schedulerInstanceService.getByUniqueId(preUniqueId);

    if (null == preInstance) {
      return checkPreInstance(context);
    }
    if (InstanceStatus.isFinished(preInstance.getStatus())) {
      return checkPreInstance(context);
    }

    context.addTraceLog(
        "The last instance(%s) has not been executed, please wait for the scheduling to be completed.",
        preInstance.getUniqueId());
    return TaskStatus.RUNNING;
  }

  private TaskStatus checkPreInstance(JobTaskContext context) {
    return TaskStatus.FINISH;
  }
}
