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

package com.antgroup.openspg.server.schema.core.service.predicate.repository;

import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.predicate.model.SimpleSubProperty;
import java.util.List;

/**
 * The read-write interface for sub property objects in the database, provides methods for saving,
 * updating, deleting, and querying sub properties.
 */
public interface SubPropertyRepository {

  /**
   * Save a new sub property to db.
   *
   * @param simpleSubProperty sub property detail
   * @return record count that added
   */
  int save(SimpleSubProperty simpleSubProperty);

  /**
   * Update sub property information in db.
   *
   * @param simpleSubProperty sub property detail
   * @return record count that updated
   */
  int update(SimpleSubProperty simpleSubProperty);

  /**
   * Delete a sub property in db.
   *
   * @param simpleSubProperty sub property detail
   * @return record count that deleted
   */
  int delete(SimpleSubProperty simpleSubProperty);

  /**
   * Query sub property by unique id
   *
   * @param uniqueId unique id of sub property
   * @return sub property
   */
  SimpleSubProperty queryByUniqueId(Long uniqueId);

  /**
   * Query sub properties in db by property or relation id.
   *
   * @param subjectIds list of property or relation id
   * @param ontologyEnum ontology type
   * @return list of sub property
   */
  List<SimpleSubProperty> queryBySubjectId(List<Long> subjectIds, SPGOntologyEnum ontologyEnum);
}
