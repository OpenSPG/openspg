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
package com.antgroup.openspg.server.common.service.datasource.meta.client.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.antgroup.openspg.server.common.service.datasource.meta.client.DataSourceMetaClient;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultMetaClientImpl implements DataSourceMetaClient {

  @Override
  public List<Column> describeTable(CloudDataSource dataSource, String database, String tableName) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public List<String> showDatabases(CloudDataSource dataSource) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public List<String> showTables(CloudDataSource dataSource, String database, String keyword) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public Boolean isPartitionTable(CloudDataSource dataSource, String database, String tableName) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public Boolean testConnect(CloudDataSource dataSource) {
    try {
      return Boolean.TRUE;
    } catch (Exception e) {
      log.warn("testConnect Exception:" + JSON.toJSONString(dataSource), e);
      throw new RuntimeException("testConnect Exception", e);
    }
  }

  @Override
  public List<Map<String, Object>> sampleDateForPartition(
      CloudDataSource dataSource,
      String dataSourceId,
      String partitionStr,
      String bizDate,
      Integer limit) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public Boolean hasPartition(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public Long getRecordCount(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    throw new RuntimeException("Not Supported");
  }

  @Override
  public List<String> getAllPartitions(CloudDataSource dataSource, String dataSourceId) {
    throw new RuntimeException("Not Supported");
  }
}
