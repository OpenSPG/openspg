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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.util;

import com.antgroup.openspg.api.facade.JSON;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.client.ElasticSearchRecordClient;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model.EsDocs;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model.EsHits;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.BaseQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.MatchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.QueryGroup;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.TermQuery;
import com.antgroup.openspg.common.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.http.ForestResponse;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_QUERY_BOOL_CLAUSE_KEY;
import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_QUERY_MATCH_CLAUSE_KEY;
import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_QUERY_MUST_CLAUSE_KEY;
import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_QUERY_NOT_CLAUSE_KEY;
import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_QUERY_SHOULD_CLAUSE_KEY;
import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_QUERY_TERM_CLAUSE_KEY;
import static com.antgroup.openspg.cloudext.interfaces.searchengine.constants.ElasticSearchConstants.ES_RESULT_HITS_KEY;


public class ElasticSearchRecordUtils {

    public static void upsertIdxRecords(
        Map<String, List<IdxRecord>> idxRecordMap, ElasticSearchRecordClient client) {
        for (Map.Entry<String, List<IdxRecord>> entry : idxRecordMap.entrySet()) {
            String idxName = entry.getKey();
            List<IdxRecord> idxRecords = entry.getValue();
            for (IdxRecord idxRecord : idxRecords) {
                if (StringUtils.isBlank(idxRecord.getDocId())) {
                    throw new IllegalArgumentException("docId is null for idxRecord");
                }
            }
            for (IdxRecord idxRecord : idxRecords) {
                ForestResponse<String> upsert = client.upsert(
                    idxName, idxRecord.getDocId(), idxRecord.getFields());
                if (!upsert.isSuccess()) {
                    throw new RuntimeException("upsertIdxRecords error, errorMsg=" + upsert.getContent());
                }
            }
        }
    }

    public static void deleteIdxRecords(
        Map<String, List<IdxRecord>> idxRecordMap, ElasticSearchRecordClient client) {
        for (Map.Entry<String, List<IdxRecord>> entry : idxRecordMap.entrySet()) {
            String idxName = entry.getKey();
            List<IdxRecord> idxRecords = entry.getValue();
            for (IdxRecord idxRecord : idxRecords) {
                if (StringUtils.isBlank(idxRecord.getDocId())) {
                    throw new IllegalArgumentException("docId is null for idxRecord");
                }
            }
            for (IdxRecord idxRecord : idxRecords) {
                ForestResponse<String> delete = client.delete(idxName, idxRecord.getDocId());
                if (!delete.isSuccess()) {
                    throw new RuntimeException("deleteIdxRecords error, errorMsg=" + delete.getContent());
                }
            }
        }
    }

    public static List<IdxRecord> mGetIdxRecords(
        String idxName, Set<String> docIds, ElasticSearchRecordClient client) {
        ForestResponse<String> response = client.mGet(idxName, docIds);
        if (!response.isSuccess()) {
            throw new RuntimeException("mGetIdxRecords error, errorMsg=" + response.getContent());
        }

        EsDocs esDocs = JSON.deserialize(response.getContent(), new TypeToken<EsDocs>() {
        }.getType());
        return esDocs.toIdxRecord();
    }

    public static List<IdxRecord> search(
        SearchRequest request, ElasticSearchRecordClient client) {
        if (request == null) {
            return Collections.emptyList();
        }
        Object query = convertQueryToObject(request.getQuery());
        if (request.getQuery() instanceof QueryGroup) {
            // If it is a composite query, you need to prefix the query string with the "bool" keyword.
            Map<String, Object> boolQuery = new HashMap<>();
            boolQuery.put(ES_QUERY_BOOL_CLAUSE_KEY, query);
            query = boolQuery;
        }

        ForestResponse<String> response = client.search(request.getIndexName(), query);
        if (!response.isSuccess()) {
            throw new RuntimeException("search error, errorMsg=" + response.getContent());
        }

        JSONObject content = JSONObject.parseObject(response.getContent());
        String hits = content.getString(ES_RESULT_HITS_KEY);
        if (StringUtils.isBlank(hits)) {
            return Collections.emptyList();
        }
        EsHits esHits = JSON.deserialize(hits, new TypeToken<EsHits>() {
        }.getType());
        return esHits.toIdxRecord();
    }

    public static Object convertQueryToObject(BaseQuery baseQuery) {
        JSONObject resultObj = new JSONObject();
        if (baseQuery instanceof MatchQuery) {
            MatchQuery matchQuery = (MatchQuery) baseQuery;
            Map<String, Object> query = new HashMap<>();
            query.put(matchQuery.getName(), matchQuery.getValue());
            resultObj.put(ES_QUERY_MATCH_CLAUSE_KEY, query);
            return resultObj;
        }

        if (baseQuery instanceof TermQuery) {
            TermQuery termQuery = (TermQuery) baseQuery;
            Map<String, Object> query = new HashMap<>();
            query.put(termQuery.getName(), termQuery.getValue());
            resultObj.put(ES_QUERY_TERM_CLAUSE_KEY, query);
            return resultObj;
        }

        if (baseQuery instanceof QueryGroup) {
            QueryGroup queryGroup = (QueryGroup) baseQuery;
            List<Object> objects = new ArrayList<>();
            for (BaseQuery query : queryGroup.getQueries()) {
                Object o = convertQueryToObject(query);
                objects.add(o);
            }
            switch (queryGroup.getOperator()) {
                case AND:
                    resultObj.put(ES_QUERY_MUST_CLAUSE_KEY, objects);
                    break;
                case NOT:
                    resultObj.put(ES_QUERY_NOT_CLAUSE_KEY, objects);
                    break;
                default:
                    resultObj.put(ES_QUERY_SHOULD_CLAUSE_KEY, objects);
                    break;
            }
        }
        return resultObj;
    }
}
