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

package com.antgroup.openspg.server.api.http.client.forest.client;

import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.dto.schema.request.BuiltInPropertyRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.RelationRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.api.http.client.util.HttpClientConstants;
import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BodyType;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.http.ForestResponse;
import java.util.List;

@BodyType(type = "json")
@Address(
    scheme = HttpClientConstants.SCHEME_VAR,
    host = HttpClientConstants.HOST_VAR,
    port = HttpClientConstants.PORT_VAR)
public interface SchemaForestClient {

  @Post(value = "/public/v1/schema/alterSchema")
  ForestResponse<Boolean> alterSchema(@JSONBody SchemaAlterRequest request);

  @Get(value = "/public/v1/schema/querySpgType")
  ForestResponse<BaseSPGType> querySpgType(@Query SPGTypeRequest request);

  @Get(value = "/public/v1/schema/queryProjectSchema")
  ForestResponse<ProjectSchema> queryProjectSchema(@Query ProjectSchemaRequest request);

  @Get(value = "/public/v1/schema/queryRelation")
  ForestResponse<Relation> queryRelation(@Query RelationRequest request);

  @Get(value = "/public/v1/schema/queryBuiltInProperty")
  ForestResponse<List<Property>> queryBuiltInProperty(@Query BuiltInPropertyRequest request);
}
