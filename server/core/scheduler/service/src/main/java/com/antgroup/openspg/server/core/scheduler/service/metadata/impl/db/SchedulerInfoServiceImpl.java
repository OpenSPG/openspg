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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInfoService;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Scheduler Info Service implementation class: Add, delete, update, and query infos */
@Service
@ConditionalOnProperty(name = "scheduler.metadata.store.type", havingValue = "db")
public class SchedulerInfoServiceImpl implements SchedulerInfoService {

  @Autowired private SchedulerInfoRepository schedulerInfoRepository;

  @Override
  public Long insert(SchedulerInfo record) {
    return schedulerInfoRepository.insert(record);
  }

  @Override
  public synchronized Long update(SchedulerInfo record) {
    return schedulerInfoRepository.update(record);
  }

  @Override
  public synchronized int deleteById(Long id) {
    return schedulerInfoRepository.deleteById(id);
  }

  @Override
  public SchedulerInfo getById(Long id) {
    return schedulerInfoRepository.getById(id);
  }

  @Override
  public SchedulerInfo getByName(String name) {
    return schedulerInfoRepository.getByName(name);
  }

  @Override
  public Paged<SchedulerInfo> query(SchedulerInfoQuery record) {
    return schedulerInfoRepository.query(record);
  }

  @Override
  public int updateLock(Long id) {
    return schedulerInfoRepository.updateLock(id);
  }

  @Override
  public int updateUnlock(Long id) {
    return schedulerInfoRepository.updateUnlock(id);
  }
}
