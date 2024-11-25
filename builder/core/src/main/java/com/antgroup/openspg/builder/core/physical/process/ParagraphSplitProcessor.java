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

package com.antgroup.openspg.builder.core.physical.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.ParagraphSplitNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.builder.model.record.StringRecord;
import com.antgroup.openspg.common.util.Md5Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

public class ParagraphSplitProcessor extends BasePythonProcessor<ParagraphSplitNodeConfig> {

  private ExecuteNode node;

  public ParagraphSplitProcessor(String id, String name, ParagraphSplitNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    this.node = context.getExecuteNodes().get(getId());
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    node.setStatus(StatusEnum.RUNNING);
    node.addTraceLog("Start split document...");
    List<BaseRecord> results = new ArrayList<>();
    for (BaseRecord record : inputs) {
      StringRecord stringRecord = (StringRecord) record;

      String fileUrl = stringRecord.getDocument();
      List<ChunkRecord.Chunk> chunks;
      String token = config.getToken();
      if (StringUtils.isNotBlank(token)) {
        chunks = readYuque(fileUrl, token);
      } else {
        chunks = readFile(fileUrl);
      }

      node.addTraceLog("invoke split operator:%s", config.getOperatorConfig().getClassName());
      for (ChunkRecord.Chunk chunk : chunks) {
        node.addTraceLog("invoke split chunk:%s", chunk.getName());
        Map map = new ObjectMapper().convertValue(chunk, Map.class);
        List<Object> result =
            (List<Object>) operatorFactory.invoke(config.getOperatorConfig(), map);
        List<ChunkRecord.Chunk> chunkList =
            JSON.parseObject(
                JSON.toJSONString(result), new TypeReference<List<ChunkRecord.Chunk>>() {});
        for (ChunkRecord.Chunk splitChunk : chunkList) {
          ChunkRecord chunkRecord = new ChunkRecord(splitChunk);
          results.add(chunkRecord);
        }
        node.addTraceLog(
            "invoke split chunk:%s size:%s succeed", chunk.getName(), chunkList.size());
      }
      node.addTraceLog(
          "invoke split operator:%s succeed", config.getOperatorConfig().getClassName());
    }
    node.addTraceLog("Split document complete. number of paragraphs:%s", results.size());
    node.setStatus(StatusEnum.FINISH);
    return results;
  }

  public List<ChunkRecord.Chunk> readYuque(String url, String token) {
    PythonInterpreterConfig.PythonInterpreterConfigBuilder builder =
        PythonInterpreterConfig.newBuilder();
    builder.setPythonExec(context.getPythonExec());
    builder.addPythonPaths(context.getPythonPaths());
    PythonInterpreter pythonInterpreter = new PythonInterpreter(builder.build());
    try {
      if (StringUtils.isNotBlank(context.getPythonKnextPath())) {
        pythonInterpreter.exec(
            String.format("import sys; sys.path.append(\"%s\")", context.getPythonKnextPath()));
      }
      String className = "YuqueReader";
      node.addTraceLog("invoke chunk operator:%s", className);
      pythonInterpreter.exec("from kag.builder.component.reader import " + className);
      String pythonObject = "pyo" + "_" + Md5Utils.md5Of(UUID.randomUUID().toString());
      pythonInterpreter.exec(
          String.format(
              "%s=%s(**{'token' : '%s','project_id' : '%s'})",
              pythonObject, className, token, context.getProjectId()));
      List<Object> result =
          (List<Object>) pythonInterpreter.invokeMethod(pythonObject, "_handle", url);
      List<ChunkRecord.Chunk> chunkList =
          JSON.parseObject(
              JSON.toJSONString(result), new TypeReference<List<ChunkRecord.Chunk>>() {});
      node.addTraceLog("invoke chunk operator:%s chunks:%s succeed", className, chunkList.size());
      return chunkList;
    } finally {
      pythonInterpreter.close();
    }
  }

  public List<ChunkRecord.Chunk> readFile(String fileUrl) {
    PythonInterpreterConfig.PythonInterpreterConfigBuilder builder =
        PythonInterpreterConfig.newBuilder();
    builder.setPythonExec(context.getPythonExec());
    builder.addPythonPaths(context.getPythonPaths());
    PythonInterpreter pythonInterpreter = new PythonInterpreter(builder.build());
    try {
      if (StringUtils.isNotBlank(context.getPythonKnextPath())) {
        pythonInterpreter.exec(
            String.format("import sys; sys.path.append(\"%s\")", context.getPythonKnextPath()));
      }
      String extension = FilenameUtils.getExtension(fileUrl).toLowerCase();
      String className = "TXTReader";
      switch (extension) {
        case "csv":
          className = "CSVReader";
          break;
        case "pdf":
          className = "PDFReader";
          break;
        case "md":
          className = "MarkDownReader";
          break;
        case "json":
          className = "JSONReader";
          break;
        case "doc":
        case "docx":
          className = "DocxReader";
          break;
      }
      node.addTraceLog("invoke chunk operator:%s", className);
      pythonInterpreter.exec("from kag.builder.component.reader import " + className);
      String pythonObject = "pyo" + "_" + Md5Utils.md5Of(UUID.randomUUID().toString());
      pythonInterpreter.exec(
          String.format(
              "%s=%s(**{'project_id' : '%s'})", pythonObject, className, context.getProjectId()));
      List<Object> result =
          (List<Object>) pythonInterpreter.invokeMethod(pythonObject, "_handle", fileUrl);
      List<ChunkRecord.Chunk> chunkList =
          JSON.parseObject(
              JSON.toJSONString(result), new TypeReference<List<ChunkRecord.Chunk>>() {});
      node.addTraceLog("invoke chunk operator:%s chunks:%s succeed", className, chunkList.size());
      return chunkList;
    } finally {
      pythonInterpreter.close();
    }
  }
}
