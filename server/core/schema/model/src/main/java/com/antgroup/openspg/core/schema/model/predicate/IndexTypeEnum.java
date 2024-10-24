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
  VECTOR("VECTOR"),

  /** text index. */
  TEXT("TEXT"),

  /** text and vector index. */
  TEXT_AND_VECTOR("TEXT_AND_VECTOR");

  /** Name of index. */
  private final String nameEn;

  IndexTypeEnum(String nameEn) {
    this.nameEn = nameEn;
  }

  public static IndexTypeEnum toEnum(String name) {
    for (IndexTypeEnum indexTypeEnum : IndexTypeEnum.values()) {
      if (indexTypeEnum.name().equals(name)) {
        return indexTypeEnum;
      }
    }
    return null;
  }

  public String getNameEn() {
    return nameEn;
  }
}
