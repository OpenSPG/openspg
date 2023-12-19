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

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecuteTemplate;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Local Async Task Example */
@Component("localExampleAsyncTask")
public class LocalExampleAsyncTaskMock extends AsyncTaskExecuteTemplate {

  @Override
  public String submit(TaskExecuteContext context) {
    String resource = UUID.randomUUID().toString();
    context.addTraceLog("submit a example Task, resource:%s", resource);
    return resource;
  }

  @Override
  public TaskStatus getStatus(TaskExecuteContext context, String resource) {
    context.addTraceLog("check example task status, resource:%s", resource);
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    if (LifeCycle.REAL_TIME.equals(instance.getLifeCycle())) {
      context.addTraceLog("LifeCycle is REAL_TIME, The instance is running continuously...");
      return TaskStatus.RUNNING;
    }
    return task.getExecuteNum() > 2 ? TaskStatus.FINISH : TaskStatus.RUNNING;
  }

  @Override
  public Boolean stop(TaskExecuteContext context, String resource) {
    context.addTraceLog("stop example Task, resource:%s", resource);
    return true;
  }
}
