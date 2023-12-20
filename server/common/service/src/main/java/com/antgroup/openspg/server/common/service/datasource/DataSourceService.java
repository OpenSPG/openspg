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

package com.antgroup.openspg.server.common.service.datasource;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsageTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.connection.*;

public interface DataSourceService {

  /**
   * When a resource is configured to a project with ID -1, the resource is shared by all projects
   */
  long SHARED_PROJECT_ID = -1L;

  DataSource getFirstDataSource(Long projectId, DataSourceUsageTypeEnum dataSourceUsageType);

  default GraphStoreClient buildSharedKgStoreClient() {
    DataSource graphStore = getFirstDataSource(SHARED_PROJECT_ID, DataSourceUsageTypeEnum.KG_STORE);
    return GraphStoreClientDriverManager.getClient(
        (GraphStoreConnectionInfo) graphStore.getConnectionInfo());
  }

  default SearchEngineClient buildSharedSearchEngineClient() {
    DataSource searchEngine = getFirstDataSource(SHARED_PROJECT_ID, DataSourceUsageTypeEnum.SEARCH);
    return SearchEngineClientDriverManager.getClient(
        (SearchEngineConnectionInfo) searchEngine.getConnectionInfo());
  }
}
