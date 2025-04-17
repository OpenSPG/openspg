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
package com.antgroup.openspg.server.common.service.datasource.impl;

import com.antgroup.openspg.common.util.ECBUtil;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.CommonEnum.DataSourceType;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceQuery;
import com.antgroup.openspg.server.common.service.datasource.DataSourceRepository;
import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.antgroup.openspg.server.common.service.datasource.meta.DataSourceMeta;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class DataSourceServiceImpl implements DataSourceService {

  @Autowired private DataSourceRepository dataSourceRepository;
  @Autowired private DataSourceMeta dataSourceMeta;

  @Override
  public Long insert(DataSource record) {
    if (StringUtils.isBlank(record.getDbDriverName())) {
      record.setDbDriverName(record.getType().getDriver());
    }
    String encrypt = ECBUtil.encrypt(record.getDbPassword(), CommonConstant.ECB_PASSWORD_KEY);
    record.setDbPassword(encrypt);
    record.setEncrypt(encrypt);
    Boolean flag = dataSourceMeta.testConnect(CloudDataSource.toCloud(record));
    Assert.isTrue(flag, "Connection test failed");
    return dataSourceRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return dataSourceRepository.deleteById(id);
  }

  @Override
  public Long update(DataSource record) {
    if (StringUtils.isBlank(record.getDbDriverName())) {
      record.setDbDriverName(record.getType().getDriver());
    }
    setEncrypt(record);
    Boolean flag = dataSourceMeta.testConnect(CloudDataSource.toCloud(record));
    Assert.isTrue(flag, "Connection test failed");
    return dataSourceRepository.update(record);
  }

  private void setEncrypt(DataSource record) {
    if (CommonConstant.DEFAULT_PASSWORD.equals(record.getDbPassword())) {
      DataSource sourceDTO = dataSourceRepository.getById(record.getId());
      record.setDbPassword(sourceDTO.getDbPassword());
      record.setEncrypt(sourceDTO.getEncrypt());
    } else {
      String encrypt = ECBUtil.encrypt(record.getDbPassword(), CommonConstant.ECB_PASSWORD_KEY);
      record.setDbPassword(encrypt);
      record.setEncrypt(encrypt);
    }
  }

  @Override
  public DataSource getById(Long id) {
    return dataSourceRepository.getById(id);
  }

  @Override
  public Paged<DataSource> query(DataSourceQuery record) {
    return dataSourceRepository.query(record);
  }

  @Override
  public List<DataSource> getGroupByType(DataSourceQuery record) {
    return dataSourceRepository.getGroupByType(record);
  }

  @Override
  public List<String> getAllDatabase(Long id) {
    return dataSourceMeta.showDatabases(CloudDataSource.toCloud(getById(id)));
  }

  @Override
  public List<String> getAllTable(Long id, String database, String keyword) {
    return dataSourceMeta.showTables(CloudDataSource.toCloud(getById(id)), database, keyword);
  }

  @Override
  public List<Column> getTableDetail(Long id, String database, String table) {
    return dataSourceMeta.describeTable(CloudDataSource.toCloud(getById(id)), database, table);
  }

  @Override
  public Boolean testConnect(DataSource record) {
    setEncrypt(record);
    return dataSourceMeta.testConnect(CloudDataSource.toCloud(record));
  }

  @Override
  public List<Column> getDataSourceType(String category) {
    List<Column> types = Lists.newArrayList();
    DataSourceType[] allType = DataSourceType.values();
    for (DataSourceType type : allType) {
      if (StringUtils.isNotBlank(category)
          && !type.getCategory().name().equalsIgnoreCase(category)) {
        continue;
      }
      types.add(new Column(type.name(), type.getCategory().name(), type.getName()));
    }
    return types;
  }
}
