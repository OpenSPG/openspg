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

package com.antgroup.openspg.builder.model.record;

import com.antgroup.openspg.schema.model.type.SPGTypeEnum;
import java.util.HashMap;
import java.util.Map;

public enum SPGRecordTypeEnum {
  ENTITY,
  CONCEPT,
  EVENT,
  STANDARD,
  RELATION,
  ;

  private static final Map<SPGTypeEnum, SPGRecordTypeEnum> TYPE_2_RECORD = new HashMap<>(5);

  static {
    TYPE_2_RECORD.put(SPGTypeEnum.ENTITY_TYPE, ENTITY);
    TYPE_2_RECORD.put(SPGTypeEnum.CONCEPT_TYPE, CONCEPT);
    TYPE_2_RECORD.put(SPGTypeEnum.EVENT_TYPE, EVENT);
    TYPE_2_RECORD.put(SPGTypeEnum.STANDARD_TYPE, STANDARD);
  }

  public boolean isInstanceOf(SPGTypeEnum spgTypeEnum) {
    return TYPE_2_RECORD.containsKey(spgTypeEnum);
  }
}
