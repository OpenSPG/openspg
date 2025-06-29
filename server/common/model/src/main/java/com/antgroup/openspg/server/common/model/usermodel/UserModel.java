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
package com.antgroup.openspg.server.common.model.usermodel;

import com.alibaba.fastjson.JSONArray;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Data;

@Data
public class UserModel extends BaseModel {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String modifier;
  private String userNo;
  private String instanceId;
  private String visibility;
  private String provider;
  private String name;
  private String config;
  private JSONArray modelList;

  public UserModel() {}

  public UserModel(
      String instanceId,
      String visibility,
      String provider,
      String name,
      String userNo,
      String modifier) {
    this.instanceId = instanceId;
    this.visibility = visibility;
    this.provider = provider;
    this.name = name;
    this.userNo = userNo;
    this.modifier = modifier;
  }
}
