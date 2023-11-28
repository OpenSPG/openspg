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

package com.antgroup.openspg.schema.http.client.impl;

import com.antgroup.openspg.common.model.api.ApiResponse;
import com.antgroup.openspg.schema.http.client.ForestUtils;
import com.antgroup.openspg.schema.http.client.SchemaCache;
import com.antgroup.openspg.schema.http.client.SchemaFacade;
import com.antgroup.openspg.schema.http.client.client.SchemaForestClient;
import com.antgroup.openspg.schema.http.client.request.BuiltInPropertyRequest;
import com.antgroup.openspg.schema.http.client.request.ProjectSchemaRequest;
import com.antgroup.openspg.schema.http.client.request.RelationRequest;
import com.antgroup.openspg.schema.http.client.request.SPGTypeRequest;
import com.antgroup.openspg.schema.http.client.request.SchemaAlterRequest;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.type.BaseSPGType;
import com.antgroup.openspg.schema.model.type.ProjectSchema;
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
