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

package com.antgroup.openspg.server.biz.service.impl;

import com.antgroup.openspg.cloudext.interfaces.searchengine.IdxDataQueryService;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.MatchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.OperatorType;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.QueryGroup;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSearchRequest;
import com.antgroup.openspg.server.biz.service.SearchManager;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchManagerImpl implements SearchManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public List<IdxRecord> spgTypeSearch(SPGTypeSearchRequest request) {
    IdxDataQueryService searchEngineClient = getSearchEngineClient();
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(
        new QueryGroup(
            Lists.newArrayList(
                new MatchQuery("id", request.getKeyword()),
                new MatchQuery("name", request.getKeyword())),
            OperatorType.OR));
    searchRequest.setFrom(request.getPageIdx() * request.getPageSize());
    searchRequest.setSize(request.getPageSize());
    return searchEngineClient.search(searchRequest);
  }

  private IdxDataQueryService getSearchEngineClient() {
    return SearchEngineClientDriverManager.getClient(appEnvConfig.getSearchEngineUrl());
  }
}
