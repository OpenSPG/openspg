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

package com.antgroup.openspg.core.spgschema.service.type;

import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema;
import java.util.List;
import java.util.Set;

/**
 * The interface of spg type that provides create、update、delete、query method to manager the spg
 * types, as spg type contains properties and relations, it will alter its properties and relations
 * at same time when altering spg type. Also, the interface provides multi methods to query spg
 * types, such as query by unique name、id, or query the whole types in a project.
 */
public interface SPGTypeService {

  /**
   * Create a new advanced type.
   *
   * @param advancedType the advanced type config to be created
   * @return the record count to be created.
   */
  int create(BaseAdvancedType advancedType);

  /**
   * Update the type config, such as CRUD its property or relation, forbid to update the name of
   * type.
   *
   * @param advancedType the new config of type to be updated
   * @return the record count to be updated
   */
  int update(BaseAdvancedType advancedType);

  /**
   * Delete the advanced type, include its properties and relations.
   *
   * @param advancedType the advanced type to be deleted.
   * @return the record count to be deleted
   */
  int delete(BaseAdvancedType advancedType);

  /**
   * Query the spg schema of project, include all types in project.
   *
   * @param projectId the unique id of project
   * @return the schema of project
   */
  ProjectSchema queryProjectSchema(Long projectId);

  /**
   * Query spg type by identity, it will return null if the type named not exists.
   *
   * @param spgTypeIdentifier spg type identity
   * @return spg type detail
   */
  BaseSPGType querySPGTypeByIdentifier(SPGTypeIdentifier spgTypeIdentifier);

  /**
   * Query spg type by unique id, it will return null of the type by id not exists.
   *
   * @param uniqueIds list of unique id
   * @return list of spg type detail
   */
  List<BaseSPGType> querySPGTypeById(List<Long> uniqueIds);

  /**
   * Query name of standard type that is spreadable, such as STD.ChinaMobile, STD.IdentifyCard
   *
   * @return list of unique name
   */
  Set<SPGTypeIdentifier> querySpreadStdTypeName();
}
