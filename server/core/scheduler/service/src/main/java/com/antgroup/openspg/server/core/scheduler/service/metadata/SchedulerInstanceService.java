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
package com.antgroup.openspg.server.core.scheduler.service.metadata;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import java.util.List;

/** Scheduler Instance Service: Add, delete, update, and query instances */
public interface SchedulerInstanceService {

  /** insert Instance */
  Long insert(SchedulerInstance record);

  /** delete By JobId */
  int deleteByJobId(Long jobId);

  /** update */
  Long update(SchedulerInstance record);

  /** get By id */
  SchedulerInstance getById(Long id);

  /** get By instanceId */
  SchedulerInstance getByUniqueId(String instanceId);

  /** query By Condition */
  List<SchedulerInstance> query(SchedulerInstance record);

  /** get Not Finish Instance */
  List<SchedulerInstance> getNotFinishInstance(SchedulerInstance record);
}
