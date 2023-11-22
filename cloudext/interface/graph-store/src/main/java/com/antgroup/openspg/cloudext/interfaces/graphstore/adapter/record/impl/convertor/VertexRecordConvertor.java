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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor;

import com.antgroup.openspg.builder.protocol.*;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreException;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.schema.core.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.server.schema.core.model.type.BaseSPGType;
import com.antgroup.openspg.server.schema.core.model.type.ConceptType;
import com.antgroup.openspg.server.schema.core.model.type.EntityType;
import com.antgroup.openspg.server.schema.core.model.type.EventType;
import com.antgroup.openspg.server.schema.core.model.type.StandardType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VertexRecordConvertor {

  public static VertexRecord toVertexRecord(BaseAdvancedRecord advancedRecord) {
    return new VertexRecord(
        advancedRecord.getId(),
        advancedRecord.getName(),
        PropertyRecordConvertor.toLPGProperties(advancedRecord.getProperties()));
  }

  public static List<VertexRecord> toVertexRecords(SPGPropertyRecord propertyRecord) {
    if (propertyRecord.isBasicType()) {
      throw GraphStoreException.unexpectedSPGPropertyRecordType(propertyRecord);
    }
    String vertexType = propertyRecord.getPropertyType().getObjectTypeRef().getName();
    List<String> vertexIdList = propertyRecord.getValue().getSplitIds();
    return vertexIdList.stream()
        .map(vertexId -> new VertexRecord(vertexId, vertexType, Collections.emptyList()))
        .collect(Collectors.toList());
  }

  public static BaseAdvancedRecord toAdvancedRecord(
      BaseSPGType baseSpgType, String bizId, Map<String, String> properties) {
    BaseAdvancedRecord advancedRecord = null;
    switch (baseSpgType.getSpgTypeEnum()) {
      case ENTITY_TYPE:
        advancedRecord =
            new EntityRecord(
                (EntityType) baseSpgType,
                bizId,
                PropertyRecordConvertor.toSPGProperties(properties, baseSpgType));
        break;
      case CONCEPT_TYPE:
        advancedRecord =
            new ConceptRecord(
                (ConceptType) baseSpgType,
                new ConceptIdentifier(bizId),
                PropertyRecordConvertor.toSPGProperties(properties, baseSpgType));
        break;
      case EVENT_TYPE:
        advancedRecord =
            new EventRecord(
                (EventType) baseSpgType,
                bizId,
                PropertyRecordConvertor.toSPGProperties(properties, baseSpgType));
        break;
      case STANDARD_TYPE:
        advancedRecord =
            new StandardRecord(
                (StandardType) baseSpgType,
                bizId,
                PropertyRecordConvertor.toSPGProperties(properties, baseSpgType));
        break;
      default:
        throw GraphStoreException.unexpectedSPGTypeEnum(baseSpgType.getSpgTypeEnum());
    }
    return advancedRecord;
  }
}
