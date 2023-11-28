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

package com.antgroup.openspg.server.core.schema.service.alter.sync;

import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchEngineSyncer extends BaseSchemaSyncer {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public void syncSchema(Long projectId, SPGSchemaAlterCmd schemaEditCmd) {
    SearchEngineClient searchEngineClient = dataSourceService.buildSharedSearchEngineClient();
    if (appEnvConfig.getEnableSearchEngine() && null != searchEngineClient) {
      searchEngineClient.alterSchema(schemaEditCmd);
    }
  }
}
