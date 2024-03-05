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
