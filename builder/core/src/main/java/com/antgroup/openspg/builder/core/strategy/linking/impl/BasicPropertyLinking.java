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

package com.antgroup.openspg.builder.core.strategy.linking.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.PropertyLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;

public class BasicPropertyLinking implements PropertyLinking {
  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void linking(BasePropertyRecord record) throws LinkingException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();
    if (!objectTypeRef.isBasicType()) {
      throw new IllegalStateException();
    }

    BasicTypeEnum basicType = BasicTypeEnum.from(objectTypeRef.getName());
    Object stdValue = null;
    String rawValue = record.getValue().getRaw();
    try {
      switch (basicType) {
        case LONG:
          stdValue = Long.valueOf(rawValue);
          break;
        case DOUBLE:
          stdValue = Double.valueOf(rawValue);
          break;
        default:
          stdValue = rawValue;
          break;
      }
    } catch (NumberFormatException e) {
      throw new LinkingException(e, "{} normalize error", rawValue);
    }
    record.getValue().setSingleStd(stdValue);
  }
}
