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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.builder.core.physical.utils.CommonUtils;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.ParagraphSplitNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.builder.model.record.StringRecord;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.project.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParagraphSplitProcessor extends BasePythonProcessor<ParagraphSplitNodeConfig> {

  private ExecuteNode node = new ExecuteNode();
  private Project project;

  public ParagraphSplitProcessor(String id, String name, ParagraphSplitNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    if (context.getExecuteNodes() != null) {
      this.node = context.getExecuteNodes().get(getId());
    }
    project = JSON.parseObject(context.getProject(), Project.class);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    node.setStatus(StatusEnum.RUNNING);
    node.addTraceLog("Start split document...");
    List<BaseRecord> results = new ArrayList<>();
    JSONObject pyConfig = new JSONObject();
    BuilderJob job = config.getJob();
    JSONObject extension = JSON.parseObject(job.getExtension());
    CommonUtils.getSplitterConfig(
        pyConfig,
        context.getPythonExec(),
        context.getPythonPaths(),
        context.getPythonEnv(),
        context.getSchemaUrl(),
        project,
        extension);
    for (BaseRecord record : inputs) {
      StringRecord stringRecord = (StringRecord) record;

      String fileUrl = stringRecord.getDocument();
      node.addTraceLog("invoke split fileUrl:%s", fileUrl);
      List<ChunkRecord.Chunk> chunks = readSource(job);
      node.addTraceLog("invoke split operator:%s", config.getOperatorConfig().getClassName());
      for (ChunkRecord.Chunk chunk : chunks) {
        node.addTraceLog("invoke split chunk:%s", chunk.getName());
        Map map = new ObjectMapper().convertValue(chunk, Map.class);
        List<Object> result =
            (List<Object>)
                operatorFactory.invoke(
                    config.getOperatorConfig(),
                    BuilderConstant.SPLITTER_ABC,
                    pyConfig.toJSONString(),
                    map);
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

  public List<ChunkRecord.Chunk> readSource(BuilderJob job) {
    node.addTraceLog("invoke read operator:%s", PythonInvokeMethod.BRIDGE_READER.getMethod());
    List<ChunkRecord.Chunk> chunkList =
        CommonUtils.readSource(
            context.getPythonExec(),
            context.getPythonPaths(),
            context.getPythonEnv(),
            context.getSchemaUrl(),
            project,
            job,
            null);
    node.addTraceLog(
        "invoke read operator:%s chunks:%s succeed",
        PythonInvokeMethod.BRIDGE_READER.getMethod(), chunkList.size());
    return chunkList;
  }
}
