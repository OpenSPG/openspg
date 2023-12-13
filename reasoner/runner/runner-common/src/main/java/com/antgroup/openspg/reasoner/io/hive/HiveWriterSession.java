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

package com.antgroup.openspg.reasoner.io.hive;

import com.antgroup.openspg.reasoner.common.exception.HiveException;
import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.io.model.HiveTableInfo;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

@Slf4j(topic = "userlogger")
public class HiveWriterSession {
  private final HiveTableInfo hiveTableInfo;
  private Connection connection;

  /** hive writer session */
  public HiveWriterSession(HiveTableInfo hiveTableInfo) {
    this.hiveTableInfo = hiveTableInfo;
  }

  /** get session id */
  public String getSessionId() {
    return String.valueOf(this.hiveTableInfo.hashCode());
  }

  /** commit hive table */
  public void commit() {
    initConnection(
        this.hiveTableInfo.getJdbcUrl(),
        this.hiveTableInfo.getJdbcUser(),
        this.hiveTableInfo.getJdbcPasswd());
    createSinkTable(this.hiveTableInfo);
    log.info("create hive table done, " + this.hiveTableInfo.getTable());
  }

  private void initConnection(String jdbcUrl, String user, String passwd) {
    try {
      this.connection = DriverManager.getConnection(jdbcUrl, user, passwd);
    } catch (SQLException e) {
      throw new HiveException("connect to " + jdbcUrl + ",error", e);
    }
  }

  /** create hive sink table */
  private void createSinkTable(HiveTableInfo hiveTableInfo) {
    String createTableSql = getCreateTableSql(hiveTableInfo);

    String loadDataSql = getLoadDataSql(hiveTableInfo, false);
    Statement stmt;
    try {
      stmt = this.connection.createStatement();
    } catch (SQLException e) {
      throw new HiveException("create statement error", e);
    }
    try {
      stmt.execute(createTableSql);
    } catch (SQLException e) {
      throw new HiveException("hive run sql," + createTableSql, e);
    }
    if (!this.checkHdfsDataExist(hiveTableInfo)) {
      return;
    }
    try {
      stmt.execute(loadDataSql);
    } catch (SQLException e) {
      log.error("hive load hdfs error, sql=" + loadDataSql, e);
      throw new HiveException("hive load hdfs error, sql=" + loadDataSql, e);
    }
  }

  /** check hive table data is existing */
  private boolean checkHdfsDataExist(HiveTableInfo hiveTableInfo) {
    Triple<String, String, List<String>> outputPathPair =
        HiveUtils.getHdfsOutputPath(hiveTableInfo);
    Configuration conf = HiveUtils.getHadoopConfiguration(hiveTableInfo, outputPathPair.getLeft());
    String dataPath = getHiveDataPath(hiveTableInfo);
    try {
      FileSystem fs = FileSystem.get(conf);
      Path delPath = new Path(dataPath);
      boolean isExists = fs.exists(delPath);
      if (!isExists) {
        log.info("HolmesHiveClient,dataNotExist,path=" + dataPath);
      }
      return isExists;
    } catch (Exception e) {
      log.error("HolmesHiveClient,fs_exists,path=" + dataPath, e);
      return false;
    }
  }

  private String getHiveDataPath(HiveTableInfo hiveTableInfo) {
    Triple<String, String, List<String>> outputPathTriple =
        HiveUtils.getHdfsOutputPath(hiveTableInfo);
    StringBuilder sb = new StringBuilder();
    sb.append(outputPathTriple.getMiddle());
    if (!outputPathTriple.getRight().isEmpty()) {
      sb.append("/").append(String.join("/", outputPathTriple.getRight()));
    }
    return sb.toString();
  }

  /** generate a sql for load hive table */
  private String getLoadDataSql(HiveTableInfo hiveTableInfo, boolean withHdfsPrefix) {
    Triple<String, String, List<String>> outputPathTriple =
        HiveUtils.getHdfsOutputPath(hiveTableInfo);
    StringBuilder sb = new StringBuilder();
    sb.append("LOAD DATA INPATH '");
    if (withHdfsPrefix && StringUtils.isNotEmpty(outputPathTriple.getLeft())) {
      sb.append(outputPathTriple.getLeft());
    }
    sb.append(getHiveDataPath(hiveTableInfo));
    sb.append("' OVERWRITE INTO TABLE `").append(hiveTableInfo.getTable()).append("`");
    if (!outputPathTriple.getRight().isEmpty()) {
      // partition
      sb.append(" PARTITION(");
      boolean first = true;
      for (String partition : outputPathTriple.getRight()) {
        if (!first) {
          sb.append(",");
        }
        first = false;
        sb.append(partition);
      }
      sb.append(")");
    }
    return sb.toString();
  }

  /** generate sql for create hive table */
  private String getCreateTableSql(HiveTableInfo hiveTableInfo) {
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE IF NOT EXISTS `").append(hiveTableInfo.getTable()).append("`(\n");
    boolean first = true;
    for (Field field : hiveTableInfo.getLubeColumns()) {
      if (!first) {
        sb.append("\n  ,");
      } else {
        sb.append("  ");
      }
      first = false;
      sb.append("`").append(field.name()).append("` ");

      if (KTInteger$.MODULE$.equals(field.kgType())) {
        sb.append("INT");
      } else if (KTLong$.MODULE$.equals(field.kgType())) {
        sb.append("BIGINT");
      } else if (KTDouble$.MODULE$.equals(field.kgType())) {
        sb.append("DOUBLE");
      } else if (KTBoolean$.MODULE$.equals(field.kgType())) {
        sb.append("BOOLEAN");
      } else if (KTString$.MODULE$.equals(field.kgType())) {
        sb.append("STRING");
      } else {
        throw new HiveException("unknown schema type, " + field.kgType(), null);
      }
    }
    sb.append("\n)\n");

    if (hiveTableInfo.getPartition().size() > 0) {
      sb.append("PARTITIONED BY (\n");
    }
    first = true;
    for (Map.Entry<String, String> entry : hiveTableInfo.getPartition().entrySet()) {
      if (!first) {
        sb.append("\n  ,");
      } else {
        sb.append("  ");
      }
      first = false;
      sb.append("`").append(entry.getKey()).append("` STRING");
    }
    if (hiveTableInfo.getPartition().size() > 0) {
      sb.append("\n)\n");
    }
    sb.append("STORED AS PARQUET\n");
    sb.append("TBLPROPERTIES ( \n").append("  'transactional'='false' \n").append(")");
    return sb.toString();
  }
}
