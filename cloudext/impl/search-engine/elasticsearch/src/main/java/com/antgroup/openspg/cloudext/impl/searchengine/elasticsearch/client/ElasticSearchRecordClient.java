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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.client;

import static com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchConstants.HOST_VAR;
import static com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchConstants.PORT_VAR;
import static com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchConstants.SCHEME_VAR;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestResponse;
import java.util.Map;
import java.util.Set;

@Address(scheme = SCHEME_VAR, host = HOST_VAR, port = PORT_VAR)
public interface ElasticSearchRecordClient {

  @Post(value = "/{idxName}/_doc/{docId}")
  ForestResponse<String> upsert(
      @Var("idxName") String idxName,
      @Var("docId") String docId,
      @JSONBody Map<String, Object> fields);

  @Delete(value = "/{idxName}/_doc/{docId}")
  ForestResponse<String> delete(@Var("idxName") String idxName, @Var("docId") String docId);

  @Get(value = "/{idxName}/_mget")
  ForestResponse<String> mGet(
      @Var("idxName") String idxName, @JSONBody(name = "ids") Set<String> docIds);

  @Get(value = "/{idxName}/_search")
  ForestResponse<String> search(
      @Var("idxName") String idxName, @JSONBody(name = "query") Object query);

  @Get(value = "/_search?from={from}&size={size}")
  ForestResponse<String> searchAll(
      @JSONBody(name = "query") Object query, @Var("from") Integer from, @Var("size") Integer size);
}
