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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum AdvancedTypeEnum {
  ENTITY_TYPE("EntityType", "实体类型"),
  INDEX_TYPE("IndexType", "索引类型实体"),
  CONCEPT_TYPE("ConceptType", "概念类型"),
  EVENT_TYPE("EventType", "事件类型"),
  STANDARD_TYPE("StandardType", "标准类型");

  private String code;
  private String desc;

  AdvancedTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static AdvancedTypeEnum toEnumByCode(String code) {
    for (AdvancedTypeEnum advancedTypeEnum : values()) {
      if (advancedTypeEnum.getCode().equalsIgnoreCase(code)) {
        return advancedTypeEnum;
      }
    }
    throw new IllegalArgumentException("code is illegal");
  }

  public static AdvancedTypeEnum toEnum(String typeName) {
    for (AdvancedTypeEnum advancedTypeEnum : values()) {
      if (advancedTypeEnum.name().equalsIgnoreCase(typeName)) {
        return advancedTypeEnum;
      }
    }
    throw new IllegalArgumentException("typeName is illegal");
  }

  private static final Map<AdvancedTypeEnum, Set<AdvancedTypeEnum>> CONVERSION_RULES =
      Collections.unmodifiableMap(
          new HashMap<AdvancedTypeEnum, Set<AdvancedTypeEnum>>() {
            {
              put(
                  AdvancedTypeEnum.INDEX_TYPE,
                  Collections.unmodifiableSet(
                      new java.util.HashSet<AdvancedTypeEnum>() {
                        {
                          add(AdvancedTypeEnum.ENTITY_TYPE);
                        }
                      }));
            }
          });

  public static boolean canConvert(AdvancedTypeEnum old, AdvancedTypeEnum newType) {
    return old != null
        && newType != null
        && (old.equals(newType)
            || newType.equals(AdvancedTypeEnum.INDEX_TYPE)
            || CONVERSION_RULES.getOrDefault(old, Collections.emptySet()).contains(newType));
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
