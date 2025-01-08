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

package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceQuery;
import com.antgroup.openspg.server.common.service.datasource.DataSourceRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDO;
import com.antgroup.openspg.server.infra.dao.mapper.DataSourceDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.DataSourceConvertor;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataSourceRepositoryImpl implements DataSourceRepository {

  @Autowired private DataSourceDOMapper dataSourceDOMapper;

  @Override
  public Long insert(DataSource record) {
    DataSourceDO jobDO = DataSourceConvertor.toDO(record);
    dataSourceDOMapper.insert(jobDO);
    record.setId(jobDO.getId());
    return jobDO.getId();
  }

  @Override
  public int deleteById(Long id) {
    return dataSourceDOMapper.deleteById(id);
  }

  @Override
  public Long update(DataSource record) {
    return dataSourceDOMapper.update(DataSourceConvertor.toDO(record));
  }

  @Override
  public DataSource getById(Long id) {
    return DataSourceConvertor.toModel(dataSourceDOMapper.getById(id));
  }

  @Override
  public Paged<DataSource> query(DataSourceQuery record) {
    Paged<DataSource> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = dataSourceDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(DataSourceConvertor.toModelList(dataSourceDOMapper.query(record)));
    return pageData;
  }

  @Override
  public List<DataSource> getGroupByType(DataSourceQuery record) {
    return DataSourceConvertor.toModelList(dataSourceDOMapper.getGroupByType(record));
  }
}
