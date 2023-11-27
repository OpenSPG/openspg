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

package com.antgroup.openspg.server.biz.common.impl;

import com.antgroup.openspg.api.facade.dto.common.request.DataSourceCreateRequest;
import com.antgroup.openspg.api.facade.dto.common.request.DataSourceQueryRequest;
import com.antgroup.openspg.api.facade.dto.common.request.DataSourceUsageCreateRequest;
import com.antgroup.openspg.api.facade.dto.common.request.DataSourceUsageQueryRequest;
import com.antgroup.openspg.biz.common.DataSourceManager;
import com.antgroup.openspg.biz.common.convertor.DataSourceConvertor;
import com.antgroup.openspg.common.model.datasource.DataSource;
import com.antgroup.openspg.common.model.datasource.DataSourceUsage;
import com.antgroup.openspg.common.service.datasource.DataSourceRepository;
import com.antgroup.openspg.common.service.datasource.DataSourceService;
import com.antgroup.openspg.common.service.datasource.DataSourceUsageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSourceManagerImpl implements DataSourceManager {

  @Autowired private DataSourceService dataSourceService;

  @Autowired private DataSourceRepository dataSourceRepository;

  @Autowired private DataSourceUsageRepository dataSourceUsageRepository;

  @Override
  public DataSource create(DataSourceCreateRequest request) {
    DataSource dataSource = DataSourceConvertor.convert(request);
    dataSourceRepository.save(dataSource);
    return dataSource;
  }

  @Override
  public List<DataSource> query(DataSourceQueryRequest request) {
    Map<String, DataSource> dataSourceMap = dataSourceRepository.query(request);
    return new ArrayList<>(dataSourceMap.values());
  }

  @Override
  public DataSourceUsage mount(DataSourceUsageCreateRequest request) {
    DataSourceUsage dataSourceUsage = DataSourceConvertor.convert(request);
    dataSourceUsageRepository.save(dataSourceUsage);
    return dataSourceUsage;
  }

  @Override
  public List<DataSourceUsage> query(DataSourceUsageQueryRequest request) {
    return dataSourceUsageRepository.query(request);
  }
}
