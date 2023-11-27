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

package com.antgroup.openspg.server.common.model.datasource;

import com.antgroup.openspg.common.model.base.BaseModel;

public class DataSourceUsage extends BaseModel {

  /** Data source name */
  private final String dataSourceName;

  /** Data source usage object ID */
  private final String mountObjectId;

  /** Data source usage object type */
  private final DataSourceMountObjectTypeEnum mountObjectType;

  /** Data source usage type */
  private final DataSourceUsageTypeEnum usageType;

  /** Whether it is the default resource for this usage scenario */
  private final boolean isDefault;

  public DataSourceUsage(
      String dataSourceName,
      String mountObjectId,
      DataSourceMountObjectTypeEnum mountObjectType,
      DataSourceUsageTypeEnum usageType,
      boolean isDefault) {
    this.dataSourceName = dataSourceName;
    this.mountObjectId = mountObjectId;
    this.mountObjectType = mountObjectType;
    this.usageType = usageType;
    this.isDefault = isDefault;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public String getMountObjectId() {
    return mountObjectId;
  }

  public DataSourceMountObjectTypeEnum getMountObjectType() {
    return mountObjectType;
  }

  public DataSourceUsageTypeEnum getUsageType() {
    return usageType;
  }

  public boolean isDefault() {
    return isDefault;
  }
}
