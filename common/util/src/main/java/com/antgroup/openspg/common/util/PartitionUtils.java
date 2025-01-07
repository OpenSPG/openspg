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
package com.antgroup.openspg.common.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;

public class PartitionUtils {

  /**
   * 解析分区信息
   *
   * @param partitionStr
   * @param bizDate
   * @return
   */
  public static List<String> analysisPartition(String partitionStr, String bizDate) {
    return analysisPartition(partitionStr, bizDate, ",");
  }

  /**
   * 解析分区信息
   *
   * @param partitionStr
   * @param bizDate
   * @return
   */
  public static List<String> analysisPartition(
      String partitionStr, String bizDate, String delimiter) {
    List<String> partitions = new ArrayList<>();
    if (partitionStr.contains("&")) {
      String[] partitionArr = partitionStr.split("&");
      for (String partitionArrStr : partitionArr) {
        String partition = replaceDtVariable(partitionArrStr, bizDate, delimiter);
        partitions.add(partition);
      }
    } else if (partitionStr.contains("|")) {
      List<String> partition = replaceDtVariableAndMultiValue(partitionStr, bizDate);
      String basePartition = partition.get(0);
      String multiKey = partition.get(1);
      String multiValue = partition.get(2);
      String[] multiValueArr = multiValue.split("\\|");
      for (int i = 0; i < multiValueArr.length; i++) {
        String tmpPartition = basePartition + "," + multiKey + "=" + multiValueArr[i];
        tmpPartition = replaceDtVariable(tmpPartition, bizDate, delimiter);
        partitions.add(tmpPartition);
      }
    } else {
      String partition = replaceDtVariable(partitionStr, bizDate, delimiter);
      partitions.add(partition);
    }
    return partitions;
  }

  /**
   * * 获取dt变量
   *
   * @param partitionWithVariable
   * @param bizDate
   * @return
   */
  public static String replaceDtVariable(
      String partitionWithVariable, String bizDate, String delimiter) {
    PartitionSpec partitionSpec;
    try {
      partitionSpec = new PartitionSpec(partitionWithVariable);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          String.format("Partition information error：%s", partitionWithVariable));
    }
    if (StringUtils.isNotBlank(bizDate)) {
      for (String key : partitionSpec.keys()) {
        String value = partitionSpec.get(key);
        if (value.contains("$")) {
          partitionSpec.set(key, bizDate);
        }
      }
    }

    return partitionSpec.toString(true, delimiter);
  }

  /**
   * * 获取dt变量
   *
   * @param partition
   * @return
   */
  public static PartitionSpec replacePartitionSpec(String partition) {
    PartitionSpec partitionSpec;
    try {
      partitionSpec = new PartitionSpec(partition);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(String.format("Partition information error：%s", partition));
    }
    return partitionSpec;
  }

  /**
   * 解析分区信息
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/2/22 17:46
   */
  public static List<String> replaceDtVariableAndMultiValue(
      String partitionWithVariable, String bizDate) {
    PartitionSpec partitionSpec;
    try {
      partitionSpec = new PartitionSpec(partitionWithVariable);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          String.format("Partition information error：%s", partitionWithVariable));
    }
    String multiValue = "";
    String multiKey = "";
    if (StringUtils.isNotBlank(bizDate)) {
      for (String key : partitionSpec.keys()) {
        String value = partitionSpec.get(key);
        if (value.contains("$")) {
          partitionSpec.set(key, bizDate);
        }
        if (value.contains("|")) {
          multiKey = key;
          multiValue = value;
        }
      }
    }
    List<String> result = new LinkedList<>();
    result.add(partitionSpec.toString());
    result.add(multiKey);
    result.add(multiValue);
    return result;
  }

  /**
   * 根据完整的名称获取项目和表名
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/2/22 17:44
   */
  public static String[] getDatabaseAndTable(String sourceId) {
    String[] split = StringUtils.split(sourceId, ".");
    Assert.isTrue(
        split.length == 2,
        String.format(
            "sourceId must be in the format dbName.tableName，currently sourceId：%s", sourceId));
    return split;
  }

  public static class PartitionSpec {
    private Map<String, String> kv = new LinkedHashMap();

    public PartitionSpec() {}

    public PartitionSpec(String spec) {
      if (spec == null) {
        throw new IllegalArgumentException("Argument 'spec' cannot be null");
      } else {
        String[] groups = spec.split("[,/]");
        String[] var3 = groups;
        int var4 = groups.length;

        for (int var5 = 0; var5 < var4; ++var5) {
          String group = var3[var5];
          String[] kv = group.split("=");
          if (kv.length != 2) {
            throw new IllegalArgumentException("Invalid partition spec.");
          }

          String k = kv[0].trim();
          String v = kv[1].trim().replaceAll("'", "").replaceAll("\"", "");
          if (k.length() == 0 || v.length() == 0) {
            throw new IllegalArgumentException("Invalid partition spec.");
          }

          this.set(k, v);
        }
      }
    }

    public void set(String key, String value) {
      this.kv.put(key, value);
    }

    public String get(String key) {
      return this.kv.get(key);
    }

    public Set<String> keys() {
      return this.kv.keySet();
    }

    public boolean isEmpty() {
      return this.kv.isEmpty();
    }

    @Override
    public String toString() {
      return this.toString(true, ",");
    }

    public String toString(boolean quote, String delimiter) {
      List<String> entries = new LinkedList();
      String[] keys = this.keys().toArray(new String[0]);
      String[] var6 = keys;
      int var7 = keys.length;

      for (int var8 = 0; var8 < var7; ++var8) {
        String key = var6[var8];
        StringBuilder entryBuilder = new StringBuilder();
        entryBuilder.append(key).append("=");
        if (quote) {
          entryBuilder.append("'").append(this.kv.get(key)).append("'");
        } else {
          entryBuilder.append(this.kv.get(key));
        }

        entries.add(entryBuilder.toString());
      }

      return String.join(delimiter, entries);
    }
  }
}
