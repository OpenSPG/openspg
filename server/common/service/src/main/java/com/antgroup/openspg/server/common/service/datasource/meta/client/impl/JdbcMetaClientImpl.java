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
import com.antgroup.openspg.common.util.PartitionUtils;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.antgroup.openspg.server.common.service.datasource.meta.client.DataSourceMetaClient;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class JdbcMetaClientImpl implements DataSourceMetaClient {

  private static final String NUM_ROWS = "numRows             ";

  private String driver;

  public JdbcMetaClientImpl(String driver) {
    this.driver = driver;
  }

  @Override
  public List<Column> describeTable(CloudDataSource dataSource, String database, String tableName) {

    List<Column> columns = new ArrayList<>();
    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      if (StringUtils.isNotBlank(database)) {
        stmt.execute("use " + database);
      }
      res = stmt.executeQuery("describe " + tableName);
      while (res.next()) {
        String name = res.getString(1);
        String type = res.getString(2);
        String comment = res.getString(3);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(type) || name.startsWith("#")) {
          continue;
        }
        columns.add(new Column(name, type, comment));
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc describeTable Exception: %s %s %s",
              JSON.toJSONString(dataSource), database, tableName),
          e);
      throw new RuntimeException("jdbc describeTable Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeResultSet(res);
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return columns;
  }

  @Override
  public List<String> showDatabases(CloudDataSource dataSource) {
    List<String> dbs = new ArrayList<>();
    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      res = stmt.executeQuery("show databases");
      while (res.next()) {
        String database = res.getString(1);
        dbs.add(database);
      }
    } catch (Exception e) {
      log.warn(String.format("jdbc showDatabases Exception: %s", JSON.toJSONString(dataSource)), e);
      throw new RuntimeException("jdbc showDatabases Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeResultSet(res);
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return dbs;
  }

  @Override
  public List<String> showTables(CloudDataSource dataSource, String database, String keyword) {
    List<String> tables = new ArrayList<>();
    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      stmt.execute("use " + database);
      res = stmt.executeQuery("show tables");
      while (res.next()) {
        String name = res.getString(1);
        // 如果keyword为空直接返回
        if (StringUtils.isBlank(keyword)) {
          tables.add(name);
          continue;
        }
        if (name.contains(keyword)) {
          tables.add(name);
        }
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc showTables Exception: %s %s", JSON.toJSONString(dataSource), database),
          e);
      throw new RuntimeException("jdbc showTables Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeResultSet(res);
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return tables;
  }

  @Override
  public Boolean isPartitionTable(CloudDataSource dataSource, String database, String tableName) {
    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      if (StringUtils.isNotBlank(database)) {
        stmt.execute("use " + database);
      }
      res = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
      if (res.next()) {
        String createTableStatement = res.getString(1);
        return createTableStatement.contains("PARTITIONED BY");
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc isPartitionTable Exception: %s %s %s",
              JSON.toJSONString(dataSource), database, tableName),
          e);
      throw new RuntimeException("jdbc isPartitionTable Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeResultSet(res);
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return false;
  }

  @Override
  public Boolean testConnect(CloudDataSource dataSource) {
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      stmt.executeQuery("show databases");
    } catch (Exception e) {
      log.warn(String.format("testConnect Exception: %s", JSON.toJSONString(dataSource)), e);
      throw new RuntimeException("testConnect Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return Boolean.TRUE;
  }

  @Override
  public List<Map<String, Object>> sampleDateForPartition(
      CloudDataSource dataSource,
      String dataSourceId,
      String partitionStr,
      String bizDate,
      Integer limit) {
    List<Map<String, Object>> data = Lists.newArrayList();
    Connection conn = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      List<String> partitions = PartitionUtils.analysisPartition(partitionStr, bizDate, " and ");
      for (String partition : partitions) {
        String sql = "select * from " + dataSourceId + " where " + partition + " limit " + limit;
        List<Map<String, Object>> resList = executeQuery(conn, null, sql);
        if (CollectionUtils.isNotEmpty(resList)) {
          data.addAll(resList);
        }
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc sampleDateForPartition Exception: %s %s %s %s %s",
              JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate, limit),
          e);
      throw new RuntimeException("jdbc sampleDateForPartition Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeConnection(conn);
    }
    return data;
  }

  @Override
  public Boolean hasPartition(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {
    Connection conn = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      List<String> partitions = PartitionUtils.analysisPartition(partitionStr, bizDate);
      for (String partition : partitions) {
        Boolean hasPartitionSpec = hasPartition(conn, dataSourceId, partition);
        if (hasPartitionSpec) {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc hasPartition Exception: %s %s %s %s",
              JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate),
          e);
      throw new RuntimeException("jdbc hasPartition Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeConnection(conn);
    }
  }

  @Override
  public Long getRecordCount(
      CloudDataSource dataSource, String dataSourceId, String partitionStr, String bizDate) {

    Long count = 0L;
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      List<String> partitions = PartitionUtils.analysisPartition(partitionStr, bizDate);
      for (String partition : partitions) {
        partition = partition.replaceAll("/", ",");
        String sql = "desc formatted " + dataSourceId + " partition(" + partition + ")";
        Long num = getCount(stmt, sql);
        if (num <= 0) {
          String whereSql = getParameterSql(partition);
          sql = "SELECT COUNT(*) FROM " + dataSourceId + " where " + whereSql;
          num = getCountBySql(stmt, sql);
        }
        count = count + num;
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc getRecordCount Exception: %s %s %s %s",
              JSON.toJSONString(dataSource), dataSourceId, partitionStr, bizDate),
          e);
      throw new RuntimeException("jdbc getRecordCount Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return count;
  }

  public static String getParameterSql(String partition) {
    return partition.replaceAll("'", "\"").replaceAll(",", " and ").replaceAll("/", " and ");
  }

  @Override
  public List<String> getAllPartitions(CloudDataSource dataSource, String dataSourceId) {
    List<String> partitions = new ArrayList<>();
    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    try {
      conn = JdbcClient.getClient(dataSource, driver);
      stmt = conn.createStatement();
      res = stmt.executeQuery("show partitions " + dataSourceId);
      while (res.next()) {
        String partition = res.getString(1);
        partitions.add(partition);
      }
    } catch (Exception e) {
      log.warn(
          String.format(
              "jdbc getAllPartitions Exception: %s %s",
              JSON.toJSONString(dataSource), dataSourceId),
          e);
      throw new RuntimeException("jdbc getAllPartitions Exception:" + e.getMessage(), e);
    } finally {
      JdbcClient.closeResultSet(res);
      JdbcClient.closeStatement(stmt);
      JdbcClient.closeConnection(conn);
    }
    return partitions;
  }

  public static List<Map<String, Object>> executeQuery(
      Connection conn, String database, String sql) {
    long start = System.currentTimeMillis();
    log.info(String.format("[jdbc executeQuery start] database:%s sql:%s", database, sql));
    List<Map<String, Object>> resList = new ArrayList<>();
    Statement stmt = null;
    ResultSet res = null;
    try {
      stmt = conn.createStatement();
      if (StringUtils.isNotBlank(database)) {
        stmt.execute("use " + database);
      }
      res = stmt.executeQuery(sql);
      ResultSetMetaData rsmd = res.getMetaData();
      int count = rsmd.getColumnCount();
      while (res.next()) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= count; i++) {
          row.put(rsmd.getColumnName(i), res.getObject(i));
        }
        resList.add(row);
      }
      long cost = System.currentTimeMillis() - start;
      log.info(
          String.format("[jdbc executeQuery] database:%s sql:%s cost:%s", database, sql, cost));
    } catch (Exception e) {
      log.error(
          String.format("[jdbc executeQuery Exception] database:%s sql:%s", database, sql), e);
      throw new RuntimeException(e);
    } finally {
      JdbcClient.closeResultSet(res);
      JdbcClient.closeStatement(stmt);
    }
    return resList;
  }

  /**
   * 判定是否有分区信息
   *
   * @param conn
   * @param conn
   * @param partitionStr
   * @return
   */
  private static Boolean hasPartition(Connection conn, String dataSourceId, String partitionStr) {
    String sql = "show partitions " + dataSourceId + " partition(" + partitionStr + ")";
    List<Map<String, Object>> resList = executeQuery(conn, null, sql);
    return resList.size() > 0;
  }

  private static Long getCount(Statement stmt, String sql) {
    Long count = 0L;
    ResultSet res = null;
    try {
      res = stmt.executeQuery(sql);
      while (res.next()) {
        String key = res.getString(2);
        String value = res.getString(3);
        if (NUM_ROWS.equalsIgnoreCase(key)) {
          String numRows = value.trim();
          count = count + Long.valueOf(numRows);
        }
      }
    } catch (Exception e) {
      log.error(String.format("[jdbc getCount Exception] sql:%s", sql), e);
    } finally {
      JdbcClient.closeResultSet(res);
    }
    return count;
  }

  private static Long getCountBySql(Statement stmt, String sql) throws SQLException {
    Long count = 0L;
    ResultSet res = stmt.executeQuery(sql);
    if (res.next()) {
      count = res.getLong(1);
    }
    JdbcClient.closeResultSet(res);
    return count;
  }
}
