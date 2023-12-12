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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.metadata;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import java.util.Date;
import java.util.List;

/** Scheduler Instance Service: Add, delete, update, and query instances */
public interface SchedulerInstanceService {

  /** insert Instance */
  Long insert(SchedulerInstance record);

  /** delete By Id */
  int deleteById(Long id);

  /** delete By JobId */
  int deleteByJobId(Long jobId);

  /** delete By Id List */
  int deleteByIds(List<Long> ids);

  /** update */
  Long update(SchedulerInstance record);

  /** get By id */
  SchedulerInstance getById(Long id);

  /** get By instanceId */
  SchedulerInstance getByUniqueId(String instanceId);

  /** query By Condition，query all if pageNo is null */
  Page<List<SchedulerInstance>> query(SchedulerInstanceQuery record);

  /** get Count By Condition */
  Long getCount(SchedulerInstanceQuery record);

  /** get By id List */
  List<SchedulerInstance> getByIds(List<Long> ids);

  /** get Not Finish Instance */
  List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record);

  /** get Instance By task type,status,time */
  List<SchedulerInstance> getInstanceByTask(
      String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime);
}
