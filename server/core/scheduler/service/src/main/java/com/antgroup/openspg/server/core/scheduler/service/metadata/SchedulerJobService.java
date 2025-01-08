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

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;

/** Scheduler Job Service: Add, delete, update, and query Jobs */
public interface SchedulerJobService {

  /** insert Job */
  Long insert(SchedulerJob record);

  /** delete By Id */
  int deleteById(Long id);

  /** update Job */
  Long update(SchedulerJob record);

  /** get By id */
  SchedulerJob getById(Long id);

  /** query By Condition */
  Paged<SchedulerJob> query(SchedulerJobQuery record);
}
