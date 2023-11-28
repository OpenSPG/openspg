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

package com.antgroup.openspg.server.core.schema.service.predicate.repository;

import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.schema.model.predicate.PropertyRef;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
import java.util.List;

/**
 * The read-write interface for property or relation in the database, provides methods for saving,
 * updating, deleting, and querying properties and relations.
 */
public interface PropertyRepository {

  /**
   * Save a new property record into db
   *
   * @param simpleProperty property detail.
   * @return record count that added
   */
  int save(SimpleProperty simpleProperty);

  /**
   * Update property detail in db.
   *
   * @param simpleProperty property detail.
   * @return record count that updated
   */
  int update(SimpleProperty simpleProperty);

  /**
   * Delete a property in db.
   *
   * @param simpleProperty property detail.
   * @return record count that deleted
   */
  int delete(SimpleProperty simpleProperty);

  /**
   * Query properties or relations by list of spg type id.
   *
   * @param subjectIds list of spg type id
   * @param ontologyEnum
   * @return list of simple property object.
   */
  List<SimpleProperty> queryBySubjectId(List<Long> subjectIds, SPGOntologyEnum ontologyEnum);

  /**
   * Batch query properties or relations by unique id.
   *
   * @param uniqueIds list of unique id
   * @param ontologyEnum ontology type
   * @return list of simple property object
   */
  List<SimpleProperty> queryByUniqueId(List<Long> uniqueIds, SPGOntologyEnum ontologyEnum);

  /**
   * Query property by unique id
   *
   * @param uniqueId unique id
   * @param ontologyEnum ontology type
   * @return property
   */
  SimpleProperty queryByUniqueId(Long uniqueId, SPGOntologyEnum ontologyEnum);

  /**
   * Query unique id by property name and object type name.
   *
   * @param objectTypeName unique name of taxonomic concept type
   * @param predicateName property name
   * @return unique id of belongTo relation
   */
  Long queryUniqueIdByPO(String predicateName, String objectTypeName);

  /**
   * Query property type ref by unique id.
   *
   * @param uniqueIds list of unique id
   * @param ontologyEnum ontology type
   * @return list of property type ref
   */
  List<PropertyRef> queryRefByUniqueId(List<Long> uniqueIds, SPGOntologyEnum ontologyEnum);
}
