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

package com.antgroup.openspg.server.core.schema.service.predicate;

import com.antgroup.openspg.schema.model.predicate.SubProperty;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
import java.util.List;

/** Sub property domain method，provide save, update, delete, query method. */
public interface SubPropertyService {

  /**
   * Create a new sub property.
   *
   * @param subProperty sub property detail
   * @return record count
   */
  int create(SubProperty subProperty);

  /**
   * Update sub property information.
   *
   * @param subProperty sub property detail
   * @return record count
   */
  int update(SubProperty subProperty);

  /**
   * Delete a sub property.
   *
   * @param subProperty sub property detail
   * @return record count
   */
  int delete(SubProperty subProperty);

  /**
   * Query sub property by property or relation id.
   *
   * @param subjectIds list of property or relation id
   * @param ontologyEnum ontology type
   * @return list of sub property
   */
  List<SubProperty> queryBySubjectId(List<Long> subjectIds, SPGOntologyEnum ontologyEnum);
}
