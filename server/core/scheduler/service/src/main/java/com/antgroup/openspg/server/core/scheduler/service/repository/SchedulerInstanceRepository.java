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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import java.util.List;

/**
 * The read-write interface for scheduler instance in the database, provides methods for saving,
 * updating, deleting, and querying properties and relations.
 */
public interface SchedulerInstanceRepository {

  /** insert Instance */
  Long insert(SchedulerInstance record);

  /** delete By JobId */
  int deleteByJobId(Long jobId);

  /** update */
  Long update(SchedulerInstance record);

  /** get By id */
  SchedulerInstance getById(Long id);

  /** get By uniqueId */
  SchedulerInstance getByUniqueId(String uniqueId);

  /** query By Condition */
  Paged<SchedulerInstance> query(SchedulerInstanceQuery record);

  /** get Not Finish Instance */
  List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record);
}
