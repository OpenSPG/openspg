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

package com.antgroup.openspg.server.core.scheduler.service.task.async.impl;

import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.async.JobAsyncTaskTemplate;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** local Dry Run Async Task */
@Component("localDryRunTask")
public class LocalDryRunAsyncTask extends JobAsyncTaskTemplate {

  @Override
  public String submit(JobTaskContext context) {
    String resource = UUID.randomUUID().toString();
    context.addTraceLog("submit a local dry run Task, resource:%s", resource);
    return resource;
  }

  @Override
  public TaskStatus getStatus(JobTaskContext context, String resource) {
    context.addTraceLog("check local dry run Task Status, resource:%s", resource);
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    if (LifeCycle.REAL_TIME.name().equalsIgnoreCase(instance.getLifeCycle())) {
      context.addTraceLog(
          "instance LifeCycle is REAL_TIME, The instance is running continuously...");
      return TaskStatus.RUNNING;
    }
    return task.getExecuteNum() > 2 ? TaskStatus.FINISH : TaskStatus.RUNNING;
  }

  @Override
  public Boolean stop(JobTaskContext context, String resource) {
    context.addTraceLog("stop local dry run Task, resource:%s", resource);
    return true;
  }
}
