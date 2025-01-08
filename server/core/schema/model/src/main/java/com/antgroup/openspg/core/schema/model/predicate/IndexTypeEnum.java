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

package com.antgroup.openspg.core.schema.model.predicate;

/** Property index type vector/text etc. */
public enum IndexTypeEnum {

  /** vector index. */
  VECTOR("VECTOR", "Vector"),

  /** text index. */
  TEXT("TEXT", "Text"),

  /** text and vector index. */
  TEXT_AND_VECTOR("TEXT_AND_VECTOR", "TextAndVector");

  /** Name of index. */
  private final String nameEn;

  /** Name of index. */
  private final String scriptName;

  IndexTypeEnum(String nameEn, String scriptName) {
    this.nameEn = nameEn;
    this.scriptName = scriptName;
  }

  public static IndexTypeEnum toEnum(String name) {
    for (IndexTypeEnum indexTypeEnum : IndexTypeEnum.values()) {
      if (indexTypeEnum.name().equals(name)) {
        return indexTypeEnum;
      }
    }
    return null;
  }

  public static IndexTypeEnum getByScriptName(String scriptName) {
    for (IndexTypeEnum indexTypeEnum : IndexTypeEnum.values()) {
      if (indexTypeEnum.getScriptName().equalsIgnoreCase(scriptName)) {
        return indexTypeEnum;
      }
    }
    throw new IllegalArgumentException("unknown type: " + scriptName);
  }

  public String getNameEn() {
    return nameEn;
  }

  public String getScriptName() {
    return scriptName;
  }
}
