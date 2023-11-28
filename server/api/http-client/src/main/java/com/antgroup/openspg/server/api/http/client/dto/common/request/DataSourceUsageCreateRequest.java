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

package com.antgroup.openspg.server.api.http.client.dto.common.request;

import com.antgroup.openspg.common.model.base.BaseRequest;

public class DataSourceUsageCreateRequest extends BaseRequest {

  private String dataSourceName;

  private String usageType;

  private String mountObjectType;

  private String mountObjectId;

  private Boolean asDefault;

  public String getDataSourceName() {
    return dataSourceName;
  }

  public void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  public String getUsageType() {
    return usageType;
  }

  public void setUsageType(String usageType) {
    this.usageType = usageType;
  }

  public String getMountObjectType() {
    return mountObjectType;
  }

  public void setMountObjectType(String mountObjectType) {
    this.mountObjectType = mountObjectType;
  }

  public String getMountObjectId() {
    return mountObjectId;
  }

  public void setMountObjectId(String mountObjectId) {
    this.mountObjectId = mountObjectId;
  }

  public Boolean getAsDefault() {
    return asDefault;
  }

  public void setAsDefault(Boolean asDefault) {
    this.asDefault = asDefault;
  }
}
