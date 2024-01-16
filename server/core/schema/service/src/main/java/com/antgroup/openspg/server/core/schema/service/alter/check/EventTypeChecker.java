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

package com.antgroup.openspg.server.core.schema.service.alter.check;

import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.EventType;
import org.apache.commons.collections4.CollectionUtils;

public class EventTypeChecker extends BaseSpgTypeChecker {

  @Override
  public void checkAdvancedConfig(BaseAdvancedType advancedType, SchemaCheckContext context) {
    EventType eventType = (EventType) advancedType;
    String schemaTypeName = eventType.getName();

    OperatorChecker.check(schemaTypeName, eventType.getAdvancedConfig().getLinkOperator());
    OperatorChecker.check(schemaTypeName, eventType.getAdvancedConfig().getFuseOperator());
    OperatorChecker.check(schemaTypeName, eventType.getAdvancedConfig().getExtractOperator());

    if (!containSubjectProperty(advancedType)) {
      throw new IllegalArgumentException(
          String.format(
              "event type must contain %s property",
              PropertyGroupEnum.SUBJECT.name().toLowerCase()));
    }
  }

  private boolean containSubjectProperty(BaseAdvancedType advancedType) {
    if (CollectionUtils.isEmpty(advancedType.getProperties())) {
      return false;
    }

    boolean found = false;
    for (Property property : advancedType.getProperties()) {
      if (PropertyGroupEnum.SUBJECT.equals(property.getPropertyGroup())) {
        found = true;
        break;
      }
    }
    return found;
  }
}
