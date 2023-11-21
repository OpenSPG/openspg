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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.convertor.TuGraphSchemaConvertor;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.TypeEnum;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.AddEdgeIndexProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.AddVertexIndexProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.AlterLabelAddFieldsProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.AlterLabelDelFieldsProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.CreateLabelProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.DeleteLabelProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.GetEdgeSchemaProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.GetVertexSchemaProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.QueryLabelsProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result.GetEdgeSchemaResult;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result.GetVertexSchemaResult;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result.QueryLabelsResult;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseAlterTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseCreateTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateIndexOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;
import com.antgroup.tugraph.TuGraphDbRpcClient;
import com.antgroup.tugraph.TuGraphDbRpcException;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
public class TuGraphSchemaUtils {

  public static <T extends BaseCreateTypeOperation> boolean createLabel(
      TuGraphDbRpcClient client, String graphName, T createOperationRecord, Double timeout)
      throws Exception {
    if (createOperationRecord == null) {
      return true;
    }
    String cypher;
    switch (createOperationRecord.getOperationTypeEnum()) {
      case CREATE_EDGE_TYPE:
        cypher =
            CreateLabelProcedure.of((CreateEdgeTypeOperation) createOperationRecord).getCypher();
        break;
      case CREATE_VERTEX_TYPE:
        cypher =
            CreateLabelProcedure.of((CreateVertexTypeOperation) createOperationRecord).getCypher();
        break;
      default:
        throw new RuntimeException(
            "unexpected operation type when creating label: "
                + createOperationRecord.getOperationTypeEnum());
    }
    client.callCypher(cypher, graphName, timeout);
    return true;
  }

  public static boolean addVertexIndexes(
      TuGraphDbRpcClient client,
      String graphName,
      String labelName,
      List<CreateIndexOperation> createIndexOperations,
      Double timeout)
      throws Exception {
    if (CollectionUtils.isNotEmpty(createIndexOperations)) {
      for (CreateIndexOperation createIndexOperation : createIndexOperations) {
        String cypher = AddVertexIndexProcedure.of(labelName, createIndexOperation).getCypher();
        client.callCypher(cypher, graphName, timeout);
      }
    }
    return true;
  }

  public static boolean addEdgeIndexes(
      TuGraphDbRpcClient client,
      String graphName,
      String labelName,
      List<CreateIndexOperation> createIndexOperations,
      Double timeout)
      throws Exception {
    if (CollectionUtils.isNotEmpty(createIndexOperations)) {
      for (CreateIndexOperation createIndexOperation : createIndexOperations) {
        String cypher = AddEdgeIndexProcedure.of(labelName, createIndexOperation).getCypher();
        client.callCypher(cypher, graphName, timeout);
      }
    }
    return true;
  }

  public static <T extends BaseLPGSchemaOperation> String getLabelName(T operationRecord) {
    switch (operationRecord.getOperationTypeEnum()) {
      case CREATE_VERTEX_TYPE:
        return ((CreateVertexTypeOperation) operationRecord).getVertexTypeName();
      case ALTER_VERTEX_TYPE:
        return ((AlterVertexTypeOperation) operationRecord).getVertexTypeName();
      case DROP_VERTEX_TYPE:
        return ((DropVertexTypeOperation) operationRecord).getVertexTypeName();
      case CREATE_EDGE_TYPE:
        return ((CreateEdgeTypeOperation) operationRecord).getEdgeTypeName().getEdgeLabel();
      case ALTER_EDGE_TYPE:
        return ((AlterEdgeTypeOperation) operationRecord).getEdgeTypeName().getEdgeLabel();
      case DROP_EDGE_TYPE:
        return ((DropEdgeTypeOperation) operationRecord).getEdgeTypeName().getEdgeLabel();
      default:
        throw new RuntimeException("get label name error");
    }
  }

  public static <T extends BaseAlterTypeOperation> boolean alterLabelAddFields(
      TuGraphDbRpcClient client, String graphName, T alterOperationRecord, Double timeout)
      throws Exception {
    if (alterOperationRecord == null) {
      return true;
    }
    String cypher;
    switch (alterOperationRecord.getOperationTypeEnum()) {
      case ALTER_EDGE_TYPE:
        cypher =
            AlterLabelAddFieldsProcedure.of((AlterEdgeTypeOperation) alterOperationRecord)
                .getCypher();
        break;
      case ALTER_VERTEX_TYPE:
        cypher =
            AlterLabelAddFieldsProcedure.of((AlterVertexTypeOperation) alterOperationRecord)
                .getCypher();
        break;
      default:
        throw new RuntimeException(
            "unexpected operation type when alter label add fields: "
                + alterOperationRecord.getOperationTypeEnum());
    }
    client.callCypher(cypher, graphName, timeout);
    return true;
  }

