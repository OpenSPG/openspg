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
package com.antgroup.openspg.server.core.reasoner.service.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.udf.impl.UdfMngImpl;
import com.antgroup.openspg.server.common.model.reasoner.ReasonerTask;
import com.antgroup.openspg.server.common.model.reasoner.StatusEnum;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import com.antgroup.openspg.server.core.reasoner.service.CatalogService;
import com.antgroup.openspg.server.core.reasoner.service.ReasonerService;
import com.antgroup.openspg.server.core.reasoner.service.runner.ReasonerRunner;
import com.google.common.collect.Lists;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReasonerServiceImpl implements ReasonerService {
  @Autowired private AppEnvConfig appEnvConfig;
  @Autowired private CatalogService catalogService;

  @PostConstruct
  public void init() {
    // init udf path
    UdfMngImpl.getInstance(
        Lists.newArrayList(),
        Lists.newArrayList(),
        Lists.newArrayList("com.antgroup.openspg.server.core.reasoner.service.udtf"),
        null,
        null,
        null);
  }

  @Override
  public ReasonerTask runTask(ReasonerTask request) {
    long startTime = System.currentTimeMillis();
    ReasonerRunner runner = new ReasonerRunner(request, appEnvConfig.getSchemaUri());
    try {
      Catalog catalog =
          catalogService.getCatalog(request.getProjectId(), request.getGraphStoreUrl());
      log.info(
          "run task success! input="
              + JSON.toJSONString(request)
              + " cost="
              + (System.currentTimeMillis() - startTime));
      return runner.run(catalog);
    } catch (Exception e) {
      log.warn("run task failed! input=" + JSON.toJSONString(request) + " err=" + e.getMessage());
      request.setStatus(StatusEnum.ERROR);
      request.setResultMessage(e.getMessage());
    }
    return request;
  }
}
