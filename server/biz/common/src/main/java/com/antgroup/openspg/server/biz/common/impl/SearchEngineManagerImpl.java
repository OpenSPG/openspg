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

package com.antgroup.openspg.server.biz.common.impl;

import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.server.api.facade.dto.common.request.SearchEngineIndexRequest;
import com.antgroup.openspg.server.api.facade.dto.common.response.SearchEngineIndexResponse;
import com.antgroup.openspg.server.biz.common.SearchEngineManager;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchEngineManagerImpl implements SearchEngineManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public SearchEngineIndexResponse queryIndex(SearchEngineIndexRequest request) {
    SearchEngineClient searchEngineClient =
        SearchEngineClientDriverManager.getClient(appEnvConfig.getSearchEngineUrl());

    String convertedIndexName =
        searchEngineClient.getIdxNameConvertor().convertIdxName(request.getSpgType());
    return new SearchEngineIndexResponse().setIndexName(convertedIndexName);
  }
}
