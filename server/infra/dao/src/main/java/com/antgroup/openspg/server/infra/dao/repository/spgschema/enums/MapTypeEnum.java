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

package com.antgroup.openspg.server.infra.dao.repository.spgschema.enums;

public enum MapTypeEnum {
  EDGE,

  PROP,

  TYPE;

  public static MapTypeEnum getEnum(String name) {
    for (MapTypeEnum mapTypeEnum : MapTypeEnum.values()) {
      if (mapTypeEnum.name().equalsIgnoreCase(name)) {
        return mapTypeEnum;
      }
    }
    throw new IllegalArgumentException("illegal type=" + name);
  }
}
