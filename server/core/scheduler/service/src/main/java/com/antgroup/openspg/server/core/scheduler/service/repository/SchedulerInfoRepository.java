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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;

/**
 * The read-write interface for scheduler info in the database, provides methods for saving,
 * updating, deleting, and querying properties and relations.
 */
public interface SchedulerInfoRepository {

  /** insert Info */
  Long insert(SchedulerInfo record);

  /** update By Id */
  Long update(SchedulerInfo record);

  /** delete By jobId */
  int deleteById(Long id);

  /** get By id */
  SchedulerInfo getById(Long id);

  /** get By name */
  SchedulerInfo getByName(String name);

  /** query By Condition */
  Paged<SchedulerInfo> query(SchedulerInfoQuery record);

  /** update Lock */
  int updateLock(Long id);

  /** update Unlock */
  int updateUnlock(Long id);
}
