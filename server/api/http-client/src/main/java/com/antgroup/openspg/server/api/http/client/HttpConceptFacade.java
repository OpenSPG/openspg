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

package com.antgroup.openspg.server.api.http.client;

import java.util.List;

import com.antgroup.openspg.core.schema.model.semantic.TripleSemantic;
import com.antgroup.openspg.core.schema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.DefineTripleSemanticRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveTripleSemanticRequest;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.client.ConceptFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.server.api.http.client.forest.ForestUtils;
import com.antgroup.openspg.server.api.http.client.forest.client.ConceptForestClient;

public class HttpConceptFacade implements ConceptFacade {

  @Override
  public ApiResponse<ConceptList> queryConcept(ConceptRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.queryConcept(request));
  }

  @Override
  public ApiResponse<List<TripleSemantic>> getReasoningConceptsDetail(SPGTypeRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.getReasoningConcept(request));
  }

  @Override
  public ApiResponse<Boolean> defineDynamicTaxonomy(DefineDynamicTaxonomyRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.defineDynamicTaxonomy(request));
  }

  @Override
  public ApiResponse<Boolean> defineLogicalCausation(DefineTripleSemanticRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.defineLogicalCausation(request));
  }

  @Override
  public ApiResponse<Boolean> removeDynamicTaxonomy(RemoveDynamicTaxonomyRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.removeDynamicTaxonomy(request));
  }

  @Override
  public ApiResponse<Boolean> removeLogicalCausation(RemoveTripleSemanticRequest request) {
    return ForestUtils.call(ConceptForestClient.class, c -> c.removeLogicalCausation(request));
  }
}
