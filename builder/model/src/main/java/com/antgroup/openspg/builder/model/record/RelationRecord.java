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

import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGSubPropertyRecord;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public class RelationRecord extends BaseSPGRecord {

  @Getter private final Relation relationType;

  @Getter private final String srcId;
  @Getter private final String dstId;

  private final List<SPGSubPropertyRecord> properties;

  public RelationRecord(
      Relation relationType, String srcId, String dstId, List<SPGSubPropertyRecord> properties) {
    super(SPGRecordTypeEnum.RELATION);
    this.relationType = relationType;
    this.srcId = srcId;
    this.dstId = dstId;
    this.properties = properties;
  }

  public List<SPGSubPropertyRecord> getSubProperties() {
    return properties;
  }

  public SPGSubPropertyRecord getSubPropertyRecord(SubProperty property) {
    for (SPGSubPropertyRecord record : getSubProperties()) {
      if (record.getSubPropertyType().getName().equals(property.getName())) {
        return record;
      }
    }
    return null;
  }

  @Override
  public List<BasePropertyRecord> getProperties() {
    return Collections.unmodifiableList(properties);
  }

  @Override
  public SPGTypeEnum getSpgTypeEnum() {
    return null;
  }
}
