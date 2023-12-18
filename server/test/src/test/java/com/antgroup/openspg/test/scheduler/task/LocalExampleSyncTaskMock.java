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
package com.antgroup.openspg.test.scheduler.task;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.Dependence;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerConfig;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerUtils;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.SyncTaskExecuteTemplate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Local Sync Task Example: Pre Check Task */
@Component("localExampleSyncTask")
public class LocalExampleSyncTaskMock extends SyncTaskExecuteTemplate {

  /** scheduler max days */
  private static final long SCHEDULER_MAX_DAYS = 5;

  @Autowired SchedulerConfig schedulerValue;
  @Autowired SchedulerInstanceService schedulerInstanceService;

  @Override
  public TaskStatus submit(TaskExecuteContext context) {
    TaskStatus status = getTaskStatus(context);
    if (TaskStatus.isFinished(status)) {
      SchedulerInstance instance = context.getInstance();
      SchedulerInstance updateInstance = new SchedulerInstance();
      updateInstance.setId(instance.getId());
      updateInstance.setStatus(InstanceStatus.RUNNING);
      updateInstance.setGmtModified(instance.getGmtModified());
      schedulerInstanceService.update(updateInstance);
    }
    return status;
  }

  /** Is pre-check required */
  private TaskStatus getTaskStatus(TaskExecuteContext context) {
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
    LifeCycle lifeCycle = instance.getLifeCycle();
    if (!LifeCycle.PERIOD.equals(lifeCycle)) {
      return processBySkip(context);
    }

    if (Dependence.INDEPENDENT.name().equals(instance.getDependence())) {
      return processBySnapshot(context);
    } else {
      return processByMerge(context);
    }
  }

  /** Skip pre-check */
  private TaskStatus processBySkip(TaskExecuteContext context) {
    context.addTraceLog("No pre-check required");
    return TaskStatus.FINISH;
  }

  /** Snapshot instance pre-check */
  private TaskStatus processBySnapshot(TaskExecuteContext context) {
    context.addTraceLog("The current task does not depend on the completion of the last instance");
    return TaskStatus.FINISH;
  }

  /** Merge instance pre-check */
  public TaskStatus processByMerge(TaskExecuteContext context) {
    context.addTraceLog("The current task depends on the completion of the last instance");
    SchedulerInstance instance = context.getInstance();
    SchedulerJob job = context.getJob();
    Date preSchedulerDate =
        SchedulerUtils.getPreviousValidTime(job.getSchedulerCron(), instance.getSchedulerDate());
    String preUniqueId = SchedulerUtils.getUniqueId(job.getId(), preSchedulerDate);
    SchedulerInstance pre = schedulerInstanceService.getByUniqueId(preUniqueId);

    if (null == pre || InstanceStatus.isFinished(pre.getStatus())) {
      return TaskStatus.FINISH;
    }

    context.addTraceLog("Last instance(%s) has not executed, please wait", pre.getUniqueId());
    return TaskStatus.RUNNING;
  }
}
