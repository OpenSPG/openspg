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
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerInstanceRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Scheduler Instance Service implementation class: Add, delete, update, and query instances */
@Service
@ConditionalOnProperty(name = "scheduler.metadata.store.type", havingValue = "db")
public class SchedulerInstanceServiceImpl implements SchedulerInstanceService {

  @Autowired private SchedulerInstanceRepository schedulerInstanceRepository;

  @Override
  public Long insert(SchedulerInstance record) {
    String uniqueId = record.getUniqueId();
    if (schedulerInstanceRepository.getByUniqueId(uniqueId) != null) {
      throw new SchedulerException("uniqueId {} already existed", uniqueId);
    }
    return schedulerInstanceRepository.insert(record);
  }

  @Override
  public int deleteByJobId(Long jobId) {
    return schedulerInstanceRepository.deleteByJobId(jobId);
  }

  @Override
  public Long update(SchedulerInstance record) {
    return schedulerInstanceRepository.update(record);
  }

  @Override
  public SchedulerInstance getById(Long id) {
    return schedulerInstanceRepository.getById(id);
  }

  @Override
  public SchedulerInstance getByUniqueId(String uniqueId) {
    return schedulerInstanceRepository.getByUniqueId(uniqueId);
  }

  @Override
  public Paged<SchedulerInstance> query(SchedulerInstanceQuery record) {
    return schedulerInstanceRepository.query(record);
  }

  @Override
  public List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record) {
    List<SchedulerInstance> instanceList = query(record).getResults();
    instanceList =
        instanceList.stream()
            .filter(s -> !InstanceStatus.isFinished(s.getStatus()))
            .collect(Collectors.toList());
    return instanceList;
  }
}
