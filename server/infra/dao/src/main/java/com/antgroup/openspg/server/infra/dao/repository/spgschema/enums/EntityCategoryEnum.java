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

import com.antgroup.openspg.schema.model.type.SPGTypeEnum;

public enum EntityCategoryEnum {
  BASIC,

  STANDARD,

  ADVANCED,

  CONCEPT,

  EVENT;

  public static EntityCategoryEnum getEnum(String val) {
    for (EntityCategoryEnum entityCategoryEnum : EntityCategoryEnum.values()) {
      if (entityCategoryEnum.name().equalsIgnoreCase(val)) {
        return entityCategoryEnum;
      }
    }
    throw new IllegalArgumentException("illegal type=" + val);
  }

  public static EntityCategoryEnum getBySchemaType(SPGTypeEnum spgTypeEnum) {
    switch (spgTypeEnum) {
      case BASIC_TYPE:
        return BASIC;
      case STANDARD_TYPE:
        return STANDARD;
      case ENTITY_TYPE:
        return ADVANCED;
      case CONCEPT_TYPE:
        return CONCEPT;
      case EVENT_TYPE:
        return EVENT;
      default:
        throw new IllegalArgumentException("illegal type=" + spgTypeEnum.name());
    }
  }

  public static SPGTypeEnum toSpgType(String name) {
    EntityCategoryEnum entityCategoryEnum = getEnum(name);
    switch (entityCategoryEnum) {
      case BASIC:
        return SPGTypeEnum.BASIC_TYPE;
      case STANDARD:
        return SPGTypeEnum.STANDARD_TYPE;
      case ADVANCED:
        return SPGTypeEnum.ENTITY_TYPE;
      case CONCEPT:
        return SPGTypeEnum.CONCEPT_TYPE;
      case EVENT:
        return SPGTypeEnum.EVENT_TYPE;
      default:
        throw new IllegalArgumentException("illegal type=" + name);
    }
  }
}
