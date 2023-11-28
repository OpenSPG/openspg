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

import com.antgroup.openspg.schema.model.predicate.Relation;
import java.util.List;

/**
 * The domain method of the relational model provides methods for adding, modifying, deleting, and
 * querying relations.
 */
public interface RelationService {

  /**
   * Create a new relation.
   *
   * @param relation relation type detail.
   * @return record count
   */
  int create(Relation relation);

  /**
   * Update relation type.
   *
   * @param relation relation type detail
   * @return record count
   */
  int update(Relation relation);

  /**
   * Delete a relation type.
   *
   * @param relation relation type detail
   * @return record count
   */
  int delete(Relation relation);

  /**
   * Query relation type by spg type id.
   *
   * @param subjectIds list of spg type id
   * @return list of relation type
   */
  List<Relation> queryBySubjectId(List<Long> subjectIds);
}
