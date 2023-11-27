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

package com.antgroup.openspg.server.api.http.client.forest.client;

import com.antgroup.openspg.api.facade.dto.schema.request.ConceptRequest;
import com.antgroup.openspg.api.http.client.util.HttpClientConstants;
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineLogicalCausationRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveLogicalCausationRequest;
import com.antgroup.openspg.core.spgschema.model.type.ConceptList;
import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BodyType;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.http.ForestResponse;

@BodyType(type = "json")
@Address(
    scheme = HttpClientConstants.SCHEME_VAR,
    host = HttpClientConstants.HOST_VAR,
    port = HttpClientConstants.PORT_VAR)
public interface ConceptForestClient {

  @Get(value = "/public/v1/concept/queryConcept")
  ForestResponse<ConceptList> queryConcept(@Query ConceptRequest request);

  @Post(value = "/public/v1/concept/defineDynamicTaxonomy")
  ForestResponse<Boolean> defineDynamicTaxonomy(@JSONBody DefineDynamicTaxonomyRequest request);

  @Post(value = "/public/v1/concept/defineLogicalCausation")
  ForestResponse<Boolean> defineLogicalCausation(@JSONBody DefineLogicalCausationRequest request);

  @Post(value = "/public/v1/concept/removeDynamicTaxonomy")
  ForestResponse<Boolean> removeDynamicTaxonomy(@JSONBody RemoveDynamicTaxonomyRequest request);

  @Post(value = "/public/v1/concept/removeLogicalCausation")
  ForestResponse<Boolean> removeLogicalCausation(@JSONBody RemoveLogicalCausationRequest request);
}
