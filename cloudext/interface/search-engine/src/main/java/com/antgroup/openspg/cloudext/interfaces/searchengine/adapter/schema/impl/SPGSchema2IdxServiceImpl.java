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

package com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.schema.impl;

import com.antgroup.openspg.cloudext.interfaces.searchengine.BaseIdxSearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.schema.SPGSchema2IdxService;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxField;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxMapping;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchema;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchemaAlterItem;
import com.antgroup.openspg.server.core.schema.model.SPGSchema;
import com.antgroup.openspg.server.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.server.core.schema.model.type.BaseSPGType;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class SPGSchema2IdxServiceImpl implements SPGSchema2IdxService {

  private final BaseIdxSearchEngineClient idxSearchEngineClient;

  public SPGSchema2IdxServiceImpl(BaseIdxSearchEngineClient idxSearchEngineClient) {
    this.idxSearchEngineClient = idxSearchEngineClient;
  }

  @Override
  public List<IdxSchemaAlterItem> generate(SPGSchema spgSchema) {
    if (CollectionUtils.isEmpty(spgSchema.getSpgTypes())) {
      return Collections.emptyList();
    }

    List<IdxSchemaAlterItem> alterItems = new ArrayList<>();
    for (BaseSPGType spgType : spgSchema.getSpgTypes()) {
      if (spgType.isBasicType()) {
        continue;
      }
      alterItems.add(generate2IdxAlterItem(spgType));
    }

    List<IdxSchema> idxSchemas = idxSearchEngineClient.querySchema();
    return checkSchemaAlterations(alterItems, idxSchemas);
  }

  private List<IdxSchemaAlterItem> checkSchemaAlterations(
      List<IdxSchemaAlterItem> expectedAlterItems, List<IdxSchema> existedIdxSchema) {
    Map<String, IdxSchema> existedIdxSchemaMap =
        existedIdxSchema.stream()
            .collect(Collectors.toMap(IdxSchema::getIdxName, Function.identity()));
    return expectedAlterItems.stream()
        .map(
            alterItem ->
                calculateActualAlteration(
                    alterItem, existedIdxSchemaMap.get(alterItem.getIdxSchema().getIdxName())))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private IdxSchemaAlterItem calculateActualAlteration(
      IdxSchemaAlterItem alterItem, IdxSchema existedSchema) {
    switch (alterItem.getAlterOp()) {
      case DELETE:
        return existedSchema == null ? null : alterItem;
      case CREATE:
        return existedSchema == null
            ? alterItem
            : buildUpdateAlterItem(
                alterItem.getIdxSchema().getIdxName(),
                calculateFieldDiff(alterItem.getIdxSchema(), existedSchema));
      case UPDATE:
        return existedSchema == null
            ? new IdxSchemaAlterItem(alterItem.getIdxSchema(), AlterOperationEnum.CREATE)
            : buildUpdateAlterItem(
                alterItem.getIdxSchema().getIdxName(),
                calculateFieldDiff(alterItem.getIdxSchema(), existedSchema));
      default:
        throw new RuntimeException(
            "unexpected alter operation when calculating actual alteration: "
                + alterItem.getAlterOp());
    }
  }

  private List<IdxField> calculateFieldDiff(IdxSchema expectedSchema, IdxSchema existedSchema) {
    Map<String, IdxField> existedFields =
        existedSchema.getIdxMapping().getIdxFields().stream()
            .collect(Collectors.toMap(IdxField::getName, Function.identity()));
    List<IdxField> expectedIdxFields =
        expectedSchema.getIdxMapping() != null
                && CollectionUtils.isNotEmpty(expectedSchema.getIdxMapping().getIdxFields())
            ? expectedSchema.getIdxMapping().getIdxFields()
            : Lists.newArrayList();
    return expectedIdxFields.stream()
        .filter(field -> !existedFields.containsKey(field.getName()))
        .collect(Collectors.toList());
  }

  private IdxSchemaAlterItem buildUpdateAlterItem(String idxName, List<IdxField> idxFields) {
    if (CollectionUtils.isEmpty(idxFields)) {
      return null;
    }
    return new IdxSchemaAlterItem(
        new IdxSchema(idxName, new IdxMapping(idxFields)), AlterOperationEnum.UPDATE);
  }

  private IdxSchemaAlterItem generate2IdxAlterItem(BaseSPGType spgType) {
    IdxSchema idxSchema = toIndexSchema(spgType);
    return new IdxSchemaAlterItem(idxSchema, spgType.getAlterOperation());
  }

  private IdxSchema toIndexSchema(BaseSPGType spgType) {
    // es支持动态schema，这里不传入mapping，由es自己判断
    return new IdxSchema(spgType.getName(), null);
  }
}
