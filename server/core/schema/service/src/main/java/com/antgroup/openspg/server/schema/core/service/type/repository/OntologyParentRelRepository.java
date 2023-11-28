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

package com.antgroup.openspg.server.schema.core.service.type.repository;

import com.antgroup.openspg.schema.model.type.ParentTypeInfo;
import java.util.List;

/**
 * The read-write interface for the inheritance relationship between spg type and parent type,
 * provides methods for adding, deleting, and querying spg type inheritance management.
 */
public interface OntologyParentRelRepository {

  /**
   * Save inheritance relationship of a spg type into database
   *
   * @param inheritInfo inheritance relationship
   * @return record count
   */
  int save(ParentTypeInfo inheritInfo);

  /**
   * Delete inheritance relationship of a spg type in database
   *
   * @param uniqueId unique id of a spg type
   * @return record count
   */
  int delete(Long uniqueId);

  /**
   * Query inheritance relationship of a spg type
   *
   * @param uniqueId unique id of a spg type
   * @return inheritance relationship
   */
  ParentTypeInfo query(Long uniqueId);

  /**
   * Batch query inheritance relationship of spg types
   *
   * @param uniqueIds list of unique id
   * @return list of inheritance relationship
   */
  List<ParentTypeInfo> query(List<Long> uniqueIds);
}
