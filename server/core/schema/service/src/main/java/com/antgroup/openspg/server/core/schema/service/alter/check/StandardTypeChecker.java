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

import com.antgroup.openspg.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.schema.model.type.StandardType;

/**
 * Standard type object structure checker, checks that each attribute value of the object meets
 * expectations. For example, standard types must specify whether they are spreadable, and forbid to
 * contain properties and relations.
 */
public class StandardTypeChecker extends BaseSpgTypeChecker {

  @Override
  public void checkAdvancedConfig(BaseAdvancedType advancedType, SchemaCheckContext context) {
    StandardType standardType = (StandardType) advancedType;

    String spgTypeName = standardType.getName();
    OperatorChecker.check(spgTypeName, standardType.getAdvancedConfig().getNormalizedOperator());

    if (null == standardType.getSpreadable()) {
      throw new IllegalArgumentException(
          String.format("spreadable of standard type:%s can not be null", spgTypeName));
    }
  }
}
