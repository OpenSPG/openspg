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

package com.antgroup.openspg.builder.model.record.property;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPGPropertyValue extends BaseValObj {

  protected static final String SEPARATOR = ",";

  /** 原始的属性值 */
  private final String raw;

  /** 当该属性类型是非基础类型时，该字段指向属性类型的实例id，由于属性可能是多值，所以这里定义为list */
  private List<String> ids;

  /** 属性原始值标准化后的值，由于属性可能是多值，所以这里定义为list */
  private List<Object> stds;

  public SPGPropertyValue(String raw) {
    this.raw = raw;
  }

  public String getStdValue() {
    return stds.stream().map(Object::toString).collect(Collectors.joining(SEPARATOR));
  }
}
