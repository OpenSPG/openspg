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
package com.antgroup.openspg.server.common.service.datasource.meta.client;

import com.antgroup.openspg.server.common.model.datasource.Column;
import java.util.List;
import java.util.Map;

/**
 * 数据源的元数据信息
 *
 * @author yangjin
 */
public interface DataSourceMetaClient {

  /**
   * get table column info
   *
   * @param dataSource
   * @return
   */
  List<Column> describeTable(CloudDataSource dataSource, String database, String tableName);

  /**
   * show database
   *
   * @param dataSource
   * @return
   */
  List<String> showDatabases(CloudDataSource dataSource);

  /**
   * show tables
   *
   * @param dataSource
   * @return
   */
  List<String> showTables(CloudDataSource dataSource, String database, String keyword);

  /**
   * is Partition Table
   *
   * @param dataSource
   * @return
   */
  Boolean isPartitionTable(CloudDataSource dataSource, String database, String tableName);

  /**
   * test connection
   *
   * @param dataSource
   * @return
   */
  Boolean testConnect(CloudDataSource dataSource);

  /**
   * get sampleDate of partition
   *
   * @param dataSource 数据源配置
   * @param dataSourceId 库名称.表名称
   * @param partitionStr 多分区信息
   * @param bizDate 周期任务的日期
   * @param limit 抽样条数
   * @return
   */
  List<Map<String, Object>> sampleDateForPartition(
      CloudDataSource dataSource,
      String dataSourceId,
      String partitionStr,
      String bizDate,
      Integer limit);

  /**
   * check has partition
   *
   * @param dataSource 数据源配置
   * @param dataSourceId 库名称.表名称
   * @param partitionStr 多分区信息
   * @param bizDate 周期任务的日期
   * @return
   */
  Boolean hasPartition(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate);

  /**
   * get partition count
   *
   * @param dataSource 数据源配置
   * @param dataSourceId 库名称.表名称
   * @param partitionStr 多分区信息
   * @param bizDate 周期任务的日期
   * @return
   */
  Long getRecordCount(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate);

  /**
   * get all partition of table
   *
   * @param dataSource 数据源配置
   * @param dataSourceId 库名称.表名称
   * @return
   */
  List<String> getAllPartitions(CloudDataSource dataSource, String dataSourceId);
}
