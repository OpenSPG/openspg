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

package com.antgroup.openspg.schema.http.client;

import com.antgroup.openspg.common.model.api.ApiResponse;
import com.antgroup.openspg.schema.http.client.request.ConceptRequest;
import com.antgroup.openspg.schema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.schema.model.semantic.request.DefineLogicalCausationRequest;
import com.antgroup.openspg.schema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.schema.model.semantic.request.RemoveLogicalCausationRequest;
import com.antgroup.openspg.schema.model.type.ConceptList;

/**
 * The interface to query concepts that defined under a concept type, also provides method to define
 * and remove dynamic taxonomy and logical causation of the concept.
 */
public interface ConceptFacade {

  /**
   * Query concept details by the concept type and concept name, if the {@code
   * ConceptRequest#conceptName} parameter is null, it will return all the concepts defined under
   * the concept type. The concept returned not only contains basic information of the concept, but
   * also contains the semantic defined on the concept.
   *
   * @param request The request which contains unique name of the concept type.
   * @return The list of concepts
   */
  ApiResponse<ConceptList> queryConcept(ConceptRequest request);

  /**
   * Define dynamic taxonomy rule between an entity and a concept, Currently the taxonomic predicate
   * is belongTo.
   *
   * @param request Request
   * @return true or false
   */
  ApiResponse<Boolean> defineDynamicTaxonomy(DefineDynamicTaxonomyRequest request);

  /**
   * Define logical causation rule between two concepts.
   *
   * @param request The Request to save concept relation
   * @return true or false
   */
  ApiResponse<Boolean> defineLogicalCausation(DefineLogicalCausationRequest request);

  /**
   * Remove dynamic taxonomy rule of concept.
   *
   * @param request The request to delete "belongTo" concept
   * @return true or false
   */
  ApiResponse<Boolean> removeDynamicTaxonomy(RemoveDynamicTaxonomyRequest request);

  /**
   * Remove logical causation rule between concepts.
   *
   * @param request The request to delete concept relation
   * @return true or false
   */
  ApiResponse<Boolean> removeLogicalCausation(RemoveLogicalCausationRequest request);
}
