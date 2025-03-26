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
package com.antgroup.openspg.server.common.service.datasource.meta;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.antgroup.openspg.server.common.service.datasource.meta.client.DataSourceMetaClient;
import com.antgroup.openspg.server.common.service.datasource.meta.client.DataSourceMetaFactory;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class DataSourceMeta {

  public List<Column> describeTable(CloudDataSource dataSource, String database, String tableName) {
    log.info("describeTable:", JSON.toJSONString(dataSource), database, tableName);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(database, "database");
    Assert.notNull(tableName, "table");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());
    log.info("describeTable start:", database, tableName);
    List<Column> columns = client.describeTable(dataSource, database, tableName);
    long cost = System.currentTimeMillis() - start;
    log.info("describeTable end:", database, tableName, cost);

    return columns;
  }

  public List<String> showDatabases(CloudDataSource dataSource) {
    log.info("showDatabases:", JSON.toJSONString(dataSource));
    Assert.notNull(dataSource, "datasource");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());
    log.info("showDatabases start");
    List<String> dbs = client.showDatabases(dataSource);
    long cost = System.currentTimeMillis() - start;
    log.info("showDatabases end:", cost);

    return dbs;
  }

  public List<String> showTables(CloudDataSource dataSource, String database, String keyword) {
    log.info("showTables:", JSON.toJSONString(dataSource), database);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(database, "database");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());
    log.info("showTables start:", database);
    List<String> tables = client.showTables(dataSource, database, keyword);
    long cost = System.currentTimeMillis() - start;
    log.info("showTables end:", database, cost);

    return tables;
  }

  public Boolean isPartitionTable(CloudDataSource dataSource, String database, String tableName) {
    log.info("isPartitionTable:", JSON.toJSONString(dataSource), database, tableName);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(database, "database");
    Assert.notNull(tableName, "table");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());
    log.info("isPartitionTable start:", database, tableName);
    Boolean isPartition = client.isPartitionTable(dataSource, database, tableName);
    long cost = System.currentTimeMillis() - start;
    log.info("isPartitionTable end:", database, tableName, cost);

    return isPartition;
  }

  public Boolean testConnect(CloudDataSource dataSource) {
    log.info("testConnect:", JSON.toJSONString(dataSource));
    Assert.notNull(dataSource, "datasource");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());
    log.info("testConnect start");
    Boolean flag = client.testConnect(dataSource);
    long cost = System.currentTimeMillis() - start;
    log.info("testConnect end:", cost);

    return flag;
  }

  public List<Map<String, Object>> sampleDateForPartition(
      CloudDataSource dataSource,
      String dataSourceId,
      String partitionStr,
      String bizDate,
      Integer limit) {
    log.info(
        "sampleDateForPartition:",
        JSON.toJSONString(dataSource),
        dataSourceId,
        partitionStr,
        bizDate,
        limit);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(dataSourceId, "database.table");
    Assert.notNull(partitionStr, "partition");
    Assert.notNull(limit, "limit");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());

    log.info("sampleDateForPartition start:", dataSourceId, partitionStr, bizDate, limit);
    List<Map<String, Object>> data =
        client.sampleDateForPartition(dataSource, dataSourceId, partitionStr, bizDate, limit);
    long cost = System.currentTimeMillis() - start;
    log.info("sampleDateForPartition end:", dataSourceId, partitionStr, bizDate, limit, cost);

    return data;
  }

  public Boolean hasPartition(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    log.info("hasPartition:", JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(dataSourceId, "database.table");
    Assert.notNull(partitionStr, "partition");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());

    log.info("hasPartition start:", dataSourceId, partitionStr, bizDate);
    Boolean hasPartition = client.hasPartition(dataSource, dataSourceId, partitionStr, bizDate);
    long cost = System.currentTimeMillis() - start;
    log.info("hasPartition end:", dataSourceId, partitionStr, bizDate, cost);
    return hasPartition;
  }

  public Long getRecordCount(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    log.info("getRecordCount:", JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(dataSourceId, "database.table");
    Assert.notNull(partitionStr, "partition");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());

    log.info("getRecordCount start:", dataSourceId, partitionStr, bizDate);
    Long count = client.getRecordCount(dataSource, dataSourceId, partitionStr, bizDate);
    long cost = System.currentTimeMillis() - start;
    log.info("getRecordCount end:", dataSourceId, partitionStr, bizDate, cost);
    return count;
  }

  public List<String> getAllPartitions(CloudDataSource dataSource, String dataSourceId) {
    log.info("getAllPartitions:", JSON.toJSONString(dataSource), dataSourceId);
    Assert.notNull(dataSource, "datasource");
    Assert.notNull(dataSourceId, "database.table");

    long start = System.currentTimeMillis();
    DataSourceMetaClient client = DataSourceMetaFactory.getInstance(dataSource.getType());

    log.info("getAllPartitions start:", dataSourceId);
    List<String> partitions = client.getAllPartitions(dataSource, dataSourceId);
    long cost = System.currentTimeMillis() - start;
    log.info("getAllPartitions end:", dataSourceId, cost);
    return partitions;
  }
}