  public static <T extends BaseAlterTypeOperation> boolean alterLabelDelFields(
      TuGraphDbRpcClient client, String graphName, T alterOperationRecord, Double timeout)
      throws Exception {
    if (alterOperationRecord == null) {
      return true;
    }
    String cypher;
    switch (alterOperationRecord.getOperationTypeEnum()) {
      case ALTER_EDGE_TYPE:
        cypher =
            AlterLabelDelFieldsProcedure.of((AlterEdgeTypeOperation) alterOperationRecord)
                .getCypher();
        break;
      case ALTER_VERTEX_TYPE:
        cypher =
            AlterLabelDelFieldsProcedure.of((AlterVertexTypeOperation) alterOperationRecord)
                .getCypher();
        break;
      default:
        throw new RuntimeException(
            "unexpected operation type when alter label del fields: "
                + alterOperationRecord.getOperationTypeEnum());
    }
    client.callCypher(cypher, graphName, timeout);
    return true;
  }

  public static <T extends BaseLPGSchemaOperation> boolean deleteLabel(
      TuGraphDbRpcClient client, String graphName, T operationRecord, Double timeout)
      throws Exception {
    if (operationRecord == null) {
      return true;
    }
    String cypher;
    String labelName;
    switch (operationRecord.getOperationTypeEnum()) {
      case DROP_EDGE_TYPE:
        cypher = DeleteLabelProcedure.of((DropEdgeTypeOperation) operationRecord).getCypher();
        labelName = ((DropEdgeTypeOperation) operationRecord).getEdgeTypeName().getEdgeLabel();
        break;
      case DROP_VERTEX_TYPE:
        cypher = DeleteLabelProcedure.of((DropVertexTypeOperation) operationRecord).getCypher();
        labelName = ((DropVertexTypeOperation) operationRecord).getVertexTypeName();
        break;
      default:
        throw new RuntimeException(
            "unexpected operation type when deleting label: "
                + operationRecord.getOperationTypeEnum());
    }
    try {
      client.callCypher(cypher, graphName, timeout);
    } catch (TuGraphDbRpcException e) {
      if (!tryToDeleteNotExistLabel(labelName, e.error)) {
        throw e;
      }
    }
    return true;
  }

  private static boolean tryToDeleteNotExistLabel(String dropLabelName, String errorMsg) {
    return String.format("Label [%s] does not exist.", dropLabelName).equals(errorMsg);
  }

  public static List<VertexType> getVertexTypes(
      TuGraphDbRpcClient client, String graphName, Double timeout) throws Exception {
    List<String> labelNames = getSchemaLabels(TypeEnum.VERTEX, client, graphName, timeout);
    if (CollectionUtils.isEmpty(labelNames)) {
      return Lists.newArrayList();
    }
    List<VertexType> vertexSchemaList = Lists.newArrayList();
    for (String labelName : labelNames) {
      VertexType vertexSchema = getVertexSchemaByLabel(labelName, client, graphName, timeout);
      if (vertexSchema != null) {
        vertexSchemaList.add(vertexSchema);
      }
    }
    return vertexSchemaList;
  }

  public static List<EdgeType> getEdgeTypes(
      TuGraphDbRpcClient client, String graphName, Double timeout) throws Exception {
    List<String> labelNames = getSchemaLabels(TypeEnum.EDGE, client, graphName, timeout);
    if (CollectionUtils.isEmpty(labelNames)) {
      return Lists.newArrayList();
    }
    List<EdgeType> edgeSchemaList = Lists.newArrayList();
    for (String labelName : labelNames) {
      EdgeType edgeSchema = getEdgeSchemaByLabel(labelName, client, graphName, timeout);
      if (edgeSchema != null) {
        edgeSchemaList.add(edgeSchema);
      }
    }
    return edgeSchemaList;
  }

  public static List<String> getSchemaLabels(
      TypeEnum dataTypeEnum, TuGraphDbRpcClient client, String graphName, Double timeout)
      throws Exception {
    String cypher = QueryLabelsProcedure.of(dataTypeEnum).getCypher();
    String labelsJsonStr = client.callCypher(cypher, graphName, timeout);
    List<QueryLabelsResult> results =
        JSON.parseObject(labelsJsonStr, new TypeReference<List<QueryLabelsResult>>() {});
    return TuGraphSchemaConvertor.toLabels(results);
  }

  public static VertexType getVertexSchemaByLabel(
      String labelName, TuGraphDbRpcClient client, String graphName, Double timeout)
      throws Exception {
    String cypher = GetVertexSchemaProcedure.of(labelName).getCypher();
    String vertexSchemaJsonStr = client.callCypher(cypher, graphName, timeout);
    List<GetVertexSchemaResult> results =
        JSON.parseObject(vertexSchemaJsonStr, new TypeReference<List<GetVertexSchemaResult>>() {});
    if (CollectionUtils.isEmpty(results)) {
      return null;
    }
    return TuGraphSchemaConvertor.toVertexType(results.get(0));
  }

  public static EdgeType getEdgeSchemaByLabel(
      String labelName, TuGraphDbRpcClient client, String graphName, Double timeout)
      throws Exception {
    String cypher = GetEdgeSchemaProcedure.of(labelName).getCypher();
    String edgeSchemaJsonStr = client.callCypher(cypher, graphName, timeout);
    List<GetEdgeSchemaResult> results =
        JSON.parseObject(edgeSchemaJsonStr, new TypeReference<List<GetEdgeSchemaResult>>() {});
    if (CollectionUtils.isEmpty(results)) {
      return null;
    }
    return TuGraphSchemaConvertor.toEdgeType(results.get(0));
  }
}
