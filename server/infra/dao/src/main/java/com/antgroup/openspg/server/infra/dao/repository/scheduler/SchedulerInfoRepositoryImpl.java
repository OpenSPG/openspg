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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;
import com.antgroup.openspg.server.core.scheduler.service.repository.SchedulerInfoRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerInfoDO;
import com.antgroup.openspg.server.infra.dao.mapper.SchedulerInfoDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor.SchedulerInfoConvertor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerInfoRepositoryImpl implements SchedulerInfoRepository {

  @Autowired private SchedulerInfoDOMapper infoDOMapper;

  @Override
  public Long insert(SchedulerInfo record) {
    SchedulerInfoDO infoDO = SchedulerInfoConvertor.toDO(record);
    infoDOMapper.insert(infoDO);
    record.setId(infoDO.getId());
    return infoDO.getId();
  }

  @Override
  public Long update(SchedulerInfo record) {
    return infoDOMapper.update(SchedulerInfoConvertor.toDO(record));
  }

  @Override
  public int deleteById(Long id) {
    return infoDOMapper.deleteById(id);
  }

  @Override
  public SchedulerInfo getById(Long id) {
    return SchedulerInfoConvertor.toModel(infoDOMapper.getById(id));
  }

  @Override
  public SchedulerInfo getByName(String name) {
    return SchedulerInfoConvertor.toModel(infoDOMapper.getByName(name));
  }

  @Override
  public Paged<SchedulerInfo> query(SchedulerInfoQuery record) {
    Paged<SchedulerInfo> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = infoDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(SchedulerInfoConvertor.toModelList(infoDOMapper.query(record)));
    return pageData;
  }

  @Override
  public int updateLock(Long id) {
    return infoDOMapper.updateLock(id);
  }

  @Override
  public int updateUnlock(Long id) {
    return infoDOMapper.updateUnlock(id);
  }
}
