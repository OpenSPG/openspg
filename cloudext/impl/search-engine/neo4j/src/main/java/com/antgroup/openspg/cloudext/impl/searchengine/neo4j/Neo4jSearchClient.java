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

package com.antgroup.openspg.cloudext.impl.searchengine.neo4j;

import com.antgroup.openspg.cloudext.interfaces.searchengine.BaseIdxSearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.IdxNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxGetQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxRecordManipulateCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxSchemaAlterCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchema;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.BaseQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.FullTextSearchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.VectorSearchQuery;
import com.antgroup.openspg.common.util.neo4j.Neo4jCommonUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jDriverManager;
import com.antgroup.openspg.common.util.neo4j.Neo4jIndexUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class Neo4jSearchClient extends BaseIdxSearchEngineClient {

  private final Neo4jIndexUtils client;

  @Getter private final String connUrl;
  @Getter private final String database;

  public Neo4jSearchClient(String connUrl) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(connUrl).build();
    this.connUrl = connUrl;
    this.database = uriComponents.getQueryParams().getFirst(Neo4jCommonUtils.DATABASE);
    this.client = initNeo4jIndexClient(uriComponents);
  }

  private Neo4jIndexUtils initNeo4jIndexClient(UriComponents uriComponents) {
    String host =
        String.format(
            "%s://%s:%s",
            uriComponents.getScheme(), uriComponents.getHost(), uriComponents.getPort());
    String user = uriComponents.getQueryParams().getFirst(Neo4jCommonUtils.USER);
    String password = uriComponents.getQueryParams().getFirst(Neo4jCommonUtils.PASSWORD);
    Driver driver = Neo4jDriverManager.getNeo4jDriver(host, user, password);
    return new Neo4jIndexUtils(driver, this.database);
  }

  @Override
  public List<IdxSchema> querySchema() {
    throw new RuntimeException("Neo4jSearchClient does not support querySchema.");
  }

  @Override
  public int alterSchema(IdxSchemaAlterCmd cmd) {
    throw new RuntimeException("Neo4jSearchClient does not support alterSchema.");
  }

  @Override
  public int manipulateRecord(IdxRecordManipulateCmd cmd) {
    throw new RuntimeException("Neo4jSearchClient does not support manipulateRecord.");
  }

  @Override
  public void close() throws Exception {}

  @Override
  public IdxNameConvertor getIdxNameConvertor() {
    throw new RuntimeException("Neo4jSearchClient does not support getIdxNameConvertor.");
  }

  @Override
  public List<IdxRecord> mGet(IdxGetQuery query) {
    throw new RuntimeException("Neo4jSearchClient does not support mGet.");
  }

  private List<Record> doNeo4jSearch(SearchRequest request) {
    int topk = request.getSize();
    String indexName = request.getIndexName();
    BaseQuery query = request.getQuery();
    if (query instanceof FullTextSearchQuery) {
      FullTextSearchQuery q = (FullTextSearchQuery) query;
      List<String> labelConstraints = q.getLabelConstraints();
      return client.textSearch(q.getQueryString(), labelConstraints, topk, indexName);
    } else if (query instanceof VectorSearchQuery) {
      VectorSearchQuery q = (VectorSearchQuery) query;
      return client.vectorSearch(
          q.getLabel(), q.getPropertyKey(), q.getQueryVector(), topk, indexName, q.getEfSearch());
    } else {
      throw new RuntimeException(
          "Neo4jSearchClient only supports FullTextSearchQuery and VectorSearchQuery.");
    }
  }

  @Override
  public List<IdxRecord> search(SearchRequest request) {
    List<Record> records = doNeo4jSearch(request);
    List<IdxRecord> results = new ArrayList<>();
    for (Record r : records) {
      Node node = r.get("node").asNode();
      String docId = node.get(Neo4jCommonUtils.ID).asString();
      double score = r.get("score").asDouble();
      Map<String, Object> fields = new HashMap<>(node.asMap());
      ArrayList<String> labels = new ArrayList<>();
      for (String label : node.labels())
        if (!Neo4jCommonUtils.GENERIC_ENTITY_LABEL.equals(label)) labels.add(label);
      String[] labelArray = new String[labels.size()];
      fields.put(Neo4jCommonUtils.LABELS_KEY, labels.toArray(labelArray));
      IdxRecord item = new IdxRecord(null, docId, score, fields);
      results.add(item);
    }
    return results;
  }
}
