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

import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model.EsMapping;
import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Put;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestResponse;
import java.util.Map;

@Address(scheme = SCHEME_VAR, host = HOST_VAR, port = PORT_VAR)
public interface ElasticSearchSchemaClient {

  @Put(value = "/{idxName}")
  ForestResponse<String> createIdx(
      @Var("idxName") String idxName, @JSONBody("mappings") EsMapping mapping);

  @Put(value = "/{idxName}/_mapping")
  ForestResponse<String> addNewFieldsIntoIdx(
      @Var("idxName") String idxName,
      @JSONBody("properties") Map<String, EsMapping.PropertyConfig> properties);

  @Delete(value = "/{idxName}")
  ForestResponse<String> deleteIdx(@Var("idxName") String idxName);

  @Get(value = "/_mapping")
  ForestResponse<String> queryAllIdxMappings();
}
