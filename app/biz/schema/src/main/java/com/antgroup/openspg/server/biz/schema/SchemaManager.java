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

package com.antgroup.openspg.server.biz.schema;

import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import java.util.List;

/** Provide methods to manager project's schema information. */
public interface SchemaManager {

  /**
   * Alter schema directly.
   *
   * @param request commit and deploy draft command
   */
  void alterSchema(SchemaAlterRequest request);

  /**
   * Get schema type of project
   *
   * @param projectId project id
   * @return schema types in project
   */
  ProjectSchema getProjectSchema(Long projectId);

  /**
   * Get schema type by unique name.
   *
   * @param uniqueName unique name
   * @return
   */
  BaseSPGType getSpgType(String uniqueName);

  /**
   * Query spg type by unique id, it will return null of the type by id not exists.
   *
   * @param uniqueIds list of unique id
   * @return list of spg type detail
   */
  List<BaseSPGType> querySPGTypeById(List<Long> uniqueIds);

  /**
   * Query relation type by spg unique id.
   *
   * @param uniqueIds list of spg unique id
   * @return list of relation type
   */
  List<Relation> queryRelationByUniqueId(List<Long> uniqueIds);

  /**
   * Query relation type by spg unique id.
   *
   * @param uniqueIds list of spg unique id
   * @param ontologyEnum
   * @return list of relation type
   */
  List<SimpleProperty> queryPropertyByUniqueId(List<Long> uniqueIds, SPGOntologyEnum ontologyEnum);

  /**
   * Get built-in properties of a kind of spg type.
   *
   * @param spgTypeEnum a kind of spg type
   * @return list of properties
   */
  List<Property> getBuiltInProperty(SPGTypeEnum spgTypeEnum);
}
