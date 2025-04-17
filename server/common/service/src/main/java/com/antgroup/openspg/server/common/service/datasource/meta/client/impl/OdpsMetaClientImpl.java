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
import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.Partition;
import com.aliyun.odps.PartitionSpec;
import com.aliyun.odps.Project;
import com.aliyun.odps.ProjectFilter;
import com.aliyun.odps.Projects;
import com.aliyun.odps.Table;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordReader;
import com.aliyun.odps.tunnel.TableTunnel;
import com.antgroup.openspg.common.util.PartitionUtils;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.antgroup.openspg.server.common.service.datasource.meta.client.DataSourceMetaClient;
import com.csvreader.CsvReader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

@Slf4j
public class OdpsMetaClientImpl implements DataSourceMetaClient {

  /** AllPartition Cache */
  private static final Cache<String, List<String>> ALL_PARTITION_CACHE =
      CacheBuilder.newBuilder().maximumSize(5000).expireAfterWrite(4, TimeUnit.MINUTES).build();

  /** AllDatabases Cache */
  private static final Cache<String, List<String>> ALL_DATABASES_CACHE =
      CacheBuilder.newBuilder().maximumSize(5000).expireAfterWrite(4, TimeUnit.HOURS).build();

  @Override
  public List<Column> describeTable(CloudDataSource dataSource, String database, String tableName) {
    try {
      Odps odps = OdpsClient.getClient(dataSource, database);

      Table table = odps.tables().get(database, tableName);
      if (null == table) {
        return null;
      }
      List<com.aliyun.odps.Column> columns = Lists.newArrayList();
      TableSchema tableSchema = table.getSchema();
      columns.addAll(tableSchema.getColumns());
      columns.addAll(tableSchema.getPartitionColumns());

      List<Column> columnList = new ArrayList<>(columns.size());
      for (com.aliyun.odps.Column column : columns) {
        Column columnInfo =
            new Column(
                column.getName(), column.getTypeInfo().getOdpsType().name(), column.getComment());
        columnList.add(columnInfo);
      }
      return columnList;
    } catch (Exception e) {
      log.warn(
          String.format(
              "odps describeTable %s %s %s Exception:",
              JSON.toJSONString(dataSource), database, tableName),
          e);
      throw new RuntimeException("odps describeTable Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public List<String> showDatabases(CloudDataSource dataSource) {
    try {
      return ALL_DATABASES_CACHE.get(
          dataSource.getDbUrl(),
          () -> {
            Odps odps = OdpsClient.getClient(dataSource, null);
            Projects projects = odps.projects();
            List<String> projectList = Lists.newArrayList();
            Iterator<Project> iterator = projects.iteratorByFilter(new ProjectFilter());
            while (iterator.hasNext()) {
              Project project = iterator.next();
              projectList.add(project.getName());
            }
            return projectList;
          });
    } catch (Exception e) {
      log.warn(String.format("odps showDatabases %s Exception:", JSON.toJSONString(dataSource)), e);
      throw new RuntimeException("odps showDatabases Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public List<String> showTables(CloudDataSource dataSource, String database, String keyword) {
    try {
      Odps odps = OdpsClient.getClient(dataSource, database);
      List<String> tables = Lists.newArrayList();
      /*Assert.isTrue(odps.projects().exists(database), "database does not exist!");
      Instance i = SQLTask.run(odps, "SHOW TABLES LIKE '*" + keyword + "*';");
      i.waitForSuccess();
      return parse(i);*/
      if (OdpsClient.tableExists(odps, database, keyword)) {
        tables.add(keyword);
      }
      return tables;
    } catch (Exception e) {
      log.warn(String.format("odps showTables %s Exception:", JSON.toJSONString(dataSource)), e);
      throw new RuntimeException("odps showTables Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean isPartitionTable(CloudDataSource dataSource, String database, String tableName) {
    try {
      Odps odps = OdpsClient.getClient(dataSource, database);
      Table table = odps.tables().get(database, tableName);
      if (null == table) {
        return false;
      }
      return table.isPartitioned();
    } catch (Exception e) {
      log.warn(
          String.format("odps isPartitionTable %s Exception:", JSON.toJSONString(dataSource)), e);
      throw new RuntimeException("odps isPartitionTable Exception:" + e.getMessage(), e);
    }
  }

  public static List<String> parse(Instance instance) throws OdpsException {
    String selectResult = instance.getTaskResults().get("AnonymousSQLTask");
    CsvReader reader = new CsvReader(new StringReader(selectResult));
    reader.setSafetySwitch(false);
    List<String> records = new ArrayList();

    try {
      for (; reader.readRecord(); ) {
        String[] newline = reader.getValues();
        for (int i = 0; i < newline.length; ++i) {
          String value = newline[i];
          if (value.contains(":")) {
            value = value.split(":")[1];
          }
          records.add(value);
        }
      }
    } catch (IOException e) {
      throw new OdpsException("Error when parse sql results.", e);
    } finally {
      reader.close();
    }
    return records;
  }

  @Override
  public Boolean testConnect(CloudDataSource dataSource) {
    try {
      Odps odps = OdpsClient.getClient(dataSource, null);
      Projects projects = odps.projects();
      Iterator<Project> iterator = projects.iteratorByFilter(new ProjectFilter());
      while (iterator.hasNext()) {
        iterator.next();
        return Boolean.TRUE;
      }
      return Boolean.TRUE;
    } catch (Exception e) {
      log.warn(String.format("odps testConnect %s Exception:", JSON.toJSONString(dataSource)), e);
      throw new RuntimeException("odps testConnect Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public List<Map<String, Object>> sampleDateForPartition(
      CloudDataSource dataSource,
      String dataSourceId,
      String partitionStr,
      String bizDate,
      Integer limit) {
    List<Map<String, Object>> data = Lists.newArrayList();

    try {
      String project = OdpsClient.getProjectAndTable(dataSourceId)[0];
      String table = OdpsClient.getProjectAndTable(dataSourceId)[1];
      Odps odps = OdpsClient.getClient(dataSource, project);
      TableTunnel tableTunnel = OdpsClient.createTableTunnel(odps, dataSource);
      List<String> partitions = PartitionUtils.analysisPartition(partitionStr, bizDate);
      for (String partition : partitions) {
        PartitionSpec spec = new PartitionSpec(partition);
        TableTunnel.DownloadSession downloadSession =
            OdpsClient.createDownloadSession(tableTunnel, project, table, spec);
        Table t = OdpsClient.getTable(odps, project, table);
        RecordReader recordReader = t.read(spec, null, limit);
        Record record;
        while ((record = recordReader.read()) != null) {
          data.add(OdpsClient.consumeRecord(record, downloadSession.getSchema()));
        }
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "odps sampleDateForPartition %s %s %s %s %s Exception:",
              JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate, limit),
          e);
      throw new RuntimeException("odps sampleDateForPartition Exception:" + e.getMessage(), e);
    }

    return data;
  }

  @Override
  public Boolean hasPartition(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    try {
      String project = OdpsClient.getProjectAndTable(dataSourceId)[0];
      String table = OdpsClient.getProjectAndTable(dataSourceId)[1];
      Odps odps = OdpsClient.getClient(dataSource, project);
      Table t = OdpsClient.getTable(odps, project, table);
      Assert.isTrue(t.isPartitioned(), "The table is not a partitioned table!");

      List<String> partitions = PartitionUtils.analysisPartition(partitionStr, bizDate);
      for (String partition : partitions) {
        PartitionSpec partitionSpec = new PartitionSpec(partition);
        Boolean hasPartitionSpec = t.hasPartition(partitionSpec);
        if (hasPartitionSpec) {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    } catch (Exception e) {
      log.warn(
          String.format(
              "odps hasPartition %s %s %s %s Exception:",
              JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate),
          e);
      throw new RuntimeException("hasPartition Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Long getRecordCount(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    Long count = 0L;
    try {
      String project = OdpsClient.getProjectAndTable(dataSourceId)[0];
      String table = OdpsClient.getProjectAndTable(dataSourceId)[1];
      Odps odps = OdpsClient.getClient(dataSource, project);
      TableTunnel tableTunnel = OdpsClient.createTableTunnel(odps, dataSource);

      List<String> partitions = PartitionUtils.analysisPartition(partitionStr, bizDate);
      for (String partition : partitions) {
        PartitionSpec partitionSpec = new PartitionSpec(partition);
        TableTunnel.DownloadSession downloadSession =
            OdpsClient.createDownloadSession(tableTunnel, project, table, partitionSpec);
        long num = downloadSession.getRecordCount();
        count = count + num;
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "getRecordCount %s %s %s %s Exception:",
              JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate),
          e);
      throw new RuntimeException("getRecordCount Exception:" + e.getMessage(), e);
    }
    return count;
  }

  @Override
  public List<String> getAllPartitions(CloudDataSource dataSource, String dataSourceId) {
    String project = OdpsClient.getProjectAndTable(dataSourceId)[0];
    String table = OdpsClient.getProjectAndTable(dataSourceId)[1];
    try {
      Odps odps = OdpsClient.getClient(dataSource, project);
      return ALL_PARTITION_CACHE.get(
          dataSourceId, () -> getPartitionsNoCache(odps, project, table));
    } catch (Exception e) {
      log.warn(
          String.format(
              "odps getAllPartitions %s %s Exception:",
              JSON.toJSONString(dataSource), dataSourceId),
          e);
      throw new RuntimeException("odps getAllPartitions Exception:" + e.getMessage(), e);
    }
  }

  /**
   * get odps Partitions No Cache
   *
   * @param odps
   * @param project
   * @param table
   * @return
   * @throws OdpsException
   */
  public static List<String> getPartitionsNoCache(Odps odps, String project, String table)
      throws OdpsException {
    Table t = getTable(odps, project, table);
    List<String> partitions = Lists.newArrayList();
    long start = System.currentTimeMillis();
    List<Partition> allPartitions = t.getPartitions();
    if (CollectionUtils.isEmpty(allPartitions)) {
      return partitions;
    }
    long cost = System.currentTimeMillis() - start;
    log.info("get all partitions: {} {} {}", project, table, cost);
    allPartitions.forEach(el -> partitions.add(el.getPartitionSpec().toString(false, true)));
    return partitions;
  }

  /**
   * get odps Table
   *
   * @param odps
   * @param project
   * @param table
   * @return
   * @throws OdpsException
   */
  public static Table getTable(Odps odps, String project, String table) throws OdpsException {
    long start = System.currentTimeMillis();
    Table t = odps.tables().get(project, table);
    t.reload();
    long cost = System.currentTimeMillis() - start;
    log.info("get odps table: {} {} {}", project, table, cost);
    return t;
  }
}
