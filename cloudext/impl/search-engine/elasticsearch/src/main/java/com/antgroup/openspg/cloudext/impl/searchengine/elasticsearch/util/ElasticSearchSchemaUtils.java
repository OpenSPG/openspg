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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.client.ElasticSearchSchemaClient;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model.EsIdxDigest;
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model.EsMapping;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxField;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxMapping;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchema;
import com.antgroup.openspg.server.core.schema.model.type.BasicTypeEnum;
import com.dtflys.forest.http.ForestResponse;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;

public class ElasticSearchSchemaUtils {

  /** Reason phrase when idx is not found for the elastic search */
  private static final String REASON_PHRASE_IDX_NOT_FOUND = "Not Found";

  public static int createIdx(List<IdxSchema> idxSchemas, ElasticSearchSchemaClient client) {
    int updated = 0;
    for (IdxSchema idxSchema : idxSchemas) {
      EsMapping mapping = EsMapping.fromIdxMapping(idxSchema.getIdxMapping());
      ForestResponse<String> response = client.createIdx(idxSchema.getIdxName(), mapping);
      if (!response.isSuccess()) {
        throw new RuntimeException("createIdx error, and errorMsg=" + response.getContent());
      }
      updated += 1;
    }
    return updated;
  }

  public static int addNewFieldsIntoIdx(
      List<IdxSchema> idxSchemas, ElasticSearchSchemaClient client) {
    int updated = 0;
    for (IdxSchema idxSchema : idxSchemas) {
      EsMapping mapping = EsMapping.fromIdxMapping(idxSchema.getIdxMapping());
      ForestResponse<String> response =
          client.addNewFieldsIntoIdx(idxSchema.getIdxName(), mapping.getProperties());
      if (!response.isSuccess()) {
        throw new RuntimeException(
            "addNewFieldsIntoIdx error, and errorMsg=" + response.getContent());
      }
      updated += 1;
    }
    return updated;
  }

  public static int deleteIdx(List<IdxSchema> idxSchemas, ElasticSearchSchemaClient client) {
    int deleted = 0;
    for (IdxSchema idxSchema : idxSchemas) {
      ForestResponse<String> response = client.deleteIdx(idxSchema.getIdxName());
      if (!response.isSuccess()) {
        // If idx is not existed, ignore this alteration
        if (REASON_PHRASE_IDX_NOT_FOUND.equals(response.getReasonPhrase())) {
          continue;
        }
        throw new RuntimeException("deleteIdx error, and errorMsg=" + response.getContent());
      }
      deleted += 1;
    }
    return deleted;
  }

  public static List<IdxSchema> queryAllIdxSchema(ElasticSearchSchemaClient client) {
    ForestResponse<String> response = client.queryAllIdxMappings();
    if (!response.isSuccess()) {
      throw new RuntimeException("queryIdx error, and errorMsg=" + response.getContent());
    }
    Map<String, EsIdxDigest> esIdxDigest =
        JSON.parseObject(response.getResult(), new TypeReference<Map<String, EsIdxDigest>>() {});
    return convert2IdxSchema(esIdxDigest);
  }

  private static List<IdxSchema> convert2IdxSchema(Map<String, EsIdxDigest> esIdxDigest) {
    if (MapUtils.isEmpty(esIdxDigest)) {
      return Lists.newArrayList();
    }
    return esIdxDigest.entrySet().stream()
        .map(
            entry ->
                new IdxSchema(entry.getKey(), convert2IdxMapping(entry.getValue().getMappings())))
        .collect(Collectors.toList());
  }

  private static IdxMapping convert2IdxMapping(EsMapping esMapping) {
    if (esMapping == null || MapUtils.isEmpty(esMapping.getProperties())) {
      return new IdxMapping(Lists.newArrayList());
    }
    List<IdxField> idxFields =
        esMapping.getProperties().entrySet().stream()
            .map(
                entry ->
                    new IdxField(entry.getKey(), BasicTypeEnum.from(entry.getValue().getType())))
            .collect(Collectors.toList());
    return new IdxMapping(idxFields);
  }
}
