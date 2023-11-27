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

package com.antgroup.openspg.server.core.builder.model.operator;

import com.antgroup.openspg.server.common.model.LangTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.core.schema.model.type.OperatorTypeEnum;

public class OperatorOverview extends BaseModel {

  /** Unique ID of the operator. */
  private final Long id;

  /** Name of the operator */
  private final String name;

  /** Description of the operator. */
  private final String desc;

  /** Type of the operator */
  private final OperatorTypeEnum type;

  /** Development language for the operator can be Java or Python. */
  private final LangTypeEnum langType;

  public OperatorOverview(
      Long id, String name, String desc, OperatorTypeEnum type, LangTypeEnum langType) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.type = type;
    this.langType = langType;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public OperatorTypeEnum getType() {
    return type;
  }

  public LangTypeEnum getLangType() {
    return langType;
  }
}
