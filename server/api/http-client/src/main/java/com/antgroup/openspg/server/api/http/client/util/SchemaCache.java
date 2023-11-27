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

package com.antgroup.openspg.server.api.http.client.util;

import com.antgroup.openspg.api.facade.ApiResponse;
import com.antgroup.openspg.core.spgschema.model.SchemaException;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ConceptList;
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class SchemaCache {

  private final Cache<String, ApiResponse<ProjectSchema>> SPG_PROJECT_SCHEMA_CACHE;
  private final Cache<String, ApiResponse<BaseSPGType>> SPG_TYPE_CACHE;
  private final Cache<String, ApiResponse<Relation>> SPG_RELATION_CACHE;
  private final Cache<String, ApiResponse<ConceptList>> SPG_CONCEPT_CACHE;

  public SchemaCache() {
    SPG_TYPE_CACHE =
        CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(3, TimeUnit.MINUTES).build();
    SPG_RELATION_CACHE =
        CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(3, TimeUnit.MINUTES).build();
    SPG_CONCEPT_CACHE =
        CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(3, TimeUnit.MINUTES).build();
    SPG_PROJECT_SCHEMA_CACHE =
        CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(3, TimeUnit.MINUTES).build();
  }

  public ApiResponse<ProjectSchema> queryProjectSchema(
      String key, Supplier<ApiResponse<ProjectSchema>> supplier) {
    return cacheOrCall(SPG_PROJECT_SCHEMA_CACHE, key, supplier);
  }

  public ApiResponse<BaseSPGType> querySPGType(
      String key, Supplier<ApiResponse<BaseSPGType>> supplier) {
    return cacheOrCall(SPG_TYPE_CACHE, key, supplier);
  }

  public ApiResponse<Relation> queryRelation(String key, Supplier<ApiResponse<Relation>> supplier) {
    return cacheOrCall(SPG_RELATION_CACHE, key, supplier);
  }

  public ApiResponse<ConceptList> queryConcept(
      String key, Supplier<ApiResponse<ConceptList>> supplier) {
    return cacheOrCall(SPG_CONCEPT_CACHE, key, supplier);
  }

  private <T> ApiResponse<T> cacheOrCall(
      Cache<String, ApiResponse<T>> cache, String key, Supplier<ApiResponse<T>> supplier) {
    try {
      return cache.get(key, supplier::get);
    } catch (ExecutionException e) {
      throw SchemaException.queryError(e);
    }
  }
}
