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

package com.antgroup.openspg.server.common.model.ref;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Data;

@Data
public class RefInfo extends BaseModel {

  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String name;

  private String refId;

  private String refType;

  private String refedId;

  private String refedType;

  private Integer status;

  private String config;

  public RefInfo() {}

  public RefInfo(
      String name, String refId, String refType, String refedId, String refedType, Integer status) {
    this.name = name;
    this.refId = refId;
    this.refType = refType;
    this.refedId = refedId;
    this.refedType = refedType;
    this.status = status;
  }
}
