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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch;

import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriver;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.impl.DefaultIdxNameConvertor;
import com.antgroup.openspg.server.common.model.datasource.connection.SearchEngineConnectionInfo;

public class ElasticSearchEngineClientDriver implements SearchEngineClientDriver {

  static {
    SearchEngineClientDriverManager.registerDriver(new ElasticSearchEngineClientDriver());
  }

  @Override
  public String driverScheme() {
    return "elasticsearch";
  }

  @Override
  public SearchEngineClient connect(SearchEngineConnectionInfo connInfo) {
    return new ElasticSearchEngineClient(connInfo, new DefaultIdxNameConvertor());
  }
}
