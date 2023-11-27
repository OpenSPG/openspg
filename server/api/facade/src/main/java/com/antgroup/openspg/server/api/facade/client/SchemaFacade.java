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

package com.antgroup.openspg.server.api.facade.client;

import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.dto.schema.request.BuiltInPropertyRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.RelationRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.core.schema.model.predicate.Property;
import com.antgroup.openspg.server.core.schema.model.predicate.Relation;
import com.antgroup.openspg.server.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.core.schema.model.type.ProjectSchema;
import java.util.List;

/**
 * The interface to alter or query schema, which provides methods for creating and modifying the
 * schema, querying the details of the schema in the project.
 */
public interface SchemaFacade {

  /**
   * Define and change the schema of the project, such as adding an entity type, deleting a concept
   * type, adding a property to an event type, or deleting a relation type. A single modification
   * can include multiple operations, and the server ensures atomic submission of the changes.
   *
   * @param request The Request for alter the schema
   * @return alter result
   */
  ApiResponse<Boolean> alterSchema(SchemaAlterRequest request);

  /**
   * Query the whole schema detail defined in the project by project id, it will return list of the
   * spg types with its own properties and relations.
   *
   * @param request The query request which contains a project id
   * @return schema detail of project
   */
  ApiResponse<ProjectSchema> queryProjectSchema(ProjectSchemaRequest request);

  /**
   * Querying the details of a SPG type, such as Entity type, Event. The input parameter is the
   * unique name of the type, and the output is the details of the type. If the SPG type does not
   * exist, null is returned.
   *
   * @param request The query request which contains a unique name of the SPG type
   * @return The SPG type details
   */
  ApiResponse<BaseSPGType> querySPGType(SPGTypeRequest request);

  /**
   * Querying the details of a relation type. The input parameter is the SPO triple of the relation,
   * and the output is the details of the relation. If the relation type does not exist, null is
   * returned.
   *
   * @param request The query request which contains a SPO triple name
   * @return The relation detail
   */
  ApiResponse<Relation> queryRelation(RelationRequest request);

  /**
   * Query built-in properties of a specific SPG type.
   *
   * @param request The query request
   * @return The list of properties
   */
  ApiResponse<List<Property>> queryBuiltInProperty(BuiltInPropertyRequest request);
}
