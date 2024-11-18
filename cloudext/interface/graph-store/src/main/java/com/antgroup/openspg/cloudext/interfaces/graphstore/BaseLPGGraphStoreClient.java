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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SPGRecordManipulateCmd;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.SPGRecord2LPGService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.SPGRecord2LPGServiceImpl;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.schema.SPGSchema2LPGService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.schema.impl.SPGSchema2LPGServiceImpl;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.VertexEdgeTypeOperationEnum;
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;

/**
 * Provides a generic base implementation for <tt>LPGGraphStoreClient</tt>.
 *
 * <p><tt>LPGGraphStoreClient</tt> has a built-in native adapter for converting <tt>SPG</tt> to
 * <tt>LPG</tt>, typically used as {@link GraphStoreClient GraphStoreClient} by the core service
 * layer, exposing two interfaces: {@link GraphStoreClient#alterSchema
 * alterSchema(SPGSchemaAlterCmd)} and {@link GraphStoreClient#manipulateRecord
 * manipulateRecord(SPGRecordManipulateCmd)}
 *
 * <p><tt>LPGGraphStoreClient</tt> <strong>SHOULD</strong> provide the following services and API
 * interfaces:
 *
 * <ul>
 *   <li><tt>{@link LPGDataDefinitionService LPGDataDefinitionService}:</tt> &nbsp&nbsp {@link
 *       LPGDataDefinitionService#createVertexType createVertexType(..)}, {@link
 *       LPGDataDefinitionService#createEdgeType createEdgeType(..)}, {@link
 *       LPGDataDefinitionService#alterVertexType alterVertexType(..)}, {@link
 *       LPGDataDefinitionService#alterEdgeType alterEdgeType(..)}, {@link
 *       LPGDataDefinitionService#dropVertexType dropVertexType(..)}, {@link
 *       LPGDataDefinitionService#dropEdgeType dropEdgeType(..)}
 *   <li><tt>{@link LPGDataManipulationService LPGDataManipulationService}:</tt> &nbsp&nbsp {@link
 *       LPGDataManipulationService#upsertVertex upsertVertex(..)}, {@link
 *       LPGDataManipulationService#upsertEdge upsertEdge(..)}, {@link
 *       LPGDataManipulationService#deleteVertex deleteVertex(..)}, {@link
 *       LPGDataManipulationService#deleteEdge deleteEdge(..)}
 *   <li><tt>{@link LPGDataQueryService LPGDataQueryService}:</tt> &nbsp&nbsp {@link
 *       LPGDataQueryService#queryRecord queryRecord(..)}
 * </ul>
 *
 * <strong><code>NOTE:</code></strong> in future version, if <tt>LPGGraphStoreClient</tt> additional
 * provides the following API interface, it will reduce the calls to the client:
 *
 * <ul>
 *   <li><tt>{@link LPGDataDefinitionService#batchTransactionalSchemaOperations
 *       LPGDataDefinitionService.batchTransactionalSchemaOperations(..)}</tt>
 * </ul>
 */
