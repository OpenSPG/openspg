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

import com.antgroup.openspg.api.facade.ApiConstants;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.client.ElasticSearchRecordClient;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.client.ElasticSearchSchemaClient;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.util.ElasticSearchRecordUtils;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.util.ElasticSearchSchemaUtils;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.util.IdxNameUtils;
import com.antgroup.openspg.cloudext.interfaces.searchengine.BaseIdxSearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.IdxNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxGetQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxRecordManipulateCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxSchemaAlterCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchema;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class ElasticSearchEngineClient extends BaseIdxSearchEngineClient {

    private final ElasticSearchSchemaClient elasticSearchIdxClient;
    private final ElasticSearchRecordClient elasticSearchDocClient;

    @Getter
    private final IdxNameConvertor idxNameConvertor;

    @Getter
    private final SearchEngineConnectionInfo connInfo;

    public ElasticSearchEngineClient(
        SearchEngineConnectionInfo connInfo, IdxNameConvertor idxNameConvertor) {
        this.idxNameConvertor = idxNameConvertor;

        this.connInfo = connInfo;
        initElasticSearchEngine(connInfo);

        elasticSearchIdxClient = Forest.client(ElasticSearchSchemaClient.class);
        elasticSearchDocClient = Forest.client(ElasticSearchRecordClient.class);
    }

    @Override
    public List<IdxSchema> querySchema() {
        List<IdxSchema> idxSchemas = ElasticSearchSchemaUtils.queryAllIdxSchema(elasticSearchIdxClient);

        IdxNameUtils.restoreIdxName(idxSchemas, idxNameConvertor);

        return idxSchemas;
    }

    @Override
    public int alterSchema(IdxSchemaAlterCmd cmd) {
        IdxNameUtils.convertIdxName(cmd, idxNameConvertor);

        int updated = 0;
        List<IdxSchema> deleteIdx = cmd.getDeleteIdx();
        updated += ElasticSearchSchemaUtils.deleteIdx(deleteIdx, elasticSearchIdxClient);

        List<IdxSchema> updateIdx = cmd.getUpdateIdx();
        updated += ElasticSearchSchemaUtils.addNewFieldsIntoIdx(updateIdx, elasticSearchIdxClient);

        List<IdxSchema> createIdx = cmd.getCreateIdx();
        updated += ElasticSearchSchemaUtils.createIdx(createIdx, elasticSearchIdxClient);
        return updated;
    }

    @Override
    public List<IdxRecord> mGet(IdxGetQuery query) {
        List<IdxRecord> idxRecords = ElasticSearchRecordUtils.mGetIdxRecords(
            query.getIdxName(), query.getDocIds(), elasticSearchDocClient
        );
        idxRecords.forEach(idxRecord ->
            idxRecord.setIdxName(idxNameConvertor.restoreIdxName(idxRecord.getIdxName()))
        );
        return idxRecords;
    }

    @Override
    public List<IdxRecord> search(SearchRequest request) {
        List<IdxRecord> idxRecords = ElasticSearchRecordUtils.search(request, elasticSearchDocClient);
        idxRecords.forEach(idxRecord ->
            idxRecord.setIdxName(idxNameConvertor.restoreIdxName(idxRecord.getIdxName()))
        );
        return idxRecords;
    }

    @Override
    public int manipulateRecord(IdxRecordManipulateCmd cmd) {
        IdxNameUtils.convertIdxName(cmd, idxNameConvertor);

        ElasticSearchRecordUtils.upsertIdxRecords(cmd.getUpsertIdxRecords(), elasticSearchDocClient);
        ElasticSearchRecordUtils.deleteIdxRecords(cmd.getDeleteIdxRecords(), elasticSearchDocClient);
        return 0;
    }

    private void initElasticSearchEngine(SearchEngineConnectionInfo connInfo) {
        ForestConfiguration configuration = Forest.config();

        String scheme = (String) connInfo.getNotNullParam(ApiConstants.SCHEME);
        String host = (String) connInfo.getNotNullParam(ApiConstants.HOST);
        Long port = Long.parseLong((String) connInfo.getNotNullParam(ApiConstants.PORT));

        configuration.setVariableValue(ElasticSearchConstants.SCHEME, scheme);
        configuration.setVariableValue(ElasticSearchConstants.HOST, host);
        configuration.setVariableValue(ElasticSearchConstants.PORT, port);
        configuration.setLogEnabled(false);
        configuration.setBackendName("httpclient");
    }

    @Override
    public void close() {
    }
}
