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

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.server.common.model.exception.GraphStoreException;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Convertor for {@link EdgeRecord} and {@link RelationRecord}. */
public class EdgeRecordConvertor {

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
   * Convert {@link SPGPropertyRecord} in {@link BaseAdvancedRecord} into edge records.
   * Specifically, {@link BaseAdvancedRecord#getName()}, {@link BaseAdvancedRecord#getId()}, {@link
   * SPGPropertyRecord#getName()}, {@link SPGTypeRef#getName()} from {@link SPGPropertyRecord}, and
   * be assigned to start vertex's type name, start vertex's ID, edge label, and end vertex's type
   * name of {@link EdgeRecord}s. And end vertex's IDs of {@link EdgeRecord}s will be split from
   * {@link SPGPropertyRecord#getValue()}
   *
   * @param advancedRecord advanced record, such as {@link
   *     com.antgroup.openspg.builder.model.record.EventRecord EventRecord}, {@link
   *     com.antgroup.openspg.builder.model.record.EntityRecord EntityRecord} and {@link
   *     com.antgroup.openspg.builder.model.record.ConceptRecord ConceptRecord}.
   * @param spgPropertyRecord record of <tt>SPG</tt> property.
   * @return a list of {@link EdgeRecord}s
   */
  public static List<EdgeRecord> toEdgeRecords(
      BaseAdvancedRecord advancedRecord, SPGPropertyRecord spgPropertyRecord) {
    if (advancedRecord.isBasicType()) {
      throw GraphStoreException.unexpectedSPGRecordType(advancedRecord);
    }
    String srcType = advancedRecord.getName();
    String srcId = advancedRecord.getId();
    String edgeName = spgPropertyRecord.getName();
    String dstType = spgPropertyRecord.getPropertyType().getObjectTypeRef().getName();
    List<String> dstIdList = spgPropertyRecord.getValue().getSplitIds();
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

  public static RelationRecord toRelationRecord(
      Relation relationType, String srcId, String dstId, Map<String, String> properties) {
    return new RelationRecord(
        relationType,
        srcId,
        dstId,
        PropertyRecordConvertor.toSPGProperties(properties, relationType));
  }
}
