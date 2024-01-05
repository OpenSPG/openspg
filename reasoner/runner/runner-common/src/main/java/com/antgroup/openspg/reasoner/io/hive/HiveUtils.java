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
import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Types;
import org.apache.parquet.schema.Types.GroupBuilder;
import org.apache.parquet.schema.Types.PrimitiveBuilder;

public class HiveUtils {

  public static HadoopOutputFile getHadoopOutputFile(HiveTableInfo hiveTableInfo, int index) {
    if (StringUtils.isNotEmpty(hiveTableInfo.getHadoopUserName())) {
      System.setProperty("HADOOP_USER_NAME", hiveTableInfo.getHadoopUserName());
    }
    Triple<String, String, List<String>> outputPathPair = getHdfsOutputPath(hiveTableInfo);
    Configuration conf = getHadoopConfiguration(hiveTableInfo, outputPathPair.getLeft());
    String fileName = outputPathPair.getMiddle();
    if (!outputPathPair.getRight().isEmpty()) {
      fileName += "/" + String.join("/", outputPathPair.getRight());
    }
    fileName += "/" + index;
    try {
      return HadoopOutputFile.fromPath(new Path(fileName), conf);
    } catch (IOException e) {
      throw new HiveException("create hadoop output file error, " + fileName, e);
    }
  }

  public static Configuration getHadoopConfiguration(
      HiveTableInfo hiveTableInfo, String defaultFs) {
    Configuration conf = new Configuration();
    if (StringUtils.isNotEmpty(hiveTableInfo.getCoreSiteXml())) {
      conf.addResource(new Path("file://" + hiveTableInfo.getCoreSiteXml()));
    }
    if (StringUtils.isNotEmpty(hiveTableInfo.getHdfsSiteXml())) {
      conf.addResource(new Path("file://" + hiveTableInfo.getHdfsSiteXml()));
    }
    conf.set("dfs.client.use.datanode.hostname", "true");
    if (StringUtils.isNotEmpty(defaultFs)) {
      conf.set("fs.defaultFS", defaultFs);
    }
    return conf;
  }

  /** get hive output path */
  public static Triple<String, String, List<String>> getHdfsOutputPath(
      HiveTableInfo hiveTableInfo) {
    List<String> partitionList = new ArrayList<>();
    if (null != hiveTableInfo.getPartition() && !hiveTableInfo.getPartition().isEmpty()) {
      // sort partition
      List<String> partitionKeyList = Lists.newArrayList(hiveTableInfo.getPartition().keySet());
      partitionKeyList.sort(Comparator.naturalOrder());
      for (String partitionKey : partitionKeyList) {
        String partitionValue = hiveTableInfo.getPartition().get(partitionKey);
        partitionList.add(partitionKey + "=" + partitionValue);
      }
    }
    if (StringUtils.isEmpty(hiveTableInfo.getOutputParquetPath())) {
      // use default hdfs path
      return new ImmutableTriple<>(null, "/tmp/holmes/" + hiveTableInfo.getTable(), partitionList);
    }
    Path hdfsPath = new Path(hiveTableInfo.getOutputParquetPath());
    URI hdfsUri = hdfsPath.toUri();
    if (StringUtils.isEmpty(hdfsUri.getAuthority())) {
      return new ImmutableTriple<>(null, hdfsUri.getPath(), partitionList);
    }
    return new ImmutableTriple<>(
        "hdfs://" + hdfsUri.getAuthority(), hdfsUri.getPath(), partitionList);
  }

  public static MessageType createParquetMessageType(HiveTableInfo hiveTableInfo) {
    GroupBuilder<MessageType> builder = Types.buildMessage();
    for (Field field : hiveTableInfo.getLubeColumns()) {
      PrimitiveBuilder<GroupBuilder<MessageType>> primitiveBuilder;
      if (KTInteger$.MODULE$.equals(field.kgType())) {
        primitiveBuilder = builder.optional(PrimitiveTypeName.INT32);
      } else if (KTLong$.MODULE$.equals(field.kgType())) {
        primitiveBuilder = builder.optional(PrimitiveTypeName.INT64);
      } else if (KTDouble$.MODULE$.equals(field.kgType())) {
        primitiveBuilder = builder.optional(PrimitiveTypeName.DOUBLE);
      } else if (KTBoolean$.MODULE$.equals(field.kgType())) {
        primitiveBuilder = builder.optional(PrimitiveTypeName.BOOLEAN);
      } else if (KTString$.MODULE$.equals(field.kgType())) {
        primitiveBuilder = builder.optional(PrimitiveTypeName.BINARY);
      } else {
        throw new HiveException("unknown schema type, " + field.kgType(), null);
      }
      builder = primitiveBuilder.named(field.name());
    }
    return builder.named(hiveTableInfo.getTable());
  }

