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

package com.antgroup.openspg.server.biz.schema;

import java.util.List;

import com.antgroup.openspg.core.schema.model.semantic.TripleSemantic;
import com.antgroup.openspg.core.schema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.DefineTripleSemanticRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveTripleSemanticRequest;
import com.antgroup.openspg.core.schema.model.type.ConceptList;

/** Provide method to manage concept */
public interface ConceptManager {

  /**
   * define dynamic taxonomy.
   *
   * @param request request
   */
  void defineDynamicTaxonomy(DefineDynamicTaxonomyRequest request);

  /**
   * remove dynamic taxonomy.
   *
   * @param request request
   */
  void removeDynamicTaxonomy(RemoveDynamicTaxonomyRequest request);

  /**
   * define logical causation.
   *
   * @param request request
   */
  void defineLogicalCausation(DefineTripleSemanticRequest request);

  /**
   * remove logical causation.
   *
   * @param request
   */
  void removeLogicalCausation(RemoveTripleSemanticRequest request);

  /**
   * Get concept detail by concept type.
   *
   * @param conceptTypeName unique name of concept type
   * @param conceptName unique name of concept
   * @return list of concept
   */
  ConceptList getConceptDetail(String conceptTypeName, String conceptName);

  /**
   * Get reasoning concepts detail.
   * @param conceptTypeNames unique names of concept types
   * @return list of triple semantic
   */
  List<TripleSemantic> getReasoningConceptsDetail(List<String> conceptTypeNames);
}
