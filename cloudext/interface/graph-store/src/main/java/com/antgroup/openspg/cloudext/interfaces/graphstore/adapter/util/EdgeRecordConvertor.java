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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util;

import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.server.common.model.exception.GraphStoreException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Convertor for {@link EdgeRecord} and {@link RelationRecord}. */
public class EdgeRecordConvertor {

  /** Convert the SPG record to an LPG record. */
  public static EdgeRecord toEdgeRecord(RelationRecord relationRecord) {
    Relation relationType = relationRecord.getRelationType();
    return new EdgeRecord(
        relationRecord.getSrcId(),
        relationRecord.getDstId(),
        new EdgeTypeName(
            relationType.getSubjectTypeRef().getName(),
            relationType.getName(),
            relationType.getObjectTypeRef().getName()),
        PropertyRecordConvertor.toLPGProperties(relationRecord.getProperties()));
  }

  /**
   * For an advanced SPG record, when one of its property types is also an advanced type, an edge
   * will be created between the current instance and the instance corresponding to that property
   * value.
   */
  public static List<EdgeRecord> toEdgeRecords(
      BaseAdvancedRecord advancedRecord, SPGPropertyRecord spgPropertyRecord) {
    if (advancedRecord.isBasicType()) {
      throw GraphStoreException.unexpectedSPGRecordType(advancedRecord);
    }
    String srcType = advancedRecord.getName();
    String srcId = advancedRecord.getId();
    String edgeName = spgPropertyRecord.getName();
    String dstType = spgPropertyRecord.getProperty().getObjectTypeRef().getName();
    List<String> dstIdList = spgPropertyRecord.getValue().getIds();
    return dstIdList.stream()
        .map(
            dstId ->
                new EdgeRecord(
                    srcId,
                    dstId,
                    new EdgeTypeName(srcType, edgeName, dstType),
                    Collections.emptyList()))
        .collect(Collectors.toList());
  }

  /**
   * Convert the LPG record to an SPG record, mainly used in the mapping or reasoning process of
   * knowledge builder.
   */
  public static RelationRecord toRelationRecord(
      Relation relationType, String srcId, String dstId, Map<String, String> properties) {
    return new RelationRecord(
        relationType,
        srcId,
        dstId,
        PropertyRecordConvertor.toSPGProperties(properties, relationType));
  }
}
