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

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClient;
import com.antgroup.openspg.schema.model.SPGSchemaAlterCmd;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GraphStorageSyncer extends BaseSchemaSyncer {

  @Override
  public void syncSchema(Long projectId, SPGSchemaAlterCmd schemaEditCmd) {
    GraphStoreClient graphStoreClient = dataSourceService.buildSharedKgStoreClient();
    if (null != graphStoreClient) {
      graphStoreClient.alterSchema(schemaEditCmd);
    }
  }
}
