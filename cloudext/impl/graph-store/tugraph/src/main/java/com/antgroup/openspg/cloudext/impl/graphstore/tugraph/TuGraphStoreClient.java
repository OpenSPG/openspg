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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.convertor.TuGraphRecordConvertor;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.PluginConfig;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.ProcedureInformation;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.util.TuGraphRecordUtils;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.util.TuGraphSchemaUtils;
import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGInternalIdGenerator;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.BaseLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.impl.NoChangedIdGenerator;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.BaseLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.LPGRecordStructEnum;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.TableLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGSchema;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateIndexOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.SchemaAtomicOperationEnum;
import com.antgroup.openspg.cloudext.interfaces.graphstore.util.TypeNameUtils;
import com.antgroup.openspg.server.common.model.api.ApiConstants;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.antgroup.tugraph.TuGraphDbRpcClient;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lgraph.Lgraph;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TuGraphStoreClient extends BaseLPGGraphStoreClient {

  private final String graphName;
  private final Double timeout;
  private final TuGraphDbRpcClient client;
  @Getter private final LPGInternalIdGenerator internalIdGenerator;
  @Getter private final LPGTypeNameConvertor typeNameConvertor;
  @Getter private final GraphStoreConnectionInfo connInfo;

  public TuGraphStoreClient(
      GraphStoreConnectionInfo connInfo, LPGTypeNameConvertor typeNameConvertor) {
    this.connInfo = connInfo;
    this.graphName = (String) connInfo.getNotNullParam(TuGraphConstants.GRAPH_NAME);
    this.timeout =
        Double.parseDouble(String.valueOf(connInfo.getNotNullParam(ApiConstants.TIMEOUT)));
    this.client = initTuGraphClient(connInfo);
    this.internalIdGenerator = new NoChangedIdGenerator();
    this.typeNameConvertor = typeNameConvertor;
  }

  @Override
  public LPGSchema querySchema() {
    List<VertexType> vertexTypes = null;
    List<EdgeType> edgeTypes = null;
    try {
      vertexTypes = TuGraphSchemaUtils.getVertexTypes(client, graphName, timeout);
      edgeTypes = TuGraphSchemaUtils.getEdgeTypes(client, graphName, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    LPGSchema lpgSchema = new LPGSchema(vertexTypes, edgeTypes);
    TypeNameUtils.restoreTypeName(lpgSchema, typeNameConvertor);
    return lpgSchema;
  }

  @Override
  public BaseLPGRecordStruct queryRecord(BaseLPGRecordQuery query) {
    String script = query.toScript(typeNameConvertor);
    String gqlResult = null;
    try {
      gqlResult = client.callCypher(script, graphName, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    TableLPGRecordStruct tableStruct = TuGraphRecordConvertor.toTable(gqlResult, typeNameConvertor);
    LPGRecordStructEnum structType = query.getQueryType().getStruct();
    switch (structType) {
      case TABLE:
        return tableStruct;
      case GRAPH:
        return tableStruct.toGraphLpgRecordStruct();
      default:
        throw new IllegalArgumentException("illegal structType=" + structType);
    }
  }

  @Override
  public void close() throws Exception {
    if (client != null) {
      client.logout();
    }
  }

  private TuGraphDbRpcClient initTuGraphClient(GraphStoreConnectionInfo connInfo) {
    String host = (String) connInfo.getNotNullParam(ApiConstants.HOST);
    String accessId = (String) connInfo.getNotNullParam(ApiConstants.ACCESS_ID);
    String accessKey = (String) connInfo.getNotNullParam(ApiConstants.ACCESS_KEY);
    TuGraphDbRpcClient client = null;
    try {
      client = new TuGraphDbRpcClient(host, accessId, accessKey);
      loadCppPlugin(client);
    } catch (Exception e) {
      throw new RuntimeException("init TuGraph Client failed", e);
    }
    return client;
  }

  private void loadCppPlugin(TuGraphDbRpcClient client) throws Exception {
    InputStream inputStream =
        getClass().getClassLoader().getResourceAsStream("plugins/plugin_config.properties");
    Properties properties = new Properties();
    properties.load(inputStream);
    String config = properties.getProperty("config");
    List<PluginConfig> pluginConfigs =
        JSON.parseObject(config, new TypeReference<List<PluginConfig>>() {});

    String procedures =
        client.listProcedures(Lgraph.PluginRequest.PluginType.CPP.name(), "v1", graphName);
    List<ProcedureInformation> procedureInformationList =
        JSONObject.parseObject(procedures, new TypeReference<List<ProcedureInformation>>() {});
    Set<String> loadedPluginNames =
        procedureInformationList.stream()
            .filter(Objects::nonNull)
            .map(procedure -> procedure.getPluginDescription().getName())
            .collect(Collectors.toSet());

    for (PluginConfig pluginConfig : pluginConfigs) {
      String pluginConfigName = pluginConfig.getName();
      if (!loadedPluginNames.contains(pluginConfigName)) {
        InputStream pluginFileStream =
            getClass().getClassLoader().getResourceAsStream(pluginConfig.getFilePath());
        Path tempFile = Files.createTempFile("temp", "");
        Files.copy(pluginFileStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        boolean loadProcedure =
            client.loadProcedure(
                tempFile.toAbsolutePath().toString(),
                pluginConfig.getType().name(),
                pluginConfig.getName(),
                pluginConfig.getCodeType().name(),
                pluginConfig.getDescription(),
                pluginConfig.isReadOnly(),
                pluginConfig.getVersion(),
                graphName);
        pluginFileStream.close();
        Files.deleteIfExists(tempFile);
        if (!loadProcedure) {
          throw new RuntimeException("tugraph load plugin failed");
        }
      }
    }
  }

  @Override
  public boolean createVertexType(CreateVertexTypeOperation operation) {
    TypeNameUtils.convertTypeName(operation, typeNameConvertor);
    try {
      TuGraphSchemaUtils.createLabel(client, graphName, operation, timeout);

      TuGraphSchemaUtils.addVertexIndexes(
          client,
          graphName,
          TuGraphSchemaUtils.getLabelName(operation),
          getCreateIndexOperations(operation),
          timeout);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  public boolean createEdgeType(CreateEdgeTypeOperation operation) {
    preProcess(operation);

    TypeNameUtils.convertTypeName(operation, typeNameConvertor);
    try {
      TuGraphSchemaUtils.createLabel(client, graphName, operation, timeout);

      TuGraphSchemaUtils.addEdgeIndexes(
          client,
          graphName,
          TuGraphSchemaUtils.getLabelName(operation),
          getCreateIndexOperations(operation),
          timeout);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  private <T extends BaseLPGSchemaOperation> List<CreateIndexOperation> getCreateIndexOperations(
      T operationRecord) {
    if (operationRecord == null) {
      return Collections.emptyList();
    }
    return operationRecord.getAtomicOperations().stream()
        .filter(
            atomicOperation ->
                SchemaAtomicOperationEnum.CREATE_INDEX.equals(
                    atomicOperation.getOperationTypeEnum()))
        .map(atomicOperation -> (CreateIndexOperation) atomicOperation)
        .collect(Collectors.toList());
  }

  private void preProcess(CreateEdgeTypeOperation operation) {
    operation.addProperty(
        new LPGProperty(TuGraphConstants.TUGRAPH_EDGE_INTERNAL_ID, BasicTypeEnum.TEXT, false));
    operation.createIndex(TuGraphConstants.TUGRAPH_EDGE_INTERNAL_ID, true, true);
  }

  @Override
  public boolean alterVertexType(AlterVertexTypeOperation operation) {
    TypeNameUtils.convertTypeName(operation, typeNameConvertor);
    try {
      TuGraphSchemaUtils.alterLabelDelFields(client, graphName, operation, timeout);
      TuGraphSchemaUtils.alterLabelAddFields(client, graphName, operation, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  public boolean alterEdgeType(AlterEdgeTypeOperation operation) {
    TypeNameUtils.convertTypeName(operation, typeNameConvertor);
    try {
      TuGraphSchemaUtils.alterLabelDelFields(client, graphName, operation, timeout);
      TuGraphSchemaUtils.alterLabelAddFields(client, graphName, operation, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  public boolean dropVertexType(DropVertexTypeOperation operation) {
    TypeNameUtils.convertTypeName(operation, typeNameConvertor);
    try {
      TuGraphSchemaUtils.deleteLabel(client, graphName, operation, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  public boolean dropEdgeType(DropEdgeTypeOperation operation) {
    TypeNameUtils.convertTypeName(operation, typeNameConvertor);
    try {
      TuGraphSchemaUtils.deleteLabel(client, graphName, operation, timeout);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  public boolean batchTransactionalSchemaOperations(List<BaseLPGSchemaOperation> operations) {
    return false;
  }

  @Override
  public void upsertVertex(String vertexTypeName, List<VertexRecord> vertexRecords)
      throws Exception {
    TypeNameUtils.convertTypeName(vertexRecords, typeNameConvertor);
    TuGraphRecordUtils.upsertVertexRecords(vertexRecords, client, graphName, timeout);
  }

  @Override
  public void deleteVertex(String vertexTypeName, List<VertexRecord> vertexRecords)
      throws Exception {
    TypeNameUtils.convertTypeName(vertexRecords, typeNameConvertor);
    TuGraphRecordUtils.deleteVertexRecords(vertexRecords, client, graphName, timeout);
  }

  @Override
  public void upsertEdge(String edgeTypeName, List<EdgeRecord> edgeRecords) throws Exception {
    TypeNameUtils.convertTypeName(edgeRecords, typeNameConvertor);
    TuGraphRecordUtils.upsertEdgeRecords(edgeRecords, client, graphName, timeout);
  }

  @Override
  public void deleteEdge(String edgeTypeName, List<EdgeRecord> edgeRecords) throws Exception {
    TypeNameUtils.convertTypeName(edgeRecords, typeNameConvertor);
    TuGraphRecordUtils.deleteEdgeRecords(edgeRecords, client, graphName, timeout);
  }
}
