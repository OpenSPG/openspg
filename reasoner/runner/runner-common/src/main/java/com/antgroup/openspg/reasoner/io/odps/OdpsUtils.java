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

package com.antgroup.openspg.reasoner.io.odps;

import com.alibaba.fastjson.JSON;
import com.aliyun.odps.Column;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsType;
import com.aliyun.odps.PartitionSpec;
import com.aliyun.odps.ReloadException;
import com.aliyun.odps.Table;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TableTunnel.DownloadSession;
import com.aliyun.odps.tunnel.TableTunnel.UploadSession;
import com.aliyun.odps.tunnel.TunnelException;
import com.aliyun.odps.tunnel.io.CompressOption;
import com.aliyun.odps.tunnel.io.TunnelBufferedWriter;
import com.aliyun.odps.tunnel.io.TunnelRecordReader;
import com.antgroup.openspg.reasoner.common.exception.OdpsException;
import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.io.model.ReadRange;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class OdpsUtils {

  private static final int MAX_TRY_TIMES = 10;
  private static final int ODPS_WAIT_MS = 5000;
  private static final int ODPS_FLOW_EXCEEDED_WAIT_MS = 60 * 1000;

  /** query table schema from odps */
  public static TableSchema getTableSchema(OdpsTableInfo odpsTableInfo) {
    Odps odps = getODPSInstance(odpsTableInfo);
    int tryTimes = MAX_TRY_TIMES;
    while (tryTimes-- > 0) {
      TableSchema schema;
      try {
        schema = odps.tables().get(odpsTableInfo.getTable()).getSchema();
        return schema;
      } catch (ReloadException e) {
        if (e.getMessage().contains("Table not found")) {
          return null;
        }
        log.error("get_table_schema_error,table_info=" + JSON.toJSONString(odpsTableInfo), e);
        if (e.getMessage().contains("time out")) {
          continue;
        }
        throw new OdpsException("get_table_schema_error", e);
      }
    }
    throw new OdpsException("get_table_schema_error, reach max retry times", null);
  }

  /** create table schema form odps table info */
  public static TableSchema createSchema(OdpsTableInfo odpsTableInfo) {
    TableSchema schema = new TableSchema();
    for (Field field : odpsTableInfo.getLubeColumns()) {
      schema.addColumn(new Column(validColumnName(field.name()), toOdpsType(field.kgType())));
    }
    for (String name : odpsTableInfo.getPartition().keySet()) {
      schema.addPartitionColumn(new Column(validColumnName(name), OdpsType.STRING));
    }
    return schema;
  }

  /** check schema is match */
  public static boolean isSchemaMatch(OdpsTableInfo odpsTableInfo, TableSchema realSchema) {
    TableSchema schemaNeeded = createSchema(odpsTableInfo);

    List<String> columnsNeeded =
        schemaNeeded.getPartitionColumns().stream()
            .map(com.aliyun.odps.Column::getName)
            .sorted(String::compareTo)
            .collect(Collectors.toList());

    List<String> columnsReal =
        realSchema.getPartitionColumns().stream()
            .map(com.aliyun.odps.Column::getName)
            .sorted(String::compareTo)
            .collect(Collectors.toList());

    if (!columnsNeeded.equals(columnsReal)) {
      log.error(
          "odps_partition_columns_not_match, need_columns="
              + JSON.toJSONString(columnsNeeded)
              + ",real_columns="
              + JSON.toJSONString(columnsReal));
      return false;
    }

    columnsNeeded =
        schemaNeeded.getColumns().stream()
            .map(com.aliyun.odps.Column::getName)
            .sorted(String::compareTo)
            .collect(Collectors.toList());

    columnsReal =
        realSchema.getColumns().stream()
            .map(com.aliyun.odps.Column::getName)
            .sorted(String::compareTo)
            .collect(Collectors.toList());
    if (!columnsNeeded.equals(columnsReal)) {
      log.error(
          "odps_type_not_match, need_columns="
              + JSON.toJSONString(columnsNeeded)
              + ",real_columns="
              + JSON.toJSONString(columnsReal));
      return false;
    }

    /*
    List<OdpsType> neededType =
        schemaNeeded.getColumns().stream()
            .map(column -> column.getTypeInfo().getOdpsType())
            .sorted(Enum::compareTo)
            .collect(Collectors.toList());

    List<OdpsType> realType =
        realSchema.getColumns().stream()
            .map(column -> column.getTypeInfo().getOdpsType())
            .sorted(Enum::compareTo)
            .collect(Collectors.toList());
    Comment it out and wait for the type inference to complete
    if (!neededType.equals(realType)) {
      log.error(
          "odps_type_not_match, need_type="
              + JSON.toJSONString(neededType)
              + ",real_type="
              + JSON.toJSONString(realType));
      return false;
    }
     */

    return true;
  }

  /** create odps table */
  public static void createTable(OdpsTableInfo odpsTableInfo) {
    Odps odps = getODPSInstance(odpsTableInfo);
    TableSchema schema = createSchema(odpsTableInfo);
    log.info("start create table=" + odpsTableInfo.getProject() + "." + odpsTableInfo.getTable());
    try {
      odps.tables()
          .createTableWithLifeCycle(
              odpsTableInfo.getProject(), odpsTableInfo.getTable(), schema, null, true, 365L);
      log.info(
          "create table success," + odpsTableInfo.getProject() + "." + odpsTableInfo.getTable());
    } catch (com.aliyun.odps.OdpsException e) {
      log.error("create table error", e);
      throw new OdpsException("create_table_error", e);
    }
  }

  /** convert KgType to odps type */
  public static OdpsType toOdpsType(KgType kgType) {
    if (KTString$.MODULE$.equals(kgType)) {
      return OdpsType.STRING;
    } else if (KTLong$.MODULE$.equals(kgType) || KTInteger$.MODULE$.equals(kgType)) {
      return OdpsType.BIGINT;
    } else if (KTDouble$.MODULE$.equals(kgType)) {
      return OdpsType.DOUBLE;
    } else if (KTBoolean$.MODULE$.equals(kgType)) {
      return OdpsType.BOOLEAN;
    } else {
      throw new OdpsException("unsupported column type, " + kgType, null);
    }
  }

  /** change column name to odps valid name */
  public static String validColumnName(String columnName) {
    return columnName.replaceAll("\\.", "_").toLowerCase();
  }

  /** create odps instance */
  public static Odps getODPSInstance(OdpsTableInfo odpsTableInfo) {
    Account account = new AliyunAccount(odpsTableInfo.getAccessID(), odpsTableInfo.getAccessKey());
    Odps odps = new Odps(account);
    odps.setEndpoint(odpsTableInfo.getEndPoint());
    odps.setDefaultProject(odpsTableInfo.getProject());
    return odps;
  }

  /** get PartitionSpec from OdpsTableInfo */
  public static PartitionSpec getOdpsPartitionSpec(OdpsTableInfo odpsTableInfo) {
    if (null == odpsTableInfo.getPartition() || odpsTableInfo.getPartition().isEmpty()) {
      return null;
    }
    PartitionSpec partitionSpec = new PartitionSpec();
    for (Map.Entry<String, String> entry : odpsTableInfo.getPartition().entrySet()) {
      partitionSpec.set(entry.getKey(), entry.getValue());
    }
    return partitionSpec;
  }

  /** get upload session */
  public static UploadSession tryGetUploadSession(
      OdpsTableInfo odpsTableInfo, String id, int index, int parallel) {
    Odps odps = getODPSInstance(odpsTableInfo);
    TableTunnel tunnel = new TableTunnel(odps);
    if (!StringUtils.isEmpty(odpsTableInfo.getTunnelEndPoint())) {
      log.info("set odps tunnel endpoint=" + odpsTableInfo.getTunnelEndPoint());
      tunnel.setEndpoint(odpsTableInfo.getTunnelEndPoint());
    }
    int maxTryTimes = MAX_TRY_TIMES;
    while (--maxTryTimes >= 0) {
      try {
        return tunnel.getUploadSession(
            odpsTableInfo.getProject(),
            odpsTableInfo.getTable(),
            getOdpsPartitionSpec(odpsTableInfo),
            id,
            parallel,
            index);
      } catch (Throwable e) {
        log.error("create upload session error", e);
      }

      waitMs(ODPS_WAIT_MS);
    }
    throw new OdpsException("create upload session failed", null);
  }

  /** create upload session */
  public static UploadSession tryCreateUploadSession(OdpsTableInfo odpsTableInfo) {
    Odps odps = getODPSInstance(odpsTableInfo);
    TableTunnel tunnel = new TableTunnel(odps);
    if (!StringUtils.isEmpty(odpsTableInfo.getTunnelEndPoint())) {
      log.info("set odps tunnel endpoint=" + odpsTableInfo.getTunnelEndPoint());
      tunnel.setEndpoint(odpsTableInfo.getTunnelEndPoint());
    }
    int maxTryTimes = MAX_TRY_TIMES;
    while (--maxTryTimes >= 0) {
      try {
        PartitionSpec partitionSpec = getOdpsPartitionSpec(odpsTableInfo);
        if (null == partitionSpec) {
          return tunnel.createUploadSession(odpsTableInfo.getProject(), odpsTableInfo.getTable());
        } else {
          return tunnel.createUploadSession(
              odpsTableInfo.getProject(), odpsTableInfo.getTable(), partitionSpec);
        }
      } catch (Throwable e) {
        log.error("create upload session error", e);
      }

      waitMs(ODPS_WAIT_MS);
    }
    throw new OdpsException("create upload session failed", null);
  }

  /** create record writer */
  public static TunnelBufferedWriter tryCreateBufferRecordWriter(UploadSession uploadSession) {
    int maxTryTimes = MAX_TRY_TIMES;
    while (--maxTryTimes >= 0) {
      try {
        CompressOption option = new CompressOption();
        return new TunnelBufferedWriter(uploadSession, option);
      } catch (Throwable e) {
        log.error("create buffer writer error", e);
      }
      waitMs(ODPS_WAIT_MS);
    }
    throw new OdpsException("create buffer writer error", null);
  }

  /** create partition for table */
  public static void createPartition(OdpsTableInfo odpsTableInfo) {
    Odps odps = getODPSInstance(odpsTableInfo);
    PartitionSpec partitionSpec = getOdpsPartitionSpec(odpsTableInfo);
    if (null == partitionSpec) {
      return;
    }
    try {
      Table t = odps.tables().get(odpsTableInfo.getTable());
      if (!t.hasPartition(partitionSpec)) {
        t.createPartition(partitionSpec);
      }
    } catch (com.aliyun.odps.OdpsException e) {
      if (e.getMessage().contains("Partition already exists")) {
        // partition already exists, do not throw error
        // com.aliyun.odps.OdpsException: Catalog Service Failed, ErrorCode: 103,
        // Error Message: ODPS-0110061: Failed to run ddltask - AlreadyExistsException(message:
        // Partition already exists, existed values: ["$partition"])
        return;
      }
      throw new OdpsException("create_partition_error", e);
    }
  }

  /** create download session */
  public static DownloadSession tryCreateDownloadSession(
      TableTunnel tunnel, OdpsTableInfo odpsTableInfo) {
    PartitionSpec partition = getOdpsPartitionSpec(odpsTableInfo);
    int maxTryTimes = MAX_TRY_TIMES;
    Throwable lastError = null;
    while (--maxTryTimes >= 0) {
      try {
        DownloadSession downloadSession;
        if (null != partition) {
          downloadSession =
              tunnel.createDownloadSession(
                  odpsTableInfo.getProject(), odpsTableInfo.getTable(), partition);
        } else {
          downloadSession =
              tunnel.createDownloadSession(odpsTableInfo.getProject(), odpsTableInfo.getTable());
        }
        return downloadSession;
      } catch (TunnelException e) {
        if ("NoSuchPartition".equals(e.getErrorCode())) {
          // continue
          log.info(
              "table="
                  + odpsTableInfo.getProject()
                  + "."
                  + odpsTableInfo.getTable()
                  + ", partition="
                  + partition
                  + ", not exist");
          return null;
        } else if ("InvalidPartitionSpec".equals(e.getErrorCode())) {
          // if this table is not a partition table, we create download session without
          // PartitionSpec
          log.info(
              "table="
                  + odpsTableInfo.getProject()
                  + "."
                  + odpsTableInfo.getTable()
                  + ", partition="
                  + partition
                  + ", InvalidPartitionSpec");
          partition = null;
          continue;
        } else if ("FlowExceeded".equals(e.getErrorCode())) {
          log.warn("create_download_session, flow exceeded");
          // flow exceeded, continue
          // --maxTryTimes;
          waitMs(ODPS_WAIT_MS);
          continue;
        }
        log.error(
            "create_download_session_error, table="
                + odpsTableInfo.getProject()
                + "."
                + odpsTableInfo.getTable()
                + ", partition="
                + partition,
            e);
      } catch (Throwable e) {
        log.error(
            "create_download_session_error, table="
                + odpsTableInfo.getProject()
                + "."
                + odpsTableInfo.getTable()
                + ", partition="
                + partition,
            e);
        lastError = e;
      }

      waitMs(ODPS_WAIT_MS);
    }
    throw new OdpsException(
        "create_download_session_failed, time_out, table="
            + odpsTableInfo.getProject()
            + "."
            + odpsTableInfo.getTable()
            + ", partition="
            + partition,
        lastError);
  }

  /** open record reader */
  public static TunnelRecordReader tryOpenRecordReader(
      TableTunnel.DownloadSession downloadSession, long start, long count) {
    TunnelRecordReader recordReader;
    int maxTryTimes = MAX_TRY_TIMES;
    while (--maxTryTimes >= 0) {
      try {
        recordReader = downloadSession.openRecordReader(start, count);
        return recordReader;
      } catch (Exception e) {
        if (e instanceof com.aliyun.odps.OdpsException) {
          com.aliyun.odps.OdpsException oe = (com.aliyun.odps.OdpsException) e;
          if ("FlowExceeded".equals(oe.getErrorCode())) {
            log.warn("open_record_reader, flow exceeded");
            --maxTryTimes;
            waitMs(ODPS_FLOW_EXCEEDED_WAIT_MS);
            continue;
          }
        }
        log.error("open_record_reader_error", e);
      }
      waitMs(ODPS_WAIT_MS);
    }
    return null;
  }

  /** must call on driver */
  public static UploadSession createUploadSession(OdpsTableInfo odpsTableInfo) {
    // check odps table is exist
    TableSchema schema = getTableSchema(odpsTableInfo);
    if (null == schema) {
      if (odpsTableInfo.getProject().endsWith("_dev")) {
        createTable(odpsTableInfo);
        schema = getTableSchema(odpsTableInfo);
        if (null == schema) {
          throw new OdpsException("create table error", null);
        }
      } else {
        // table not exist
        throw new OdpsException(
            "table not exist, project="
                + odpsTableInfo.getProject()
                + ",table="
                + odpsTableInfo.getTable(),
            null);
      }
    }

    // check table schema is match
    if (!isSchemaMatch(odpsTableInfo, schema)) {
      throw new OdpsException(
          "table "
              + odpsTableInfo.getProject()
              + "."
              + odpsTableInfo.getTable()
              + ",schema not match",
          null);
    }

    // create partition and upload session
    createPartition(odpsTableInfo);
    return tryCreateUploadSession(odpsTableInfo);
  }

  /** delete odps table */
  public static void dropOdpsTable(OdpsTableInfo odpsTableInfo) throws Exception {
    Odps odps = getODPSInstance(odpsTableInfo);
    odps.tables().delete(odpsTableInfo.getProject(), odpsTableInfo.getTable());
    log.info("dropOdpsTable," + odpsTableInfo.getTableInfoKeyString());
  }

  /** get read range */
  public static Map<OdpsTableInfo, ReadRange> getReadRange(
      int parallel, int index, int allRound, int nowRound, Map<OdpsTableInfo, Long> tableCountMap) {

    List<Pair<OdpsTableInfo, Long>> tableCountList = new ArrayList<>();
    long allCount = 0;
    for (OdpsTableInfo tableInfo : tableCountMap.keySet()) {
      tableCountList.add(new ImmutablePair<>(tableInfo, tableCountMap.get(tableInfo)));
      allCount += tableCountMap.get(tableInfo);
    }
    tableCountList.sort(
        Comparator.comparingLong((Pair<OdpsTableInfo, Long> o) -> o.getRight())
            .thenComparing(Pair::getLeft));

    Map<OdpsTableInfo, ReadRange> result = new HashMap<>();

    ReadRange loadRange = getReadRange(parallel, index, allRound, nowRound, allCount, 1);
    long offset1 = 0;
    long offset2 = 0;
    long loadedCount = 0;
    for (Pair<OdpsTableInfo, Long> partitionInfo : tableCountList) {
      offset1 = offset2;
      offset2 += partitionInfo.getRight();

      long start = 0;
      if (loadedCount <= 0) {
        if (loadRange.getStart() >= offset1 && loadRange.getStart() < offset2) {
          start = loadRange.getStart() - offset1;
        } else {
          continue;
        }
      }

      if (loadRange.getEnd() <= offset2) {
        long end = start + (loadRange.getCount() - loadedCount);
        if (end == start) {
          continue;
        }
        result.put(partitionInfo.getLeft(), new ReadRange(start, end));
        loadedCount += end - start;
        break;
      } else {
        long end = offset2 - offset1;
        if (end == start) {
          continue;
        }
        result.put(partitionInfo.getLeft(), new ReadRange(start, end));
        loadedCount += end - start;
      }
    }

    result =
        result.entrySet().stream()
            .filter(tableReadRange -> tableReadRange.getValue().getCount() > 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return result;
  }

  private static ReadRange getReadRange(
      int parallel, int index, int allRound, int nowRound, long count, long minLoadSize) {
    long modSize = count % parallel;
    long share = count / parallel;
    long start;
    if (index < modSize) {
      share += 1;
      start = share * index;
    } else {
      start = modSize + share * index;
    }
    long end = start + share;

    if (share > 0 && share < minLoadSize) {
      // allocate to last node
      start = 0;
      if (index == parallel - 1) {
        end = count;
      } else {
        end = 0;
      }
    }

    if (allRound > 1) {
      long roundCount = end - start;
      long roundShare = roundCount / allRound;
      if (roundShare > 0 && roundShare < minLoadSize) {
        // allocate to last node
        if (nowRound == allRound - 1) {
          end = start + roundCount;
        } else {
          end = start;
        }
      } else {
        long roundModSize = roundCount % allRound;
        if (nowRound < roundModSize) {
          roundShare += 1;
          start += nowRound * roundShare;
        } else {
          start += roundModSize + roundShare * nowRound;
        }
        end = start + roundShare;
      }
    }

    return new ReadRange(start, end);
  }

  /** wait ms */
  public static void waitMs(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      log.warn("sleep_error", e);
    }
  }
}
