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

package com.antgroup.openspg.server.schema.core.service.type.impl;

import com.antgroup.openspg.server.schema.core.service.predicate.PropertyService;
import com.antgroup.openspg.server.schema.core.service.predicate.RelationService;
import com.antgroup.openspg.server.schema.core.service.type.SPGTypeService;
import com.antgroup.openspg.server.schema.core.service.type.convertor.SPGTypeAssemble;
import com.antgroup.openspg.server.schema.core.service.type.convertor.SPGTypeConvertor;
import com.antgroup.openspg.server.schema.core.service.type.model.SimpleSPGType;
import com.antgroup.openspg.server.schema.core.service.type.repository.SPGTypeRepository;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.core.schema.model.predicate.Property;
import com.antgroup.openspg.server.core.schema.model.predicate.Relation;
import com.antgroup.openspg.server.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.server.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.core.schema.model.type.WithAlterOperation;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SPGTypeServiceImpl implements SPGTypeService {

  @Autowired private SPGTypeRepository spgTypeRepository;
  @Autowired private PropertyService propertyService;
  @Autowired private RelationService relationService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int create(BaseAdvancedType advancedType) {
    if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
      advancedType.getProperties().forEach(property -> propertyService.create(property));
      log.info("property of schemaType: {} is created", advancedType.getName());
    }

    if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
      advancedType.getRelations().forEach(relation -> relationService.create(relation));
      log.info("relation of schemaType: {} is created", advancedType.getName());
    }

    int cnt = spgTypeRepository.save(SPGTypeConvertor.toSimpleSpgType(advancedType));
    log.info("schema type: {} is created", advancedType.getName());
    return cnt;
  }

  @Override
  public int update(BaseAdvancedType advancedType) {
    if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
      this.alterProperties(advancedType.getProperties());
      log.info("properties of schemaType: {} is updated", advancedType.getName());
    }

    if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
      this.alterRelations(advancedType.getRelations());
      log.info("relations of schemaType: {} is updated", advancedType.getName());
    }

    int cnt = spgTypeRepository.update(SPGTypeConvertor.toSimpleSpgType(advancedType));
    log.info("schema type: {} is updated", advancedType.getName());
    return cnt;
  }

  @Override
  public int delete(BaseAdvancedType advancedType) {
    if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
      advancedType.getProperties().forEach(property -> propertyService.delete(property));
      log.info("property of schemaType: {} is deleted", advancedType.getName());
    }

    if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
      advancedType.getRelations().forEach(relation -> relationService.delete(relation));
      log.info("relation of schemaType: {} is deleted", advancedType.getName());
    }

    int cnt = spgTypeRepository.delete(SPGTypeConvertor.toSimpleSpgType(advancedType));
    log.info("schema type: {} is deleted", advancedType.getName());
    return cnt;
  }

  @Override
  public ProjectSchema queryProjectSchema(Long projectId) {
    List<SimpleSPGType> basicTypes = spgTypeRepository.queryAllBasicType();
    List<BaseAdvancedType> customizedTypes = this.queryCustomizedType(projectId);

    List<BaseSPGType> spgTypes = new ArrayList<>();
    spgTypes.addAll(SPGTypeConvertor.toBaseSpgType(basicTypes));
    spgTypes.addAll(customizedTypes);

    return new ProjectSchema(spgTypes);
  }

  @Override
  public BaseSPGType querySPGTypeByIdentifier(SPGTypeIdentifier spgTypeIdentifier) {
    SimpleSPGType simpleSpgType = spgTypeRepository.queryByName(spgTypeIdentifier.toString());
    if (simpleSpgType == null) {
      return null;
    }
    if (SPGTypeEnum.BASIC_TYPE.equals(simpleSpgType.getSpgTypeEnum())) {
      return SPGTypeConvertor.toBaseSpgType(simpleSpgType);
    }

    List<BaseAdvancedType> spgTypes = this.withDetail(Lists.newArrayList(simpleSpgType));
    return spgTypes.get(0);
  }

  @Override
  public List<BaseSPGType> querySPGTypeById(List<Long> uniqueIds) {
    if (CollectionUtils.isEmpty(uniqueIds)) {
      return Collections.emptyList();
    }

    List<BaseSPGType> spgTypes = new ArrayList<>();
    List<SimpleSPGType> simpleSpgTypes = spgTypeRepository.queryByUniqueId(uniqueIds);
    List<SimpleSPGType> advancedSimpleTypes = new ArrayList<>();
    simpleSpgTypes.forEach(
        simpleSpgType -> {
          if (SPGTypeEnum.BASIC_TYPE.equals(simpleSpgType.getSpgTypeEnum())) {
            spgTypes.add(SPGTypeConvertor.toBaseSpgType(simpleSpgType));
          } else {
            advancedSimpleTypes.add(simpleSpgType);
          }
        });
    spgTypes.addAll(this.withDetail(advancedSimpleTypes));
    return spgTypes;
  }

  private List<BaseAdvancedType> queryCustomizedType(Long projectId) {
    List<SimpleSPGType> standardTypes = spgTypeRepository.queryAllStandardType();
    List<SimpleSPGType> simpleSpgTypes = spgTypeRepository.queryByProject(projectId);

    return this.withDetail(
        Streams.concat(standardTypes.stream(), simpleSpgTypes.stream())
            .collect(Collectors.toList()));
  }

  private List<BaseAdvancedType> withDetail(List<SimpleSPGType> simpleSpgTypes) {
    if (CollectionUtils.isEmpty(simpleSpgTypes)) {
      return Collections.emptyList();
    }

    Set<Long> uniqueIds = Sets.newHashSet();
    for (SimpleSPGType simpleSpgType : simpleSpgTypes) {
      if (simpleSpgType.getParentTypeInfo() != null
          && CollectionUtils.isNotEmpty(simpleSpgType.getParentTypeInfo().getInheritPath())) {
        uniqueIds.addAll(simpleSpgType.getParentTypeInfo().getInheritPath());
      } else {
        uniqueIds.add(simpleSpgType.getUniqueId());
      }
    }

    List<Property> properties = propertyService.queryBySubjectId(Lists.newArrayList(uniqueIds));
    List<Relation> relations = relationService.queryBySubjectId(Lists.newArrayList(uniqueIds));
    Set<SPGTypeIdentifier> spreadStdTypeNames = this.querySpreadStdTypeName();

    List<BaseSPGType> spgTypes =
        SPGTypeAssemble.assemble(simpleSpgTypes, properties, relations, spreadStdTypeNames);
    return spgTypes.stream().map(e -> (BaseAdvancedType) e).collect(Collectors.toList());
  }

  private void alterProperties(List<Property> propertys) {
    List<Property> deleteProperties =
        propertys.stream().filter(WithAlterOperation::isDelete).collect(Collectors.toList());
    deleteProperties.forEach(e -> propertyService.delete(e));

    List<Property> updateProperties =
        propertys.stream().filter(WithAlterOperation::isUpdate).collect(Collectors.toList());
    updateProperties.forEach(e -> propertyService.update(e));

    List<Property> createProperties =
        propertys.stream().filter(WithAlterOperation::isCreate).collect(Collectors.toList());
    createProperties.forEach(e -> propertyService.create(e));
  }

  private void alterRelations(List<Relation> relations) {
    List<Relation> deleteRelations =
        relations.stream().filter(WithAlterOperation::isDelete).collect(Collectors.toList());
    deleteRelations.forEach(e -> relationService.delete(e));

    List<Relation> updateRelations =
        relations.stream().filter(WithAlterOperation::isUpdate).collect(Collectors.toList());
    updateRelations.forEach(e -> relationService.update(e));

    List<Relation> createRelations =
        relations.stream().filter(WithAlterOperation::isCreate).collect(Collectors.toList());
    createRelations.forEach(e -> relationService.create(e));
  }

  @Override
  public Set<SPGTypeIdentifier> querySpreadStdTypeName() {
    List<SimpleSPGType> standardTypes = spgTypeRepository.queryAllStandardType();
    return standardTypes.stream()
        .filter(e -> Boolean.TRUE.equals(e.getSpreadable()))
        .map(SimpleSPGType::getSpgTypeIdentifier)
        .collect(Collectors.toSet());
  }
}
