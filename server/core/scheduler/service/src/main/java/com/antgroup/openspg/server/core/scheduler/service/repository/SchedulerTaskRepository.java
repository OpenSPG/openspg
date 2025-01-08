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

package com.antgroup.openspg.server.core.scheduler.service.repository;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import java.util.List;

/**
 * The read-write interface for scheduler task in the database, provides methods for saving,
 * updating, deleting, and querying properties and relations.
 */
public interface SchedulerTaskRepository {

  /** insert Task */
  Long insert(SchedulerTask record);

  /** delete By jobId */
  int deleteByJobId(Long jobId);

  /** update By Id */
  Long update(SchedulerTask record);

  /** get By id */
  SchedulerTask getById(Long id);

  /** query By Condition */
  Paged<SchedulerTask> query(SchedulerTaskQuery record);

  /** query By InstanceId And nodeId */
  SchedulerTask queryByInstanceIdAndNodeId(Long instanceId, String nodeId);

  /** query By InstanceId */
  List<SchedulerTask> queryByInstanceId(Long instanceId);

  /** set Status By InstanceId */
  int setStatusByInstanceId(Long instanceId, SchedulerEnum.TaskStatus status);

  /** update Lock */
  int updateLock(Long id);

  /** update Unlock */
  int updateUnlock(Long id);
}
