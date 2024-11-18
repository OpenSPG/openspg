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

package com.antgroup.openspg.builder.core.strategy.linking.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.PropertyLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import java.util.List;

public class IdEqualsLinking implements PropertyLinking {

  public static final IdEqualsLinking INSTANCE = new IdEqualsLinking();

  private IdEqualsLinking() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void linking(BasePropertyRecord record) throws LinkingException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();
    if (!objectTypeRef.isAdvancedType()) {
      throw new IllegalStateException();
    }

    List<String> rawValues = record.getRawValues();
    record.getValue().setStrStds(rawValues);
    record.getValue().setIds(rawValues);
  }
}
