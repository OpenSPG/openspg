/*
 * Copyright 2023 OpenSPG Authors
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
package com.antgroup.openspg.server.core.scheduler.service.task.sync.builder;

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.SyncTaskExecuteTemplate;
import org.springframework.stereotype.Component;

@Component("kagCommandPostSyncTask")
public class KagCommandPostSyncTask extends SyncTaskExecuteTemplate {

  @Override
  public SchedulerEnum.TaskStatus submit(TaskExecuteContext context) {
    context.addTraceLog("Post-processing, disk resources released successfully");
    return SchedulerEnum.TaskStatus.FINISH;
  }
}
