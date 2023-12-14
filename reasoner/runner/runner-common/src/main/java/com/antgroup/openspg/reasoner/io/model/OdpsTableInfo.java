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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class OdpsTableInfo extends AbstractTableInfo implements Comparable<OdpsTableInfo> {
  private String accessID;
  private String accessKey;
  private String endPoint = "http://service.odps.aliyun-inc.com/api";
  private String tunnelEndPoint;

  /** use for odps writer */
  private String uploadSessionId;

  /**
   * Getter method for property <tt>accessKey</tt>.
   *
   * @return property value of accessKey
   */
  @JSONField(serialize = false)
  public String getAccessKey() {
    return accessKey;
  }

  /** toString */
  @Override
  public String toString() {
    String str =
        "table="
            + this.project
            + "."
            + this.table
            + ",accessId="
            + this.accessID
            + ",endPoint="
            + this.endPoint;
    if (StringUtils.isNotEmpty(this.tunnelEndPoint)) {
      str += ",tunnelEndPoint=" + this.tunnelEndPoint;
    }
    String partitionString = getPartitionString();
    if (StringUtils.isNotEmpty(partitionString)) {
      str += ",partition[" + partitionString + "]";
    }
    str += ",uploadSessionId=" + this.uploadSessionId;
    return str;
  }

  @Override
  public int compareTo(OdpsTableInfo that) {
    return this.getTableInfoKeyString().compareTo(that.getTableInfoKeyString());
  }
}
