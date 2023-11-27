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

package com.antgroup.openspg.server.api.http.client;

import com.antgroup.openspg.api.facade.ApiResponse;
import com.antgroup.openspg.api.facade.client.SchemaFacade;
import com.antgroup.openspg.api.facade.dto.schema.request.BuiltInPropertyRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.RelationRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.api.http.client.forest.ForestUtils;
import com.antgroup.openspg.api.http.client.forest.client.SchemaForestClient;
import com.antgroup.openspg.api.http.client.util.SchemaCache;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema;
import java.util.List;

public class HttpSchemaFacade implements SchemaFacade {

  private final SchemaCache schemaCache;

  public HttpSchemaFacade() {
    this(false);
  }

  public HttpSchemaFacade(boolean enableCache) {
    this.schemaCache = enableCache ? new SchemaCache() : null;
  }

  @Override
  public ApiResponse<Boolean> alterSchema(SchemaAlterRequest request) {
    return ForestUtils.call(SchemaForestClient.class, c -> c.alterSchema(request));
  }

  @Override
  public ApiResponse<ProjectSchema> queryProjectSchema(ProjectSchemaRequest request) {
    if (schemaCache != null) {
      return schemaCache.queryProjectSchema(
          request.toString(),
          () -> ForestUtils.call(SchemaForestClient.class, c -> c.queryProjectSchema(request)));
    }

    return ForestUtils.call(SchemaForestClient.class, c -> c.queryProjectSchema(request));
  }

  @Override
  public ApiResponse<BaseSPGType> querySPGType(SPGTypeRequest request) {
    if (schemaCache != null) {
      return schemaCache.querySPGType(
          request.toString(),
          () -> ForestUtils.call(SchemaForestClient.class, c -> c.querySpgType(request)));
    }

    return ForestUtils.call(SchemaForestClient.class, c -> c.querySpgType(request));
  }

  @Override
  public ApiResponse<Relation> queryRelation(RelationRequest request) {
    if (schemaCache != null) {
      return schemaCache.queryRelation(
          request.toString(),
          () -> ForestUtils.call(SchemaForestClient.class, c -> c.queryRelation(request)));
    }

    return ForestUtils.call(SchemaForestClient.class, c -> c.queryRelation(request));
  }

  @Override
  public ApiResponse<List<Property>> queryBuiltInProperty(BuiltInPropertyRequest request) {
    return ForestUtils.call(SchemaForestClient.class, c -> c.queryBuiltInProperty(request));
  }
}
