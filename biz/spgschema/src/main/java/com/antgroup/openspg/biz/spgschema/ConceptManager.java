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

package com.antgroup.openspg.biz.spgschema;

import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineLogicalCausationRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveLogicalCausationRequest;
import com.antgroup.openspg.core.spgschema.model.type.ConceptList;

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
  void defineLogicalCausation(DefineLogicalCausationRequest request);

  /**
   * remove logical causation.
   *
   * @param request
   */
  void removeLogicalCausation(RemoveLogicalCausationRequest request);

  /**
   * Get concept detail by concept type.
   *
   * @param conceptTypeName unique name of concept type
   * @param conceptName unique name of concept
   * @return list of concept
   */
  ConceptList getConceptDetail(String conceptTypeName, String conceptName);
}
