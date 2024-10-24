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
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.ExtractPostProcessorNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractPostProcessor extends BasePythonProcessor<ExtractPostProcessorNodeConfig> {

  private ExecuteNode node;

  public ExtractPostProcessor(String id, String name, ExtractPostProcessorNodeConfig config) {
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
    node.addTraceLog("Start post processor...");
    List<BaseRecord> results = new ArrayList<>();

    List<Map> lists = Lists.newArrayList();
    for (BaseRecord record : inputs) {
      SubGraphRecord spgRecord = (SubGraphRecord) record;
      lists.add(mapper.convertValue(spgRecord, Map.class));
    }

    node.addTraceLog(
        "invoke post processor operator:%s", config.getOperatorConfig().getClassName());
    Object result = operatorFactory.invoke(config.getOperatorConfig(), lists);
    node.addTraceLog(
        "invoke post processor operator:%s succeed", config.getOperatorConfig().getClassName());
    SubGraphRecord subGraph = JSON.parseObject(JSON.toJSONString(result), SubGraphRecord.class);
    node.addTraceLog(
        "post processor succeed node:%s edge%s",
        subGraph.getResultNodes().size(), subGraph.getResultEdges().size());

    /*ProjectSchema projectSchema = CommonUtils.getProjectSchema(context);
    List<BaseSPGRecord> nodes = CommonUtils.convertNodes(subGraph, projectSchema);
    List<BaseSPGRecord> edges = CommonUtils.convertEdges(subGraph, projectSchema);
    results.addAll(nodes);
    results.addAll(edges);*/
    results.add(subGraph);
    node.addTraceLog("post processor complete...");
    node.setOutputs(subGraph);
    node.setStatus(StatusEnum.FINISH);
    return results;
  }
}
