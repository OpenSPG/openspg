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

public enum LanguageEnum {
  EN("en-US", "英文"),
  ZH("zh-CN", "中文");

  private String code;
  private String desc;

  LanguageEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static LanguageEnum getByCode(String code) {
    for (LanguageEnum languageEnum : values()) {
      if (languageEnum.getCode().equals(code)) {
        return languageEnum;
      }
    }
    return null;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