  public static void data2Group(Object[] data, Group group, HiveTableInfo hiveTableInfo) {
    for (int i = 0; i < data.length; ++i) {
      Field field = hiveTableInfo.getLubeColumns().get(i);
      Object value = data[i];
      if (null == value) {
        continue;
      }

      if (KTInteger$.MODULE$.equals(field.kgType())) {
        group.append(field.name(), (Integer) value);
      } else if (KTLong$.MODULE$.equals(field.kgType())) {
        group.append(field.name(), (Long) value);
      } else if (KTDouble$.MODULE$.equals(field.kgType())) {
        group.append(field.name(), (Double) value);
      } else if (KTBoolean$.MODULE$.equals(field.kgType())) {
        group.append(field.name(), (Boolean) value);
      } else if (KTString$.MODULE$.equals(field.kgType())) {
        group.append(field.name(), (String) value);
      } else {
        throw new HiveException("unknown schema type, " + field.kgType(), null);
      }
    }
  }

  public static Connection initConnection(HiveTableInfo hiveTableInfo) {
    return initConnection(
        hiveTableInfo.getJdbcUrl(), hiveTableInfo.getJdbcUser(), hiveTableInfo.getJdbcPasswd());
  }

  public static Connection initConnection(String jdbcUrl, String user, String passwd) {
    try {
      return DriverManager.getConnection(jdbcUrl, user, passwd);
    } catch (SQLException e) {
      throw new HiveException("connect to " + jdbcUrl + ",error", e);
    }
  }

  public static Statement createStatement(Connection connection) {
    Statement stmt;
    try {
      stmt = connection.createStatement();
    } catch (SQLException e) {
      throw new HiveException("create statement error", e);
    }
    return stmt;
  }

  public static ResultSet runSql(Statement stmt, String sql) {
    try {
      boolean executeResult = stmt.execute(sql);
      if (executeResult) {
        return stmt.getResultSet();
      } else {
        throw new HiveException("sql has not result set, sql=" + sql, null);
      }
    } catch (SQLException e) {
      throw new HiveException("hive run sql," + sql, e);
    }
  }

  public static int runSqlUpdate(Statement stmt, String sql) {
    try {
      boolean executeResult = stmt.execute(sql);
      if (executeResult) {
        throw new HiveException("sql has not update count, sql=" + sql, null);
      } else {
        return stmt.getUpdateCount();
      }
    } catch (SQLException e) {
      throw new HiveException("hive run sql," + sql, e);
    }
  }

  private static final String HIVE_METASTORE_CONFIG_KEY = "hive.metastore.uris";

  /** get hive metastore url */
  public static String getHiveMetaStoreUrl(Statement stmt) {
    String getHiveMetaStoreUrisSql = "set " + HIVE_METASTORE_CONFIG_KEY;

    String metastoreUris;
    try {
      ResultSet resultSet = runSql(stmt, getHiveMetaStoreUrisSql);
      resultSet.next();
      String configStr = resultSet.getString(1);
      if (configStr.startsWith(HIVE_METASTORE_CONFIG_KEY)) {
        metastoreUris = configStr.substring(HIVE_METASTORE_CONFIG_KEY.length() + 1);
      } else {
        metastoreUris = configStr;
      }
    } catch (SQLException e) {
      throw new RuntimeException("hive run sql," + getHiveMetaStoreUrisSql, e);
    }
    return metastoreUris;
  }

  public static HiveWriterSession createHiveWriterSession(HiveTableInfo hiveTableInfo) {
    return new HiveWriterSession(hiveTableInfo);
  }

  private static final String HIVE_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";

  static {
    try {
      Class.forName(HIVE_DRIVER_NAME);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("init hive driver error", e);
    }
  }
}
