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

package com.antgroup.openspg.server.schema.core.service.predicate;

import com.antgroup.openspg.schema.model.predicate.Property;
import java.util.List;

/** Property domain service, provide method to save/update/query/delete property. */
public interface PropertyService {

  /**
   * Create a new property.
   *
   * @param property property type detail
   * @return record count
   */
  int create(Property property);

  /**
   * Update property type detail.
   *
   * @param property property type detail
   * @return record count
   */
  int update(Property property);

  /**
   * Delete a property type.
   *
   * @param property property type detail
   * @return record count
   */
  int delete(Property property);

  /**
   * Query property type by spg type id.
   *
   * @param subjectIds list of spg type id
   * @return list of property type
   */
  List<Property> queryBySubjectId(List<Long> subjectIds);
}
