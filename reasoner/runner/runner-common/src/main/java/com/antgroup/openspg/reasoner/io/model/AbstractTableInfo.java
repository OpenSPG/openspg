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

package com.antgroup.openspg.reasoner.io.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.antgroup.openspg.reasoner.common.table.Field;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public abstract class AbstractTableInfo implements Serializable {
  protected String project;
  protected String table;

  protected Map<String, String> partition;

  protected List<Field> columns;

  /** get lube field */
  @JSONField(serialize = false)
  public List<com.antgroup.openspg.reasoner.lube.catalog.struct.Field> getLubeColumns() {
    return this.columns.stream()
        .map(
            new Function<Field, com.antgroup.openspg.reasoner.lube.catalog.struct.Field>() {
              @Override
              public com.antgroup.openspg.reasoner.lube.catalog.struct.Field apply(Field field) {
                return new com.antgroup.openspg.reasoner.lube.catalog.struct.Field(
                    field.getName(), field.getType().getKgType(), true);
              }
            })
        .collect(Collectors.toList());
  }

  /** hash code */
  @Override
  public int hashCode() {
    return this.getTableInfoKeyString().hashCode();
  }

  /** get key */
  @JSONField(serialize = false)
  public String getTableInfoKeyString() {
    String str = "table=" + this.project + "." + this.table;
    String partitionString = getPartitionString();
    if (StringUtils.isNotEmpty(partitionString)) {
      str += ",partition[" + partitionString + "]";
    }
    return str;
  }

  /** convert map partition info to string */
  @JSONField(serialize = false)
  public String getPartitionString() {
    if (null == this.partition) {
      return null;
    }

    List<String> partitionKeys = Lists.newArrayList(this.partition.keySet());
    partitionKeys.sort(String::compareTo);

    StringBuilder sb = new StringBuilder();
    for (String partitionKey : partitionKeys) {
      String partitionValue = this.partition.get(partitionKey);
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(partitionKey).append("='").append(partitionValue).append("'");
    }
    return sb.toString();
  }
}
