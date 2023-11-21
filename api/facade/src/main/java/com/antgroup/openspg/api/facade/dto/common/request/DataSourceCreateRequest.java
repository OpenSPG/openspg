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

package com.antgroup.openspg.api.facade.dto.common.request;

import com.antgroup.openspg.common.model.base.BaseRequest;

public class DataSourceCreateRequest extends BaseRequest {

  private String name;

  private String desc;

  private String type;

  private String connInfo;

  private String physicalInfo;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getConnInfo() {
    return connInfo;
  }

  public void setConnInfo(String connInfo) {
    this.connInfo = connInfo;
  }

  public String getPhysicalInfo() {
    return physicalInfo;
  }

  public void setPhysicalInfo(String physicalInfo) {
    this.physicalInfo = physicalInfo;
  }
}
