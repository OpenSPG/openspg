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
package com.antgroup.openspg.server.common.model.datasource;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Column extends BaseModel {

  private static final long serialVersionUID = 2897040705844372408L;

  private String name;
  private String type;
  private String comment;

  public Column() {}

  public Column(String name, String type, String comment) {
    this.name = name;
    this.type = type;
    this.comment = comment;
  }
}
