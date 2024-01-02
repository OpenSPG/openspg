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
import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class ElasticSearchEngineClient extends BaseIdxSearchEngineClient {

  private final ElasticSearchSchemaClient elasticSearchIdxClient;
  private final ElasticSearchRecordClient elasticSearchDocClient;

  @Getter private final IdxNameConvertor idxNameConvertor;

  @Getter private final String connUrl;

  public ElasticSearchEngineClient(String connUrl, IdxNameConvertor idxNameConvertor) {
    this.idxNameConvertor = idxNameConvertor;

    this.connUrl = connUrl;
    initElasticSearchEngine(connUrl);

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
    List<IdxRecord> idxRecords =
        ElasticSearchRecordUtils.mGetIdxRecords(
            query.getIdxName(), query.getDocIds(), elasticSearchDocClient);
    idxRecords.forEach(
        idxRecord -> idxRecord.setIdxName(idxNameConvertor.restoreIdxName(idxRecord.getIdxName())));
    return idxRecords;
  }

  @Override
  public List<IdxRecord> search(SearchRequest request) {
    List<IdxRecord> idxRecords = ElasticSearchRecordUtils.search(request, elasticSearchDocClient);
    idxRecords.forEach(
        idxRecord -> idxRecord.setIdxName(idxNameConvertor.restoreIdxName(idxRecord.getIdxName())));
    return idxRecords;
  }

  @Override
  public int manipulateRecord(IdxRecordManipulateCmd cmd) {
    IdxNameUtils.convertIdxName(cmd, idxNameConvertor);

    ElasticSearchRecordUtils.upsertIdxRecords(cmd.getUpsertIdxRecords(), elasticSearchDocClient);
    ElasticSearchRecordUtils.deleteIdxRecords(cmd.getDeleteIdxRecords(), elasticSearchDocClient);
    return 0;
  }

  private void initElasticSearchEngine(String connInfo) {
    ForestConfiguration configuration = Forest.config();

    UriComponents uriComponents = UriComponentsBuilder.fromUriString(connInfo).build();

    String scheme = uriComponents.getQueryParams().getFirst("scheme");
    configuration.setVariableValue(ElasticSearchConstants.SCHEME, scheme);
    configuration.setVariableValue(ElasticSearchConstants.HOST, uriComponents.getHost());
    configuration.setVariableValue(ElasticSearchConstants.PORT, uriComponents.getPort());
    configuration.setReadTimeout(30, TimeUnit.SECONDS);
    configuration.setLogEnabled(false);
    configuration.setBackendName("httpclient");
  }

  @Override
  public void close() {}
}
