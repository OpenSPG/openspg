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

import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.VertexRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.OneHopLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.common.model.exception.GraphStoreException;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGRecordAlterItem;
import com.antgroup.openspg.core.spgschema.model.type.WithSPGTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Provides a generic base implementation for {@link SPGRecord2LPGStrategy}. */
public abstract class BaseSPGRecord2LPGStrategy implements SPGRecord2LPGStrategy {

  /**
   * <code>LPG</code> graph store client is used for getting existed semantic edges in <tt>LPG</tt>,
   * which should be deleted before adding new semantic edges converted by {@link
   * SPGPropertyRecord}.
   */
  private BaseLPGGraphStoreClient lpgGraphStoreClient;

  @Override
  public void setLpgGraphStoreClient(BaseLPGGraphStoreClient lpgGraphStoreClient) {
    this.lpgGraphStoreClient = lpgGraphStoreClient;
  }

  /**
   * Simplify <tt>SPG</tt> record alter item itself to <tt>LPG</tt> record alter item.
   *
   * @param alterItem <tt>SPG</tt> record alter item itself
   * @return <tt>LPG</tt> record alter item
   */
  abstract LPGRecordAlterItem simplify(SPGRecordAlterItem alterItem);

  /** Normalize {@link SPGPropertyRecord}s in <tt>SPG</tt> record. */
  protected List<LPGRecordAlterItem> normalizeProperties(SPGRecordAlterItem alterItem) {
    if (!(alterItem.getSpgRecord() instanceof BaseAdvancedRecord)) {
      throw GraphStoreException.unexpectedSPGRecordType(alterItem.getSpgRecord());
    }
    BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) alterItem.getSpgRecord();

    // Get semantic properties from SPG record.
    List<SPGPropertyRecord> semanticProperties = advancedRecord.getSemanticPropertyRecords();

    // Use LPG graph store client to get existed semantic edges which should be deleted in LPG.
    List<LPGRecordAlterItem> resultAlterItems =
        new ArrayList<>(getEdgeRecordsToDelete(advancedRecord, semanticProperties));

    RecordAlterOperationEnum alterOperationEnum = alterItem.getAlterOp();
    // When operation is upsert, new semantic edges and vertices will be added to LPG.
    if (RecordAlterOperationEnum.UPSERT.equals(alterOperationEnum)) {
      // Get new semantic edges to upsert.
      resultAlterItems.addAll(getEdgeRecordsToUpsert(advancedRecord, semanticProperties));

      // Get semantic properties of standard type.
      List<SPGPropertyRecord> standardProperties =
          semanticProperties.stream()
              .filter(WithSPGTypeEnum::isStandardType)
              .collect(Collectors.toList());
      // Ensure that the semantic vertices representing standard entities exist in LPG.
      resultAlterItems.addAll(getVertexRecordsToUpsert(standardProperties));
    }
    return resultAlterItems;
  }

  private List<LPGRecordAlterItem> getEdgeRecordsToDelete(
      BaseAdvancedRecord advancedRecord, List<SPGPropertyRecord> semanticProperties) {
    GraphLPGRecordStruct recordStruct =
        (GraphLPGRecordStruct)
            lpgGraphStoreClient.queryRecord(
                new OneHopLPGRecordQuery(
                    advancedRecord.getId(),
                    advancedRecord.getName(),
                    semanticProperties.stream()
                        .map(
                            p ->
                                new EdgeTypeName(
                                    advancedRecord.getName(),
                                    p.getName(),
                                    p.getPropertyType().getObjectTypeRef().getName()))
                        .collect(Collectors.toSet()),
                    Direction.OUT));
    return recordStruct.getEdges().stream()
        .map(lpgRecord -> new LPGRecordAlterItem(RecordAlterOperationEnum.DELETE, lpgRecord))
        .collect(Collectors.toList());
  }

  private List<LPGRecordAlterItem> getEdgeRecordsToUpsert(
      BaseAdvancedRecord advancedRecord, List<SPGPropertyRecord> semanticProperties) {
    return semanticProperties.stream()
        .flatMap(p -> EdgeRecordConvertor.toEdgeRecords(advancedRecord, p).stream())
        .map(r -> new LPGRecordAlterItem(RecordAlterOperationEnum.UPSERT, r))
        .collect(Collectors.toList());
  }

  private List<LPGRecordAlterItem> getVertexRecordsToUpsert(
      List<SPGPropertyRecord> standardProperties) {
    return standardProperties.stream()
        .flatMap(p -> VertexRecordConvertor.toVertexRecords(p).stream())
        .map(r -> new LPGRecordAlterItem(RecordAlterOperationEnum.UPSERT, r))
        .collect(Collectors.toList());
  }
}
