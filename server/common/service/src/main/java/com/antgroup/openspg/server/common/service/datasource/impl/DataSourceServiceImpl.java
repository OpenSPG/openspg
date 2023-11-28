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

package com.antgroup.openspg.server.common.service.datasource.impl;

import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceMountObjectTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsage;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsageTypeEnum;
import com.antgroup.openspg.server.common.model.exception.DataSourceException;
import com.antgroup.openspg.server.common.service.datasource.DataSourceRepository;
import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.antgroup.openspg.server.common.service.datasource.DataSourceUsageRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSourceServiceImpl implements DataSourceService {

  @Autowired private DataSourceRepository dataSourceRepository;

  @Autowired private DataSourceUsageRepository dataSourceUsageRepository;

  @Override
  public DataSource getFirstDataSource(
      Long projectId, DataSourceUsageTypeEnum dataSourceUsageType) {
    List<String> dataSourceNames =
        dataSourceUsageRepository
            .getByMountObject(
                String.valueOf(SHARED_PROJECT_ID),
                DataSourceMountObjectTypeEnum.PROJECT,
                dataSourceUsageType)
            .stream()
            .map(DataSourceUsage::getDataSourceName)
            .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(dataSourceNames)) {
      throw DataSourceException.noUsageForProject(dataSourceUsageType, SHARED_PROJECT_ID);
    }

    String dataSourceName = dataSourceNames.get(0);
    DataSource dataSource = dataSourceRepository.get(dataSourceName);
    if (dataSource == null) {
      throw DataSourceException.dataSourceNotExist(dataSourceName);
    }
    return dataSource;
  }
}
