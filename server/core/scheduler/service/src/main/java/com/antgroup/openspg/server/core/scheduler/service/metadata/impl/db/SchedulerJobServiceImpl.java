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
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.db;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Scheduler Job Service implementation class: Add, delete, update, and query Jobs */
@Service
@ConditionalOnProperty(name = "scheduler.metadata.store.type", havingValue = "db")
public class SchedulerJobServiceImpl implements SchedulerJobService {

  @Autowired private SchedulerJobRepository schedulerJobRepository;

  @Override
  public Long insert(SchedulerJob record) {
    return schedulerJobRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return schedulerJobRepository.deleteById(id);
  }

  @Override
  public Long update(SchedulerJob record) {
    return schedulerJobRepository.update(record);
  }

  @Override
  public SchedulerJob getById(Long id) {
    return schedulerJobRepository.getById(id);
  }

  @Override
  public Paged<SchedulerJob> query(SchedulerJobQuery record) {
    return schedulerJobRepository.query(record);
  }
}
