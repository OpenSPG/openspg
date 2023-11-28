/*
 * Copyright 2023 Ant Group CO., Ltd.
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

import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceQueryRequest;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.service.datasource.DataSourceRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDOWithBLOBs;
import com.antgroup.openspg.server.infra.dao.mapper.DataSourceDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.DataSourceConvertor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataSourceRepositoryImpl implements DataSourceRepository {

  @Autowired private DataSourceDOMapper dataSourceDOMapper;

  @Override
  public int save(DataSource dataSource) {
    DataSourceDOWithBLOBs dataSourceDO = DataSourceConvertor.toDO(dataSource);
    return dataSourceDOMapper.insert(dataSourceDO);
  }

  @Override
  public DataSource get(String uniqueName) {
    DataSourceDOExample example = new DataSourceDOExample();

    DataSourceDOExample.Criteria criteria = example.createCriteria();
    criteria.andUniqueNameEqualTo(uniqueName);

    List<DataSourceDOWithBLOBs> dataSourceDOs =
        dataSourceDOMapper.selectByExampleWithBLOBs(example);
    if (CollectionUtils.isEmpty(dataSourceDOs)) {
      return null;
    }
    return DataSourceConvertor.toModel(dataSourceDOs.get(0));
  }

  @Override
  public Map<String, DataSource> query(DataSourceQueryRequest request) {
    DataSourceDOExample example = new DataSourceDOExample();

    DataSourceDOExample.Criteria criteria = example.createCriteria();
    if (request.getType() != null) {
      criteria.andTypeEqualTo(request.getType());
    }
    if (request.getName() != null) {
      criteria.andUniqueNameEqualTo(request.getName());
    }

    List<DataSourceDOWithBLOBs> dataSourceDOs =
        dataSourceDOMapper.selectByExampleWithBLOBs(example);
    return dataSourceDOs.stream()
        .collect(
            Collectors.toMap(DataSourceDOWithBLOBs::getUniqueName, DataSourceConvertor::toModel));
  }

  @Override
  public Map<String, DataSource> batchGet(List<String> uniqueNames) {
    DataSourceDOExample example = new DataSourceDOExample();

    DataSourceDOExample.Criteria criteria = example.createCriteria();
    criteria.andUniqueNameIn(uniqueNames);

    List<DataSourceDOWithBLOBs> dataSourceDOs =
        dataSourceDOMapper.selectByExampleWithBLOBs(example);
    return dataSourceDOs.stream()
        .collect(
            Collectors.toMap(DataSourceDOWithBLOBs::getUniqueName, DataSourceConvertor::toModel));
  }
}
