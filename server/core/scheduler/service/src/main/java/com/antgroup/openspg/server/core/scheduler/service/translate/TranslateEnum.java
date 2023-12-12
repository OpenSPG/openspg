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

package com.antgroup.openspg.server.core.scheduler.service.translate;

/**
 * Translate Enum
 * @Title: TranslateEnum.java @Description:
 */
public enum TranslateEnum {
  /** local dry run */
  LOCAL_DRY_RUN("localDryRunTranslate"),
  /** builder */
  KG_BUILDER("builderTranslate");

  private String type;

  TranslateEnum(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  /**
   * get by name
   *
   * @param name
   * @return
   */
  public static TranslateEnum getByName(String name, TranslateEnum defaultValue) {
    for (TranslateEnum value : TranslateEnum.values()) {
      if (value.name().equalsIgnoreCase(name)) {
        return value;
      }
    }
    return defaultValue;
  }

  /**
   * get by name
   *
   * @param name
   * @return
   */
  public static TranslateEnum getByName(String name) {
    return getByName(name, null);
  }
}
