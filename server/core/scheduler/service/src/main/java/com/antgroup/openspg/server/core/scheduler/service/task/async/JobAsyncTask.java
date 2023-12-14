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

/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.task.async;

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;

/** Async scheduler Task, submit/stop task */
public interface JobAsyncTask {
  /** Async submit task, return null and retry */
  String submit(JobTaskContext context);

  /** get task Status */
  TaskStatus getStatus(JobTaskContext context, String resource);

  /** stop Task */
  Boolean stop(JobTaskContext context, String resource);
}