public abstract class BaseLPGGraphStoreClient
    implements GraphStoreClient,
        LPGDataDefinitionService,
        LPGDataManipulationService,
        LPGDataQueryService,
        LPGDataComputeService {

  private final SPGSchema2LPGService spgSchema2LpgService = new SPGSchema2LPGServiceImpl(this);
  private final SPGRecord2LPGService spgRecord2LpgService = new SPGRecord2LPGServiceImpl(this);

  @Override
  public boolean alterSchema(SPGSchemaAlterCmd cmd) {
    // Translate SPG schema operation into LPG schema operations
    List<BaseLPGSchemaOperation> lpgSchemaOperations =
        spgSchema2LpgService.translate(cmd.getSpgSchema());
    // Alter LPG schema one by one.
    // TODO in the future: add code switch to batch alter LPG schema transactional if LPG graph
    // store support to
    return alterLPGSchemaOneByOne(lpgSchemaOperations);
  }

  /**
   * Alter LPG schema one by one. Order for alter type operations is as the following:
   *
   * <ol>
   *   <li>{@link DropEdgeTypeOperation DropEdgeTypeOperation}s
   *   <li>{@link DropVertexTypeOperation DropVertexTypeOperation}s
   *   <li>{@link CreateVertexTypeOperation CreateVertexTypeOperation}s
   *   <li>{@link CreateEdgeTypeOperation CreateEdgeTypeOperation}s
   *   <li>{@link AlterVertexTypeOperation AlterVertexTypeOperation}s
   *   <li>{@link AlterEdgeTypeOperation AlterEdgeTypeOperation}s
   * </ol>
   *
   * @param lpgSchemaOperations operations for LPG schema.
   * @return <code>true</code> if results of alterations are all successfully, <code>false</code>
   *     otherwise
   */
  private boolean alterLPGSchemaOneByOne(List<BaseLPGSchemaOperation> lpgSchemaOperations) {
    // Do drop for LPG type, edge type first.
    lpgSchemaOperations.stream()
        .filter(
            record ->
                VertexEdgeTypeOperationEnum.DROP_EDGE_TYPE.equals(record.getOperationTypeEnum()))
        .forEach(record -> dropEdgeType((DropEdgeTypeOperation) record));
    lpgSchemaOperations.stream()
        .filter(
            record ->
                VertexEdgeTypeOperationEnum.DROP_VERTEX_TYPE.equals(record.getOperationTypeEnum()))
        .forEach(record -> dropVertexType((DropVertexTypeOperation) record));

    // Do create for LPG type, vertex type first.
    lpgSchemaOperations.stream()
        .filter(
            record ->
                VertexEdgeTypeOperationEnum.CREATE_VERTEX_TYPE.equals(
                    record.getOperationTypeEnum()))
        .forEach(record -> createVertexType((CreateVertexTypeOperation) record));
    lpgSchemaOperations.stream()
        .filter(
            record ->
                VertexEdgeTypeOperationEnum.CREATE_EDGE_TYPE.equals(record.getOperationTypeEnum()))
        .forEach(record -> createEdgeType((CreateEdgeTypeOperation) record));

    // Do alter for LPG type.
    lpgSchemaOperations.stream()
        .filter(
            record ->
                VertexEdgeTypeOperationEnum.ALTER_VERTEX_TYPE.equals(record.getOperationTypeEnum()))
        .forEach(record -> alterVertexType((AlterVertexTypeOperation) record));
    lpgSchemaOperations.stream()
        .filter(
            record ->
                VertexEdgeTypeOperationEnum.ALTER_EDGE_TYPE.equals(record.getOperationTypeEnum()))
        .forEach(record -> alterEdgeType((AlterEdgeTypeOperation) record));

    return true;
  }

  @Override
  public boolean manipulateRecord(SPGRecordManipulateCmd cmd) {
    List<LPGRecordAlterItem> alterItems =
        cmd.getAlterItems().stream()
            .flatMap(alterItem -> spgRecord2LpgService.convert(alterItem).stream())
            .collect(Collectors.toList());

    return manipulateLPGRecord(alterItems);
  }

  /**
   * Manipulate LPG records. Order for alter record operations is as the following:
   *
   * <ol>
   *   <li>DELETE {@link EdgeRecord EdgeRecord}s
   *   <li>DELETE {@link VertexRecord VertexRecord}s
   *   <li>UPSERT {@link VertexRecord VertexRecord}s
   *   <li>UPSERT {@link EdgeRecord EdgeRecord}s
   * </ol>
   *
   * @param alterItems alteration items for LPG record.
   * @return <code>true</code> if results of alterations are all successfully, <code>false</code>
   *     otherwise
   */
  public boolean manipulateLPGRecord(List<LPGRecordAlterItem> alterItems) {
    try {
      manipulateEdgeRecord(
          getEdgeRecord(alterItems, RecordAlterOperationEnum.DELETE),
          RecordAlterOperationEnum.DELETE);
      manipulateVertexRecord(
          getVertexRecord(alterItems, RecordAlterOperationEnum.DELETE),
          RecordAlterOperationEnum.DELETE);

      manipulateVertexRecord(
          getVertexRecord(alterItems, RecordAlterOperationEnum.UPSERT),
          RecordAlterOperationEnum.UPSERT);
      manipulateEdgeRecord(
          getEdgeRecord(alterItems, RecordAlterOperationEnum.UPSERT),
          RecordAlterOperationEnum.UPSERT);
      return true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void manipulateVertexRecord(
      Map<String, List<VertexRecord>> vertexRecordMap, RecordAlterOperationEnum alterOp)
      throws Exception {
    if (MapUtils.isEmpty(vertexRecordMap) || alterOp == null) {
      return;
    }
    for (Map.Entry<String, List<VertexRecord>> entry : vertexRecordMap.entrySet()) {
      if (RecordAlterOperationEnum.UPSERT.equals(alterOp)) {
        upsertVertex(entry.getKey(), entry.getValue());
      } else if (RecordAlterOperationEnum.DELETE.equals(alterOp)) {
        deleteVertex(entry.getKey(), entry.getValue());
      }
    }
  }

  private void manipulateEdgeRecord(
      Map<EdgeTypeName, List<EdgeRecord>> edgeRecordMap, RecordAlterOperationEnum alterOp)
      throws Exception {
    if (MapUtils.isEmpty(edgeRecordMap) || alterOp == null) {
      return;
    }
    for (Map.Entry<EdgeTypeName, List<EdgeRecord>> entry : edgeRecordMap.entrySet()) {
      if (RecordAlterOperationEnum.UPSERT.equals(alterOp)) {
        upsertEdge(entry.getKey().toString(), entry.getValue());
      } else if (RecordAlterOperationEnum.DELETE.equals(alterOp)) {
        deleteEdge(entry.getKey().toString(), entry.getValue());
      }
    }
  }

  private Map<String, List<VertexRecord>> getVertexRecord(
      List<LPGRecordAlterItem> alterItems, RecordAlterOperationEnum alterOp) {
    return alterItems.stream()
        .filter(i -> i.getAlterOp().equals(alterOp))
        .filter(i -> i.getLpgRecord().getRecordType().equals(LPGRecordTypeEnum.VERTEX))
        .map(i -> (VertexRecord) i.getLpgRecord())
        .collect(Collectors.groupingBy(VertexRecord::getVertexType, Collectors.toList()));
  }

  private Map<EdgeTypeName, List<EdgeRecord>> getEdgeRecord(
      List<LPGRecordAlterItem> alterItems, RecordAlterOperationEnum alterOp) {
    return alterItems.stream()
        .filter(i -> i.getAlterOp().equals(alterOp))
        .filter(i -> i.getLpgRecord().getRecordType().equals(LPGRecordTypeEnum.EDGE))
        .map(i -> (EdgeRecord) i.getLpgRecord())
        .collect(Collectors.groupingBy(EdgeRecord::getEdgeType, Collectors.toList()));
  }
}
