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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util;

import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.common.model.exception.GraphStoreException;
import java.util.*;
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

  public static List<RelationRecord> toRelationRecords(
      BaseSPGType spgType, Map<String, String> properties) {

    Map<String, Relation> relations = new HashMap<>();

    for (Relation relation : spgType.getRelations()) {
      SPGTypeRef objectTypeRef = relation.getObjectTypeRef();
      String relationKey = String.format("%s#%s", relation.getName(), objectTypeRef.getName());
      relations.put(relationKey, relation);
    }

    Map<String, String> relationRecords = new HashMap<>();
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      Relation relation = relations.get(key);
      if (relation != null) {
        relationRecords.put(key, value);
      }
    }

    Map<String, Map<String, String>> subProperties = new HashMap<>();
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      String key = entry.getKey();
      String[] splits = key.split("#");
      if (splits.length == 3) {
        String relationKey = String.format("%s#%s", splits[0], splits[1]);
        Map<String, String> subPropertiesMap =
            subProperties.computeIfAbsent(relationKey, k -> new HashMap<>());
        subPropertiesMap.put(splits[2], entry.getValue());
      }
    }

    List<RelationRecord> results = new ArrayList<>(relations.size());
    for (Map.Entry<String, Relation> entry : relations.entrySet()) {
      String relationKey = entry.getKey();
      Relation relation = entry.getValue();

      String dstId = relationRecords.get(relationKey);
      if (dstId == null) {
        continue;
      }
      Map<String, String> props = subProperties.get(relationKey);
      results.add(toRelationRecord(relation, null, dstId, props));
    }
    return results;
  }
}
