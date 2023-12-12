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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import java.util.List;

/** Scheduler Task Service: Add, delete, update, and query tasks */
public interface SchedulerTaskService {

  /** insert Task */
  Long insert(SchedulerTask record);

  /** delete Task By id */
  int deleteById(Long id);

  /** delete By id List */
  int deleteByIds(List<Long> ids);

  /** delete By jobId */
  int deleteByJobId(Long jobId);

  /** update By Id */
  Long update(SchedulerTask record);

  /** insert Or Update，id is null to Update */
  Long replace(SchedulerTask record);

  /** get By id */
  SchedulerTask getById(Long id);

  /** query By Condition，query all if pageNo is null */
  Page<List<SchedulerTask>> query(SchedulerTaskQuery record);

  /** get Count By Condition */
  Long getCount(SchedulerTaskQuery record);

  /** get By id List */
  List<SchedulerTask> getByIds(List<Long> ids);

  /** query By InstanceId And Type */
  SchedulerTask queryByInstanceIdAndType(Long instanceId, String type);

  /** query By InstanceId */
  List<SchedulerTask> queryByInstanceId(Long instanceId);

  /** query Base Column By InstanceId */
  List<SchedulerTask> queryBaseColumnByInstanceId(Long instanceId);

  /** set Status By InstanceId */
  int setStatusByInstanceId(Long instanceId, TaskStatus status);

  /** update Extension By Lock */
  int updateExtensionByLock(SchedulerTask record, String extension);

  /** update Lock */
  int updateLock(Long id);

  /** update Unlock */
  int updateUnlock(Long id);
}