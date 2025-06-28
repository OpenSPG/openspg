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

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import lombok.Data;

@Data
public class UserModelDTO extends BaseModel {

  private Long id;
  private String provider;
  private String visibility;
  private String name;
  private JSONObject config;
  private String userNo;
  private String newName;
  private JSONObject customize;
}
