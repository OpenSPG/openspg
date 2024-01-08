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

package com.antgroup.openspg.builder.core.strategy.linking.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.PropertyLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.BaseQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.MatchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.OperatorType;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.QueryGroup;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class SearchBasedLinking implements PropertyLinking {

  private SearchEngineClient searchEngineClient;

  @Override
  public void init(BuilderContext context) throws BuilderException {
    searchEngineClient = SearchEngineClientDriverManager.getClient(context.getSearchEngineUrl());
  }

  @Override
  public void linking(BasePropertyRecord record) throws LinkingException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();

    List<String> rawValues = record.getRawValues();
    List<String> ids = new ArrayList<>(rawValues.size());
    for (String rawValue : rawValues) {
      if (!objectTypeRef.isEntityType() && !objectTypeRef.isConceptType()) {
        ids.add(rawValue);
      } else {
        String searchResult = null;
        try {
          searchResult = search(objectTypeRef, rawValue);
        } catch (Exception e) {
          throw new LinkingException(e, "{} normalize error", rawValue);
        }
        ids.add(searchResult);
      }
    }
    record.getValue().setStrStds(ids);
    record.getValue().setIds(ids);
  }

  private String search(SPGTypeRef objectTypeRef, String rawValue) {
    // build search request
    SearchRequest request = new SearchRequest();
    request.setIndexName(
        searchEngineClient.getIdxNameConvertor().convertIdxName(objectTypeRef.getName()));

    List<BaseQuery> queries = new ArrayList<>(4);
    queries.add(new MatchQuery("id", rawValue));
    queries.add(new MatchQuery("name", rawValue));
    if (objectTypeRef.isConceptType()) {
      queries.add(new MatchQuery("alias", rawValue));
      queries.add(new MatchQuery("stdId", rawValue));
    }
    QueryGroup queryGroup = new QueryGroup(queries, OperatorType.OR);
    request.setQuery(queryGroup);
    request.setSize(50);

    // search and parse response
    List<IdxRecord> idxRecords = searchEngineClient.search(request);
    if (CollectionUtils.isEmpty(idxRecords)) {
      throw new LinkingException("property={} normalize failed", rawValue);
    }
    IdxRecord idxRecord = idxRecords.get(0);
    if (!rawValue.equals(idxRecord.getDocId()) && idxRecord.getScore() < 0.5) {
      throw new LinkingException("property={} normalize failed", rawValue);
    }
    return idxRecord.getDocId();
  }
}
