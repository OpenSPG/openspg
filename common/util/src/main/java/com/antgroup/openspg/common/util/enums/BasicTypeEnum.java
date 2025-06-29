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
package com.antgroup.openspg.common.util.enums;

import lombok.Getter;

@Getter
public enum BasicTypeEnum {
  TEXT("Text", "文本"),
  INTEGER("Integer", "整型"),
  FLOAT("Float", "浮点数");

  private String code;
  private String desc;

  BasicTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static BasicTypeEnum getByCode(String code) {
    for (BasicTypeEnum typeEnum : values()) {
      if (typeEnum.getCode().equalsIgnoreCase(code)) {
        return typeEnum;
      }
    }
    throw new IllegalArgumentException("code is illegal");
  }

  public static boolean isBasicType(String code) {
    for (BasicTypeEnum typeEnum : values()) {
      if (typeEnum.getCode().equalsIgnoreCase(code)) {
        return true;
      }
    }
    return false;
  }
}
