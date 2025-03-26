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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerInstanceRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerInstanceDO;
import com.antgroup.openspg.server.infra.dao.mapper.SchedulerInstanceDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor.SchedulerInstanceConvertor;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerInstanceRepositoryImpl implements SchedulerInstanceRepository {

  @Autowired private SchedulerInstanceDOMapper instanceDOMapper;

  @Override
  public Long insert(SchedulerInstance record) {
    SchedulerInstanceDO instanceDO = SchedulerInstanceConvertor.toDO(record);
    instanceDOMapper.insert(instanceDO);
    record.setId(instanceDO.getId());
    return instanceDO.getId();
  }

  @Override
  public int deleteByJobId(Long jobId) {
    return instanceDOMapper.deleteByJobId(jobId);
  }

  @Override
  public Long update(SchedulerInstance record) {
    return instanceDOMapper.update(SchedulerInstanceConvertor.toDO(record));
  }

  @Override
  public SchedulerInstance getById(Long id) {
    return SchedulerInstanceConvertor.toModel(instanceDOMapper.getById(id));
  }

  @Override
  public SchedulerInstance getByUniqueId(String uniqueId) {
    return SchedulerInstanceConvertor.toModel(instanceDOMapper.getByUniqueId(uniqueId));
  }

  @Override
  public Paged<SchedulerInstance> query(SchedulerInstanceQuery record) {
    Paged<SchedulerInstance> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = instanceDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(SchedulerInstanceConvertor.toModelList(instanceDOMapper.query(record)));
    return pageData;
  }

  @Override
  public List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record) {
    return SchedulerInstanceConvertor.toModelList(instanceDOMapper.getNotFinishInstance(record));
  }
}
