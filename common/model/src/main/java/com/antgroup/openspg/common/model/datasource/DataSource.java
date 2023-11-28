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

package com.antgroup.openspg.common.model.datasource;

import com.antgroup.openspg.common.model.base.BaseModel;
import com.antgroup.openspg.common.model.datasource.DataSourceTypeEnum;
import com.antgroup.openspg.common.model.datasource.connection.BaseConnectionInfo;

public class DataSource extends BaseModel {

  /** Unique name of the data source */
  private final String uniqueName;

  /** Data source type */
  private final DataSourceTypeEnum dataSourceType;

  /** Physical cluster information */
  private final String physicalInfo;

  /** Data source connection information */
  private final BaseConnectionInfo connectionInfo;

  public DataSource(
      String uniqueName,
      DataSourceTypeEnum dataSourceType,
      String physicalInfo,
      BaseConnectionInfo connectionInfo) {
    this.uniqueName = uniqueName;
    this.dataSourceType = dataSourceType;
    this.physicalInfo = physicalInfo;
    this.connectionInfo = connectionInfo;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  public DataSourceTypeEnum getDataSourceType() {
    return dataSourceType;
  }

  public String getPhysicalInfo() {
    return physicalInfo;
  }

  public BaseConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }
}
