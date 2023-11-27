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

import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceUsageDO;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceUsageDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.DataSourceUsageDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.DataSourceUsageConvertor;
import com.antgroup.openspg.server.common.service.datasource.DataSourceUsageRepository;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceUsageQueryRequest;
import com.antgroup.openspg.server.common.model.datasource.DataSourceMountObjectTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsage;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsageTypeEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataSourceUsageRepositoryImpl implements DataSourceUsageRepository {

  @Autowired private DataSourceUsageDOMapper dataSourceUsageDOMapper;

  @Override
  public int save(DataSourceUsage dataSourceUsage) {
    DataSourceUsageDO dataSourceUsageDO = DataSourceUsageConvertor.toDO(dataSourceUsage);
    return dataSourceUsageDOMapper.insert(dataSourceUsageDO);
  }

  @Override
  public List<DataSourceUsage> getByMountObject(
      String mountObjectId,
      DataSourceMountObjectTypeEnum mountObjectType,
      DataSourceUsageTypeEnum usageType) {
    DataSourceUsageDOExample example = new DataSourceUsageDOExample();

    DataSourceUsageDOExample.Criteria criteria =
        example
            .createCriteria()
            .andMountObjectIdEqualTo(mountObjectId)
            .andMountObjectTypeEqualTo(mountObjectType.name());

    if (usageType != null) {
      criteria.andUsageTypeEqualTo(usageType.name());
    }

    example.setOrderByClause("id ASC");
    List<DataSourceUsageDO> dataSourceUsageDOS = dataSourceUsageDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(dataSourceUsageDOS, DataSourceUsageConvertor::toModel);
  }

  @Override
  public List<DataSourceUsage> query(DataSourceUsageQueryRequest request) {
    DataSourceUsageDOExample example = new DataSourceUsageDOExample();

    DataSourceUsageDOExample.Criteria criteria = example.createCriteria();
    if (request.getDataSourceName() != null) {
      criteria.andDataSourceNameEqualTo(request.getDataSourceName());
    }
    if (request.getUsageType() != null) {
      criteria.andUsageTypeEqualTo(request.getUsageType());
    }
    if (request.getMountObjectId() != null) {
      criteria.andMountObjectIdEqualTo(request.getMountObjectId());
    }
    if (request.getMountObjectType() != null) {
      criteria.andMountObjectTypeEqualTo(request.getMountObjectType());
    }
    example.setOrderByClause("id ASC");
    List<DataSourceUsageDO> dataSourceUsageDOS = dataSourceUsageDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(dataSourceUsageDOS, DataSourceUsageConvertor::toModel);
  }
}
