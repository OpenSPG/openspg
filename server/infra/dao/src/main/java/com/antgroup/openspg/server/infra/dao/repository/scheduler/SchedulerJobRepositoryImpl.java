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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerJobRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerJobDO;
import com.antgroup.openspg.server.infra.dao.mapper.SchedulerJobDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor.SchedulerJobConvertor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerJobRepositoryImpl implements SchedulerJobRepository {

  @Autowired private SchedulerJobDOMapper schedulerJobDOMapper;

  @Override
  public Long insert(SchedulerJob record) {
    SchedulerJobDO jobDO = SchedulerJobConvertor.toDO(record);
    schedulerJobDOMapper.insert(jobDO);
    record.setId(jobDO.getId());
    return jobDO.getId();
  }

  @Override
  public int deleteById(Long id) {
    return schedulerJobDOMapper.deleteById(id);
  }

  @Override
  public Long update(SchedulerJob record) {
    return schedulerJobDOMapper.update(SchedulerJobConvertor.toDO(record));
  }

  @Override
  public SchedulerJob getById(Long id) {
    return SchedulerJobConvertor.toModel(schedulerJobDOMapper.getById(id));
  }

  @Override
  public Paged<SchedulerJob> query(SchedulerJobQuery record) {
    Paged<SchedulerJob> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = schedulerJobDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(SchedulerJobConvertor.toModelList(schedulerJobDOMapper.query(record)));
    return pageData;
  }
}
