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

import com.alibaba.fastjson.JSONObject;
import com.aliyun.odps.Column;
import com.aliyun.odps.Odps;
import com.aliyun.odps.PartitionSpec;
import com.aliyun.odps.Table;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.tunnel.TableTunnel;
import com.antgroup.openspg.common.util.RetryerUtil;
import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.github.rholder.retry.Retryer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@Slf4j
public class OdpsClient {

  private static Retryer<Boolean> retry = RetryerUtil.getRetryer(50L, 2L, 5);

  /** downloadSession cache */
  private static final Cache<String, TableTunnel.DownloadSession> downloadSession_CACHE =
      CacheBuilder.newBuilder().maximumSize(5000).expireAfterWrite(4, TimeUnit.MINUTES).build();

  /**
   * 获取odps客户端
   *
   * @param store
   * @param defaultProject
   * @return
   */
  public static Odps getClient(CloudDataSource store, String defaultProject) {
    Account account = new AliyunAccount(store.getDbUser(), store.getDbPassword());
    Odps odps = new Odps(account);
    odps.setEndpoint(store.getDbUrl());
    if (defaultProject != null) {
      odps.setDefaultProject(defaultProject);
    }
    return odps;
  }

  /**
   * ge table tunnel
   *
   * @param odps
   * @param store
   * @return
   */
  public static TableTunnel createTableTunnel(Odps odps, CloudDataSource store) {
    TableTunnel tunnel = new TableTunnel(odps);
    JSONObject property = store.getConnectionInfo();
    if (property.containsKey(CommonConstant.TUNNEL_ENDPOINT)) {
      tunnel.setEndpoint(property.getString(CommonConstant.TUNNEL_ENDPOINT));
    }
    return tunnel;
  }

  /**
   * create download session
   *
   * @param tunnel
   * @param project
   * @param table
   * @param partitionSpec
   * @return
   */
  public static TableTunnel.DownloadSession createDownloadSession(
      TableTunnel tunnel, String project, String table, PartitionSpec partitionSpec) {
    Assert.notNull(partitionSpec, "createDownloadSession partitionSpec not null");
    String cacheKey = project + "." + table + ":" + partitionSpec;
    try {
      return downloadSession_CACHE.get(
          cacheKey,
          () -> {
            long start = System.currentTimeMillis();
            TableTunnel.DownloadSession downloadSession =
                retryableCreateDownLoadSession(tunnel, project, table, partitionSpec);
            long cost = System.currentTimeMillis() - start;
            log.info("createDownloadSession {} end:{}", cacheKey, cost);
            return downloadSession;
          });
    } catch (Exception e) {
      log.warn("createDownloadSession Exception:" + cacheKey, e);
      throw new RuntimeException("createDownloadSession Exception", e);
    }
  }

  /**
   * 获取指定表
   *
   * @param odps
   * @param project
   * @param table
   * @return
   */
  public static Table getTable(Odps odps, String project, String table) {
    try {
      long start = System.currentTimeMillis();
      Table t = odps.tables().get(project, table);
      t.reload();
      long cost = System.currentTimeMillis() - start;
      log.info("get table end:", project, table, cost);
      return t;
    } catch (Exception e) {
      log.warn(String.format("get table %s %s Exception:", project, table), e);
      throw new RuntimeException("get table Exception", e);
    }
  }

  /**
   * 重试机制获取获取DownloadSession
   *
   * @param tunnel
   * @return
   */
  private static TableTunnel.DownloadSession retryableCreateDownLoadSession(
      TableTunnel tunnel, String project, String table, PartitionSpec partitionSpec)
      throws Exception {
    AtomicReference<TableTunnel.DownloadSession> downloadSession = new AtomicReference<>();
    retry.call(
        () -> {
          try {
            if (partitionSpec == null) {
              downloadSession.set(tunnel.createDownloadSession(project, table));
            } else {
              downloadSession.set(tunnel.createDownloadSession(project, table, partitionSpec));
            }
            return true;
          } catch (Throwable e) {
            log.warn(
                String.format("retry create DownLoadSession %s %s Exception:", project, table), e);
            throw new RuntimeException("retry create DownLoadSession Exception", e);
          }
        });
    return downloadSession.get();
  }

  /**
   * 根据完整的名称获取项目和表名
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/2/22 17:44
   */
  public static String[] getProjectAndTable(String sourceId) {
    sourceId = sourceId.replaceFirst("odps\\.", "");
    String[] split = StringUtils.split(sourceId, ".");
    Assert.isTrue(split.length == 2, "数据源格式必须为projectName.tableName，当前格式为：" + sourceId);
    return split;
  }

  /**
   * 解析record数据成map
   *
   * @param record
   * @param schema
   * @return
   */
  public static Map<String, Object> consumeRecord(Record record, TableSchema schema) {
    Map<String, Object> map = Maps.newHashMap();
    List<com.aliyun.odps.Column> allColumn = schema.getColumns();
    allColumn.addAll(schema.getPartitionColumns());
    for (int i = 0; i < allColumn.size(); i++) {
      Column column = allColumn.get(i);
      String colValue;
      switch (column.getTypeInfo().getOdpsType()) {
        case BIGINT:
          {
            Long v = record.getBigint(i);
            colValue = getString(v);
            break;
          }
        case BOOLEAN:
          {
            Boolean v = record.getBoolean(i);
            colValue = getString(v);
            break;
          }
        case DATETIME:
          {
            Date v = record.getDatetime(i);
            colValue = getString(v);
            break;
          }
        case DOUBLE:
          {
            Double v = record.getDouble(i);
            colValue = getString(v);
            break;
          }
        case STRING:
          {
            String v = record.getString(i);
            colValue = v == null ? null : v;
            break;
          }
        default:
          throw new RuntimeException("Unknown column type: " + column.getTypeInfo().getOdpsType());
      }
      map.put(column.getName(), colValue);
    }
    return map;
  }

  private static String getString(Serializable v) {
    return v == null ? null : v.toString();
  }
}
