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

package com.antgroup.openspg.server.core.reasoner.service.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.kg.reasoner.thinker.Thinker;
import com.antgroup.kg.reasoner.thinker.catalog.LogicCatalog;
import com.antgroup.kg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.kg.reasoner.thinker.logic.Result;
import com.antgroup.kg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.kg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.runner.local.thinker.LocalThinkerMain;
import com.antgroup.openspg.reasoner.runner.local.thinker.OpenSPGLogicCatalog;
import com.antgroup.openspg.reasoner.runner.local.thinker.ThinkerParams;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThinkerRunner {
  private final ThinkerParams task;
  private final String graphStateClass =
      "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState";

  public ThinkerRunner(ThinkerParams task) {
    this.task = task;
  }

  public List<Result> run() {
    LogicCatalog logicCatalog = new OpenSPGLogicCatalog(task.getProjectId(), task.getConnInfo());
    logicCatalog.init();
    Thinker thinker =
        new DefaultThinker(
            LocalThinkerMain.loadGraph(graphStateClass, task.getGraphStateInitString()),
            logicCatalog);
    List<Result> result;
    Map<String, Object> params = new HashMap<>();
    for (Map.Entry<String, Object> entry : task.getParams().entrySet()) {
      if (entry.getKey().equals("entities")) {
        List<Map<String, String>> entities =
            JSON.parseObject(
                String.valueOf(entry.getValue()),
                new TypeReference<List<Map<String, String>>>() {});
        for (Map<String, String> map : entities) {
          Entity entity = new Entity(map.get("id"), map.get("type"));
          params.put(entity.toString(), entity);
        }
      } else {
        params.put(entry.getKey(), entry.getValue());
      }
    }
    if (task.getMode().toLowerCase().equals("spo")) {
      result =
          thinker.find(
              task.getTriple().getSubject(),
              task.getTriple().getPredicate(),
              task.getTriple().getObject(),
              params);
    } else {
      result = thinker.find((Node) task.getTriple().getObject(), params);
    }
    return result;
  }
}
