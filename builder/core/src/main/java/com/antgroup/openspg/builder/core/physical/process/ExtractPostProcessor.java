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
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.ExtractPostProcessorNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.common.constants.BuilderConstant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractPostProcessor extends BasePythonProcessor<ExtractPostProcessorNodeConfig> {

  private ExecuteNode node = new ExecuteNode();

  public ExtractPostProcessor(String id, String name, ExtractPostProcessorNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    if (context.getExecuteNodes() != null) {
      this.node = context.getExecuteNodes().get(getId());
    }
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    node.setStatus(StatusEnum.RUNNING);
    JSONObject pyConfig = new JSONObject();
    pyConfig.put(BuilderConstant.TYPE, BuilderConstant.BASE);
    node.addTraceLog("Start alignment...");
    List<BaseRecord> results = new ArrayList<>();

    for (BaseRecord record : inputs) {
      SubGraphRecord spgRecord = (SubGraphRecord) record;
      Map map = mapper.convertValue(spgRecord, Map.class);
      node.addTraceLog("invoke alignment operator:%s", config.getOperatorConfig().getClassName());
      List<Object> result =
          (List<Object>)
              operatorFactory.invoke(
                  config.getOperatorConfig(),
                  BuilderConstant.POSTPROCESSOR_ABC,
                  pyConfig.toJSONString(),
                  map);
      node.addTraceLog(
          "invoke alignment operator:%s succeed", config.getOperatorConfig().getClassName());
      List<SubGraphRecord> records =
          JSON.parseObject(JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});
      for (SubGraphRecord subGraph : records) {
        node.addTraceLog(
            "alignment succeed node:%s edge:%s",
            subGraph.getResultNodes().size(), subGraph.getResultEdges().size());
        results.add(subGraph);
      }
    }
    node.addTraceLog("alignment complete...");
    node.setStatus(StatusEnum.FINISH);
    return results;
  }
}
