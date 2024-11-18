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

package com.antgroup.openspg.server.core.schema.service.semantic.repository;

import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.semantic.TripleSemantic;
import com.antgroup.openspg.server.core.schema.service.semantic.model.SimpleSemantic;
import com.antgroup.openspg.server.core.schema.service.semantic.model.TripleSemanticQuery;
import java.util.List;

/**
 * The read-writer interface for predicate semantic, provides save、update、delete and query method.
 */
public interface SemanticRepository {

  /**
   * Save or update semantic record, should be unique by spo triple
   *
   * @param semantic semantic record
   * @return record count
   */
  int saveOrUpdate(SimpleSemantic semantic);

  /**
   * Delete semantic record by spo triple.
   *
   * @param subjectId the unique id of subject
   * @param predicateName predicate name
   * @param objectId the unique id of object
   * @param ontologyEnum ontology type
   * @return record count
   */
  int deleteBySpo(
      String subjectId, String predicateName, String objectId, SPGOntologyEnum ontologyEnum);

  /**
   * Delete semantic record by predicate and object
   *
   * @param predicateName predicate name
   * @param objectType object type
   * @param objectId object id
   * @param ontologyEnum ontology type
   * @return record count
   */
  int deleteByObject(
      String predicateName, String objectType, String objectId, SPGOntologyEnum ontologyEnum);

  /**
   * Delete concept relation semantic by condition.
   *
   * @param conceptSemantic relation semantic of concept
   * @return record count
   */
  int deleteConceptSemantic(TripleSemantic conceptSemantic);

  /**
   * Query concept semantic record by condition.
   *
   * @param query query condition
   * @return list of semantic record
   */
  List<SimpleSemantic> queryConceptSemanticByCond(TripleSemanticQuery query);

  /**
   * Query semantic record by subject.
   *
   * @param subjectIds list of subject id
   * @param ontologyEnum ontology type
   * @return list of semantic record
   */
  List<SimpleSemantic> queryBySubjectId(List<String> subjectIds, SPGOntologyEnum ontologyEnum);
}
