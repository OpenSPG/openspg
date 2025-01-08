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

package com.antgroup.openspg.server.infra.dao.repository.scheduler;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerTaskRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerTaskDO;
import com.antgroup.openspg.server.infra.dao.mapper.SchedulerTaskDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor.SchedulerTaskConvertor;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerTaskRepositoryImpl implements SchedulerTaskRepository {

  @Autowired private SchedulerTaskDOMapper taskDOMapper;

  @Override
  public Long insert(SchedulerTask record) {
    SchedulerTaskDO taskDO = SchedulerTaskConvertor.toDO(record);
    taskDOMapper.insert(taskDO);
    record.setId(taskDO.getId());
    return taskDO.getId();
  }

  @Override
  public int deleteByJobId(Long jobId) {
    return taskDOMapper.deleteByJobId(jobId);
  }

  @Override
  public Long update(SchedulerTask record) {
    return taskDOMapper.update(SchedulerTaskConvertor.toDO(record));
  }

  @Override
  public SchedulerTask getById(Long id) {
    return SchedulerTaskConvertor.toModel(taskDOMapper.getById(id));
  }

  @Override
  public Paged<SchedulerTask> query(SchedulerTaskQuery record) {
    Paged<SchedulerTask> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = taskDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(SchedulerTaskConvertor.toModelList(taskDOMapper.query(record)));
    return pageData;
  }

  @Override
  public SchedulerTask queryByInstanceIdAndNodeId(Long instanceId, String nodeId) {
    return SchedulerTaskConvertor.toModel(
        taskDOMapper.queryByInstanceIdAndNodeId(instanceId, nodeId));
  }

  @Override
  public List<SchedulerTask> queryByInstanceId(Long instanceId) {
    return SchedulerTaskConvertor.toModelList(taskDOMapper.queryByInstanceId(instanceId));
  }

  @Override
  public int setStatusByInstanceId(Long instanceId, SchedulerEnum.TaskStatus status) {
    return taskDOMapper.setStatusByInstanceId(instanceId, status.name());
  }

  @Override
  public int updateLock(Long id) {
    return taskDOMapper.updateLock(id);
  }

  @Override
  public int updateUnlock(Long id) {
    return taskDOMapper.updateUnlock(id);
  }
}
