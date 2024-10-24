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
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.*;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.TextSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.VectorSearchRequest;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.service.SearchManager;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchManagerImpl implements SearchManager {

  @Autowired private ProjectManager projectManager;

  @Override
  public List<IdxRecord> spgTypeSearch(SPGTypeSearchRequest request) {
    String searchEngineUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    IdxDataQueryService searchEngineClient = getSearchEngineClient(searchEngineUrl);
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

  @Override
  public List<IdxRecord> textSearch(TextSearchRequest request) {
    String searchEngineUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    IdxDataQueryService searchEngineClient = getSearchEngineClient(searchEngineUrl);
    String queryString = request.getQueryString();
    if (queryString == null) queryString = StringUtils.EMPTY;
    List<String> labelConstraints = null;
    if (request.getLabelConstraints() != null && !request.getLabelConstraints().isEmpty()) {
      String[] labels = new String[request.getLabelConstraints().size()];
      labelConstraints = Arrays.asList(request.getLabelConstraints().toArray(labels));
    }
    int topk = -1;
    if (request.getTopk() != null && request.getTopk() > 0) topk = request.getTopk();
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(new FullTextSearchQuery(queryString, labelConstraints));
    searchRequest.setSize(topk);
    return searchEngineClient.search(searchRequest);
  }

  @Override
  public List<IdxRecord> vectorSearch(VectorSearchRequest request) {
    String searchEngineUrl = projectManager.getGraphStoreUrl(request.getProjectId());
    IdxDataQueryService searchEngineClient = getSearchEngineClient(searchEngineUrl);
    String label = request.getLabel();
    if (label == null) label = StringUtils.EMPTY;
    String propertyKey = request.getPropertyKey();
    if (propertyKey == null) propertyKey = StringUtils.EMPTY;
    float[] queryVector = request.getQueryVector();
    if (queryVector == null) queryVector = ArrayUtils.EMPTY_FLOAT_ARRAY;
    int efSearch = -1;
    if (request.getEfSearch() != null && request.getEfSearch() > 0)
      efSearch = request.getEfSearch();
    int topk = -1;
    if (request.getTopk() != null && request.getTopk() > 0) topk = request.getTopk();
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(new VectorSearchQuery(label, propertyKey, queryVector, efSearch));
    searchRequest.setSize(topk);
    return searchEngineClient.search(searchRequest);
  }

  private IdxDataQueryService getSearchEngineClient(String searchEngineUrl) {
    return SearchEngineClientDriverManager.getClient(searchEngineUrl);
  }
}
