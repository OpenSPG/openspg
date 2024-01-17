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

package com.antgroup.openspg.server.core.schema.service.type.repository;

import com.antgroup.openspg.server.core.schema.service.type.model.SimpleSPGType;
import java.util.List;

/**
 * The read-write interface for spg type, provides methods for adding, modifying, deleting, and
 * querying spg types.
 */
public interface SPGTypeRepository {

  /**
   * Add a spg type to the database.
   *
   * @param advancedType the spg type to add
   * @return record count
   */
  int save(SimpleSPGType advancedType);

  /**
   * Update spg type information by unique id.
   *
   * @param advancedType the spg type to update
   * @return record count
   */
  int update(SimpleSPGType advancedType);

  /**
   * Delete the spg type in database which unique id is match.
   *
   * @param advancedType the spg type to delete
   * @return record count
   */
  int delete(SimpleSPGType advancedType);

  /**
   * Query all spg type defined in the project.
   *
   * @param projectId unique id of project
   * @return list of spg type
   */
  List<SimpleSPGType> queryByProject(Long projectId);

  /**
   * Query all BasicType in the database.
   *
   * @return list of BasicType
   */
  List<SimpleSPGType> queryAllBasicType();

  /**
   * Query all StandardType in the database.
   *
   * @return list of StandardType
   */
  List<SimpleSPGType> queryAllStandardType();

  /**
   * Batch query spg type by the unique id.
   *
   * @param uniqueIds list of unique id
   * @return list of spg type
   */
  List<SimpleSPGType> queryByUniqueId(List<Long> uniqueIds);

  /**
   * Query spg type by the unique name
   *
   * @param uniqueName unique name of spg type
   * @return spg type object
   */
  SimpleSPGType queryByName(String uniqueName);
}
