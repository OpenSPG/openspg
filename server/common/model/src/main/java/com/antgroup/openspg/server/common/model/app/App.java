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

package com.antgroup.openspg.server.common.model.app;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Data;

@Data
public class App extends BaseModel {
  private Long id;
  private String name;
  private String logo;
  private String description;
  private JSONObject config;
  private String userNo;
  private String accessToken;
  // account token status
  private Integer status;
  private Date appDeployTime;
  private String alias;

  public App() {}

  public App(
      Long id,
      String name,
      String logo,
      String description,
      JSONObject config,
      String userNo,
      String alias) {
    this.id = id;
    this.name = name;
    this.logo = logo;
    this.description = description;
    this.config = config;
    this.userNo = userNo;
    this.alias = alias;
  }
}
