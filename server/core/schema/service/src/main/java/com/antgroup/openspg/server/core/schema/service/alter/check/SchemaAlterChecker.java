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

package com.antgroup.openspg.server.core.schema.service.alter.check;

import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/** Provide some methods to validate advanced type draft, check the draft content is valid. */
public class SchemaAlterChecker {

  /** The validator of each kind of schema type. */
  private final Map<SPGTypeEnum, BaseSpgTypeChecker> validatorMap;

  public SchemaAlterChecker() {
    validatorMap = new HashMap<>();
    validatorMap.put(SPGTypeEnum.STANDARD_TYPE, new StandardTypeChecker());
    validatorMap.put(SPGTypeEnum.ENTITY_TYPE, new EntityTypeChecker());
    validatorMap.put(SPGTypeEnum.CONCEPT_TYPE, new ConceptTypeChecker());
    validatorMap.put(SPGTypeEnum.EVENT_TYPE, new EventTypeChecker());
  }

  public void check(SchemaCheckContext context) {
    if (CollectionUtils.isEmpty(context.getAlterTypes())) {
      throw new IllegalArgumentException("schema draft is empty");
    }

    for (BaseAdvancedType advancedType : context.getAlterTypes()) {
      advancedType.setProjectId(context.getProjectId());

      BaseSpgTypeChecker checker = validatorMap.get(advancedType.getSpgTypeEnum());
      if (checker != null) {
        checker.check(advancedType, context);
      }
    }
  }
}
