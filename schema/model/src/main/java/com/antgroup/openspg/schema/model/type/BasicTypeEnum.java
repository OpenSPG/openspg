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

package com.antgroup.openspg.schema.model.type;

import com.google.common.collect.Lists;
import java.util.List;

/** The enumeration of basic types, including text, integer, and float. */
public enum BasicTypeEnum {

  /** String literal */
  TEXT("Text", Lists.newArrayList("TEXT", "STRING")),

  /** Integer number */
  LONG("Integer", Lists.newArrayList("LONG", "BIGINT", "INTEGER", "INT")),

  /** Float number */
  DOUBLE("Float", Lists.newArrayList("DOUBLE", "FLOAT")),
  ;

  /** The corresponding SPG type identification. */
  private final String flag;

  /** The corresponding data type. */
  public final List<String> mappingTypes;

  BasicTypeEnum(String flag, List<String> mappingTypes) {
    this.flag = flag;
    this.mappingTypes = mappingTypes;
  }

  public static BasicTypeEnum from(String name) {
    for (BasicTypeEnum basicTypeEnum : BasicTypeEnum.values()) {
      for (String type : basicTypeEnum.mappingTypes) {
        if (type.equalsIgnoreCase(name)) {
          return basicTypeEnum;
        }
      }
    }
    throw new IllegalArgumentException("illegal basicType=" + name);
  }

  public String getFlag() {
    return flag;
  }
}
