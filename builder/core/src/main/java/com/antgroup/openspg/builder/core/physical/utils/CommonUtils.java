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

package com.antgroup.openspg.builder.core.physical.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.project.Project;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class CommonUtils {

  private static final String DOT = ".";

  private static final SPGTypeRef TEXT_REF =
      new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier.parse("Text")), SPGTypeEnum.BASIC_TYPE);

  public static ProjectSchema getProjectSchema(BuilderContext context) {
    HttpClientBootstrap.init(
        new ConnectionInfo(context.getSchemaUrl()).setConnectTimeout(6000).setReadTimeout(600000));

    SchemaFacade schemaFacade = new HttpSchemaFacade();
    ApiResponse<ProjectSchema> response =
        schemaFacade.queryProjectSchema(new ProjectSchemaRequest(context.getProjectId()));
    if (response.isSuccess()) {
      return response.getData();
    }
    throw new PipelineConfigException(
        "get schema error={}, schemaUrl={}, projectId={}",
        response.getErrorMsg(),
        context.getSchemaUrl(),
        context.getProjectId());
  }

  public static List<BaseSPGRecord> convertNodes(
      SubGraphRecord subGraph, ProjectSchema projectSchema, String namespace) {
    List<SubGraphRecord.Node> resultNodes = subGraph.getResultNodes();
    List<BaseSPGRecord> records = new ArrayList<>();
    if (CollectionUtils.isEmpty(resultNodes)) {
      return records;
    }
    for (SubGraphRecord.Node node : resultNodes) {
      BaseSPGType spgType = projectSchema.getByName(labelPrefix(namespace, node.getLabel()));
      if (spgType == null) {
        continue;
      }
      Map<String, String> stringMap =
          node.getProperties().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> entry.getValue() == null ? null : entry.getValue().toString()));
      BaseSPGRecord baseSPGRecord =
          VertexRecordConvertor.toAdvancedRecord(spgType, String.valueOf(node.getId()), stringMap);
      records.add(baseSPGRecord);
    }
    records.forEach(CommonUtils::replaceUnSpreadableStandardProperty);
    return records;
  }

  public static String labelPrefix(String namespace, String label) {
    if (label.contains(DOT)) {
      return label;
    }
    return namespace + DOT + label;
  }

  public static void addLabelPrefix(String namespace, List<SubGraphRecord> records) {
    records.forEach(
        record -> {
          List<SubGraphRecord.Node> resultNodes = record.getResultNodes();
          if (resultNodes != null) {
            resultNodes.forEach(
                resultNode -> {
                  String label = CommonUtils.labelPrefix(namespace, resultNode.getLabel());
                  resultNode.setLabel(label);
                });
          }
          List<SubGraphRecord.Edge> resultEdges = record.getResultEdges();
          if (resultEdges != null) {
            resultEdges.forEach(
                resultEdge -> {
                  String fromType = CommonUtils.labelPrefix(namespace, resultEdge.getFromType());
                  String toType = CommonUtils.labelPrefix(namespace, resultEdge.getToType());
                  resultEdge.setFromType(fromType);
                  resultEdge.setToType(toType);
                });
          }
        });
  }

  public static List<BaseSPGRecord> convertEdges(
      SubGraphRecord subGraph, ProjectSchema projectSchema, String namespace) {
    List<SubGraphRecord.Edge> resultEdges = subGraph.getResultEdges();
    List<BaseSPGRecord> records = new ArrayList<>();
    if (CollectionUtils.isEmpty(resultEdges)) {
      return records;
    }
    for (SubGraphRecord.Edge edge : resultEdges) {
      RelationIdentifier identifier =
          RelationIdentifier.parse(
              labelPrefix(namespace, edge.getFromType())
                  + '_'
                  + edge.getLabel()
                  + '_'
                  + labelPrefix(namespace, edge.getToType()));
      Relation relation = projectSchema.getByName(identifier);
      if (relation == null) {
        continue;
      }
      Map<String, String> stringMap =
          edge.getProperties().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> entry.getValue() == null ? null : entry.getValue().toString()));
      RelationRecord relationRecord =
          EdgeRecordConvertor.toRelationRecord(
              relation, String.valueOf(edge.getFrom()), String.valueOf(edge.getTo()), stringMap);
      records.add(relationRecord);
    }
    return records;
  }

  private static void replaceUnSpreadableStandardProperty(BaseSPGRecord record) {
    if (SPGRecordTypeEnum.RELATION.equals(record.getRecordType())) {
      return;
    }

    record
        .getProperties()
        .forEach(
            property -> {
              Property propertyType = ((SPGPropertyRecord) property).getProperty();
              propertyType.setObjectTypeRef(TEXT_REF);
              property.getValue().setSingleStd(property.getValue().getRaw());
            });
  }

  public static List<ChunkRecord.Chunk> readSource(
      String pythonExec,
      String pythonPaths,
      String hostAddr,
      Project project,
      String url,
      String token) {
    PythonInvokeMethod bridgeReader = PythonInvokeMethod.BRIDGE_READER;
    Long projectId = project.getId();
    JSONObject llm = JSONObject.parseObject(project.getConfig()).getJSONObject(CommonConstants.LLM);
    JSONObject pyConfig = new JSONObject();
    JSONObject scanner = new JSONObject();
    pyConfig.put(BuilderConstant.SCANNER, scanner);
    JSONObject reader = new JSONObject();
    pyConfig.put(BuilderConstant.READER, reader);

    if (StringUtils.isNotBlank(token)) {
      scanner.put(BuilderConstant.TYPE, BuilderConstant.YU_QUE);
      scanner.put(BuilderConstant.TOKEN, token);
      reader.put(BuilderConstant.TYPE, BuilderConstant.YU_QUE);
      reader.put(BuilderConstant.CUT_DEPTH, 1);
    } else {
      String extension = FilenameUtils.getExtension(url).toLowerCase();
      switch (extension) {
        case BuilderConstant.CSV:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.CSV);
          scanner.put(BuilderConstant.HEADER, true);
          JSONArray colNames = new JSONArray();
          colNames.add(BuilderConstant.CONTENT);
          scanner.put(BuilderConstant.COL_NAMES, colNames);
          reader.put(BuilderConstant.TYPE, BuilderConstant.DICT);
          break;
        case BuilderConstant.JSON:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.JSON);
          reader.put(BuilderConstant.TYPE, BuilderConstant.DICT);
          reader.put(BuilderConstant.ID_COL, BuilderConstant.ID);
          reader.put(BuilderConstant.NAME_COL, BuilderConstant.NAME);
          reader.put(BuilderConstant.CONTENT_COL, BuilderConstant.CONTENT);
          break;
        case BuilderConstant.TXT:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.TXT);
          break;
        case BuilderConstant.PDF:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.PDF);
          reader.put(BuilderConstant.CUT_DEPTH, 1);
          reader.put(BuilderConstant.LLM, llm);
          break;
        case BuilderConstant.MD:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.MD);
          reader.put(BuilderConstant.CUT_DEPTH, 1);
          reader.put(BuilderConstant.LLM, llm);
          break;
        case BuilderConstant.DOC:
        case BuilderConstant.DOCX:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.DOCX);
          reader.put(BuilderConstant.LLM, llm);
          break;
      }
    }
    PemjaConfig config =
        new PemjaConfig(
            pythonExec, pythonPaths, hostAddr, projectId, bridgeReader, Maps.newHashMap());
    List<Object> result;
    if (StringUtils.isNotBlank(token)) {
      List<String> urls = Lists.newArrayList();
      urls.add(url);
      result = (List<Object>) PemjaUtils.invoke(config, pyConfig.toJSONString(), urls);
    } else {
      result = (List<Object>) PemjaUtils.invoke(config, pyConfig.toJSONString(), url);
    }
    List<ChunkRecord.Chunk> chunkList =
        JSON.parseObject(
            JSON.toJSONString(result), new TypeReference<List<ChunkRecord.Chunk>>() {});
    return chunkList;
  }

  public static PemjaConfig getSplitterConfig(
      JSONObject pyConfig,
      String pythonExec,
      String pythonPaths,
      String hostAddr,
      Project project,
      JSONObject builderExtension) {
    Long projectId = project.getId();
    JSONObject llm = JSONObject.parseObject(project.getConfig()).getJSONObject(CommonConstants.LLM);
    JSONObject config = builderExtension.getJSONObject(BuilderConstant.SPLIT_CONFIG);
    Boolean semanticSplit = config.getBoolean(BuilderConstant.SEMANTIC_SPLIT);

    Long splitLength = config.getLong(BuilderConstant.SPLIT_LENGTH);

    PythonInvokeMethod splitter = PythonInvokeMethod.BRIDGE_COMPONENT;
    if (semanticSplit != null && semanticSplit) {
      pyConfig.put(BuilderConstant.TYPE, BuilderConstant.SEMANTIC);
      pyConfig.put(BuilderConstant.LLM, llm);
      pyConfig.put(BuilderConstant.PY_SPLIT_LENGTH, splitLength);
    } else {
      pyConfig.put(BuilderConstant.TYPE, BuilderConstant.LENGTH);
      pyConfig.put(BuilderConstant.PY_SPLIT_LENGTH, splitLength);
      pyConfig.put(BuilderConstant.PY_WINDOW_LENGTH, 0);
    }

    PemjaConfig pemjaConfig =
        new PemjaConfig(pythonExec, pythonPaths, hostAddr, projectId, splitter, Maps.newHashMap());

    return pemjaConfig;
  }
}
