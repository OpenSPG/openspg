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
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerTaskRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Scheduler Task Service implementation class: Add, delete, update, and query tasks */
@Service
@ConditionalOnProperty(name = "scheduler.metadata.store.type", havingValue = "db")
public class SchedulerTaskServiceImpl implements SchedulerTaskService {

  @Autowired private SchedulerTaskRepository schedulerTaskRepository;

  @Override
  public Long insert(SchedulerTask record) {
    return schedulerTaskRepository.insert(record);
  }

  @Override
  public synchronized int deleteByJobId(Long jobId) {
    return schedulerTaskRepository.deleteByJobId(jobId);
  }

  @Override
  public synchronized Long update(SchedulerTask record) {
    return schedulerTaskRepository.update(record);
  }

  @Override
  public synchronized Long replace(SchedulerTask record) {
    if (record.getId() == null) {
      return insert(record);
    } else {
      return update(record);
    }
  }

  @Override
  public SchedulerTask getById(Long id) {
    return schedulerTaskRepository.getById(id);
  }

  @Override
  public Paged<SchedulerTask> query(SchedulerTaskQuery record) {
    return schedulerTaskRepository.query(record);
  }

  @Override
  public SchedulerTask queryByInstanceIdAndNodeId(Long instanceId, String nodeId) {
    return schedulerTaskRepository.queryByInstanceIdAndNodeId(instanceId, nodeId);
  }

  @Override
  public List<SchedulerTask> queryByInstanceId(Long instanceId) {
    return schedulerTaskRepository.queryByInstanceId(instanceId);
  }

  @Override
  public int setStatusByInstanceId(Long instanceId, TaskStatus status) {
    return schedulerTaskRepository.setStatusByInstanceId(instanceId, status);
  }

  @Override
  public int updateLock(Long id) {
    return schedulerTaskRepository.updateLock(id);
  }

  @Override
  public int updateUnlock(Long id) {
    return schedulerTaskRepository.updateUnlock(id);
  }
}
