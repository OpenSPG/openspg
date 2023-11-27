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

package com.antgroup.openspg.server.api.http.client;

import com.antgroup.openspg.api.facade.ApiResponse;
import com.antgroup.openspg.api.facade.client.ConceptFacade;
import com.antgroup.openspg.api.facade.dto.schema.request.ConceptRequest;
import com.antgroup.openspg.api.http.client.forest.ForestUtils;
import com.antgroup.openspg.api.http.client.forest.client.ConceptForestClient;
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineLogicalCausationRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveLogicalCausationRequest;
import com.antgroup.openspg.core.spgschema.model.type.ConceptList;

public class HttpConceptFacade implements ConceptFacade {

  @Override
  public ApiResponse<ConceptList> queryConcept(ConceptRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.queryConcept(request));
  }

  @Override
  public ApiResponse<Boolean> defineDynamicTaxonomy(DefineDynamicTaxonomyRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.defineDynamicTaxonomy(request));
  }

  @Override
  public ApiResponse<Boolean> defineLogicalCausation(DefineLogicalCausationRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.defineLogicalCausation(request));
  }

  @Override
  public ApiResponse<Boolean> removeDynamicTaxonomy(RemoveDynamicTaxonomyRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.removeDynamicTaxonomy(request));
  }

  @Override
  public ApiResponse<Boolean> removeLogicalCausation(RemoveLogicalCausationRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.removeLogicalCausation(request));
  }
}
