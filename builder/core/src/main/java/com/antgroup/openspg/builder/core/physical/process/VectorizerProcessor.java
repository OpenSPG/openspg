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
import com.antgroup.openspg.builder.model.pipeline.config.predicting.VectorizerProcessorNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VectorizerProcessor extends BasePythonProcessor<VectorizerProcessorNodeConfig> {

  private ExecuteNode node;

  public VectorizerProcessor(String id, String name, VectorizerProcessorNodeConfig config) {
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
    node.addTraceLog("Start vectorizer processor...");
    List<BaseRecord> results = new ArrayList<>();
    SubGraphRecord subGraph = new SubGraphRecord(Lists.newArrayList(), Lists.newArrayList());
    SubGraphRecord outputs = new SubGraphRecord(Lists.newArrayList(), Lists.newArrayList());

    for (BaseRecord record : inputs) {
      SubGraphRecord spgRecord = (SubGraphRecord) record;
      outputs.getResultNodes().addAll(spgRecord.getResultNodes());
      outputs.getResultEdges().addAll(spgRecord.getResultEdges());
      Map map = mapper.convertValue(spgRecord, Map.class);
      node.addTraceLog(
          "invoke vectorizer processor operator:%s", config.getOperatorConfig().getClassName());
      List<Object> result = (List<Object>) operatorFactory.invoke(config.getOperatorConfig(), map);
      node.addTraceLog(
          "invoke vectorizer processor operator:%s succeed",
          config.getOperatorConfig().getClassName());
      List<SubGraphRecord> records =
          JSON.parseObject(JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});
      for (SubGraphRecord subGraphRecord : records) {
        node.addTraceLog(
            "vectorizer processor succeed node:%s edge%s",
            subGraphRecord.getResultNodes().size(), subGraphRecord.getResultEdges().size());
        subGraph.getResultNodes().addAll(subGraphRecord.getResultNodes());
        subGraph.getResultEdges().addAll(subGraphRecord.getResultEdges());
      }
    }
    results.add(subGraph);
    node.addTraceLog("post vectorizer complete...");
    node.setOutputs(outputs);
    node.setStatus(StatusEnum.FINISH);
    return results;
  }
}
