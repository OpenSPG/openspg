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

import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.EntityType;
import java.util.Collections;
import java.util.List;

public class EntityRecord extends BaseAdvancedRecord {

  private final EntityType entityType;

  private final String bizId;

  private final List<SPGPropertyRecord> properties;

  public EntityRecord(EntityType entityType, String bizId, List<SPGPropertyRecord> properties) {
    super(SPGRecordTypeEnum.ENTITY);
    this.bizId = bizId;
    this.entityType = entityType;
    this.properties = properties;
  }

  @Override
  public BaseSPGType getSpgType() {
    return entityType;
  }

  @Override
  public String getId() {
    return bizId;
  }

  @Override
  public List<SPGPropertyRecord> getSpgProperties() {
    return Collections.unmodifiableList(properties);
  }

  @Override
  public void addSpgProperties(SPGPropertyRecord record) {
    properties.add(record);
  }

  @Override
  public List<BasePropertyRecord> getProperties() {
    return Collections.unmodifiableList(properties);
  }
}
