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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.strategy;

import com.antgroup.openspg.builder.protocol.ConceptRecord;
import com.antgroup.openspg.builder.protocol.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.protocol.SPGRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.server.schema.core.model.identifier.ConceptIdentifier;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Strategy for translation from alter item of {@link ConceptRecord} to {@link LPGRecordAlterItem}s.
 */
public class ConceptRecord2LPGStrategy extends SPGTypeRecord2LPGStrategy {

  @Override
  public List<LPGRecordAlterItem> translate(SPGRecordAlterItem item) {
    List<LPGRecordAlterItem> lpgRecordAlterItems = normalizeProperties(item);

    lpgRecordAlterItems.add(simplify(item));

    // Generate alteration of semantic edge which is from concept vertex to its father.
    lpgRecordAlterItems.addAll(
        generateConceptSemanticEdgeAlterItems(
            (ConceptRecord) item.getSpgRecord(), item.getAlterOp()));
    return lpgRecordAlterItems;
  }

  private List<LPGRecordAlterItem> generateConceptSemanticEdgeAlterItems(
      ConceptRecord conceptRecord, RecordAlterOperationEnum recordAlterOperationEnum) {
    // If it is root concept vertex, skip it.
    if (isRootConcept(conceptRecord)) {
      return Lists.newArrayList();
    }
    List<LPGRecordAlterItem> alterItems = Lists.newArrayList();
    alterItems.add(
        new LPGRecordAlterItem(
            recordAlterOperationEnum, getConceptSemanticEdgeRecord(conceptRecord)));
    return alterItems;
  }

  private EdgeRecord getConceptSemanticEdgeRecord(ConceptRecord conceptRecord) {
    ConceptIdentifier conceptName = conceptRecord.getConceptName();
    String srcId = conceptName.getId();
    String dstId = conceptName.getFatherId();
    EdgeTypeName edgeTypeName =
        new EdgeTypeName(
            conceptRecord.getConceptType().getName(),
            conceptRecord.getConceptType().getConceptLayerConfig().getHypernymPredicate(),
            conceptRecord.getConceptType().getName());
    List<LPGPropertyRecord> propertyRecords = Lists.newArrayList();
    propertyRecords.add(new LPGPropertyRecord(EdgeType.SRC_ID, srcId));
    propertyRecords.add(new LPGPropertyRecord(EdgeType.DST_ID, dstId));
    return new EdgeRecord(srcId, dstId, edgeTypeName, propertyRecords);
  }

  private boolean isRootConcept(ConceptRecord conceptRecord) {
    ConceptIdentifier name = conceptRecord.getConceptName();
    return StringUtils.isBlank(name.getFatherId());
  }
}
