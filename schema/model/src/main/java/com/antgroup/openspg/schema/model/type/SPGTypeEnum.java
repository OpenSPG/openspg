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

/** Type category in SPG framework. */
public enum SPGTypeEnum {

  /** @see BasicType */
  BASIC_TYPE,

  /** @see EntityType */
  ENTITY_TYPE,

  /** @see ConceptType */
  CONCEPT_TYPE,

  /** @see EventType */
  EVENT_TYPE,

  /** @see StandardType */
  STANDARD_TYPE,
  ;

  public static SPGTypeEnum toEnum(String spgType) {
    for (SPGTypeEnum spgTypeEnum : SPGTypeEnum.values()) {
      if (spgTypeEnum.name().equalsIgnoreCase(spgType)) {
        return spgTypeEnum;
      }
    }
    throw new IllegalArgumentException("illegal type=" + spgType);
  }
}
