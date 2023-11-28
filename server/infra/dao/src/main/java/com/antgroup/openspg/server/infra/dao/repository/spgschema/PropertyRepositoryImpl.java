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

package com.antgroup.openspg.server.infra.dao.repository.spgschema;

import com.antgroup.openspg.server.infra.dao.dataobject.OntologyPropertyDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyPropertyDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.OntologyPropertyDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor.SimplePropertyConvertor;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.enums.MapTypeEnum;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.enums.PropertyCategoryEnum;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.core.schema.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.server.core.schema.service.type.model.SimpleSPGType;
import com.antgroup.openspg.server.core.schema.service.type.repository.SPGTypeRepository;
import com.antgroup.openspg.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.schema.model.predicate.PropertyRef;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PropertyRepositoryImpl implements PropertyRepository {

  @Autowired private SPGTypeRepository spgTypeRepository;
  @Autowired private OntologyPropertyDOMapper ontologyPropertyDOMapper;

  @Override
  public int save(SimpleProperty simpleProperty) {
    OntologyPropertyDO propertyDO = SimplePropertyConvertor.toNewDO(simpleProperty);
    return ontologyPropertyDOMapper.insert(propertyDO);
  }

  @Override
  public int update(SimpleProperty simpleProperty) {
    OntologyPropertyDO propertyDO = SimplePropertyConvertor.toUpdateDO(simpleProperty);
    return ontologyPropertyDOMapper.updateByPrimaryKeySelective(propertyDO);
  }

  @Override
  public int delete(SimpleProperty simpleProperty) {
    OntologyPropertyDOExample example = new OntologyPropertyDOExample();
    example.createCriteria().andOriginalIdEqualTo(simpleProperty.getUniqueId());
    return ontologyPropertyDOMapper.deleteByExample(example);
  }

  @Override
  public List<SimpleProperty> queryBySubjectId(
      List<Long> subjectIds, SPGOntologyEnum ontologyEnum) {
    OntologyPropertyDOExample example = new OntologyPropertyDOExample();
    example
        .createCriteria()
        .andOriginalDomainIdIn(subjectIds)
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andMapTypeEqualTo(MapTypeEnum.TYPE.name())
        .andPropertyCategoryEqualTo(
            SPGOntologyEnum.PROPERTY.equals(ontologyEnum)
                ? PropertyCategoryEnum.BASIC.name()
                : PropertyCategoryEnum.ADVANCED.name());
    List<OntologyPropertyDO> ontologyPropertyDOS =
        ontologyPropertyDOMapper.selectByExampleWithBLOBs(example);
    if (CollectionUtils.isEmpty(ontologyPropertyDOS)) {
      return Collections.emptyList();
    }
    return CollectionsUtils.listMap(
        ontologyPropertyDOS, SimplePropertyConvertor::toSimplePredicate);
  }

  @Override
  public List<SimpleProperty> queryByUniqueId(List<Long> uniqueIds, SPGOntologyEnum ontologyEnum) {
    OntologyPropertyDOExample example = new OntologyPropertyDOExample();
    example
        .createCriteria()
        .andOriginalIdIn(uniqueIds)
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andMapTypeEqualTo(MapTypeEnum.TYPE.name())
        .andPropertyCategoryEqualTo(
            SPGOntologyEnum.PROPERTY.equals(ontologyEnum)
                ? PropertyCategoryEnum.BASIC.name()
                : PropertyCategoryEnum.ADVANCED.name());
    List<OntologyPropertyDO> ontologyPropertyDOS =
        ontologyPropertyDOMapper.selectByExampleWithBLOBs(example);
    if (CollectionUtils.isEmpty(ontologyPropertyDOS)) {
      return Collections.emptyList();
    }
    return CollectionsUtils.listMap(
        ontologyPropertyDOS, SimplePropertyConvertor::toSimplePredicate);
  }

  @Override
  public SimpleProperty queryByUniqueId(Long uniqueId, SPGOntologyEnum ontologyEnum) {
    OntologyPropertyDOExample example = new OntologyPropertyDOExample();
    example
        .createCriteria()
        .andOriginalIdEqualTo(uniqueId)
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andMapTypeEqualTo(MapTypeEnum.TYPE.name())
        .andPropertyCategoryEqualTo(
            SPGOntologyEnum.PROPERTY.equals(ontologyEnum)
                ? PropertyCategoryEnum.BASIC.name()
                : PropertyCategoryEnum.ADVANCED.name());
    List<OntologyPropertyDO> ontologyPropertyDOS =
        ontologyPropertyDOMapper.selectByExampleWithBLOBs(example);
    if (CollectionUtils.isEmpty(ontologyPropertyDOS)) {
      return null;
    }
    return SimplePropertyConvertor.toSimplePredicate(ontologyPropertyDOS.get(0));
  }

  @Override
  public Long queryUniqueIdByPO(String predicateName, String objectTypeName) {
    SimpleSPGType spgType = spgTypeRepository.queryByName(objectTypeName);
    if (null == spgType) {
      return null;
    }

    Long uniqueId = spgType.getUniqueId();

    OntologyPropertyDOExample example = new OntologyPropertyDOExample();
    example
        .createCriteria()
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andMapTypeEqualTo(MapTypeEnum.TYPE.name())
        .andPropertyCategoryEqualTo(PropertyCategoryEnum.BASIC.name())
        .andOriginalRangeIdEqualTo(uniqueId)
        .andPropertyNameEqualTo(predicateName);
    List<OntologyPropertyDO> ontologyPropertyDOS =
        ontologyPropertyDOMapper.selectByExampleWithBLOBs(example);
    return CollectionUtils.isEmpty(ontologyPropertyDOS)
        ? null
        : ontologyPropertyDOS.get(0).getOriginalId();
  }

  @Override
  public List<PropertyRef> queryRefByUniqueId(List<Long> uniqueIds, SPGOntologyEnum ontologyEnum) {
    List<SimpleProperty> simpleProperties = this.queryByUniqueId(uniqueIds, ontologyEnum);
    if (CollectionUtils.isEmpty(simpleProperties)) {
      return Collections.emptyList();
    }

    Set<Long> spgTypeIds = new HashSet<>();
    simpleProperties.forEach(
        e -> {
          spgTypeIds.add(e.getSubjectTypeId().getUniqueId());
          spgTypeIds.add(e.getObjectTypeId().getUniqueId());
        });

    List<SimpleSPGType> spgTypes =
        spgTypeRepository.queryByUniqueId(Lists.newArrayList(spgTypeIds));
    Map<Long, SimpleSPGType> spgTypeMap =
        spgTypes.stream()
            .collect(Collectors.toMap(SimpleSPGType::getUniqueId, Function.identity()));

    List<PropertyRef> propertyRefs = new ArrayList<>();
    for (SimpleProperty simpleProperty : simpleProperties) {
      SimpleSPGType subjectType = spgTypeMap.get(simpleProperty.getSubjectTypeId().getUniqueId());
      SimpleSPGType objectType = spgTypeMap.get(simpleProperty.getObjectTypeId().getUniqueId());
      propertyRefs.add(
          new PropertyRef(
              subjectType.toRef(),
              simpleProperty.getBasicInfo(),
              objectType.toRef(),
              ontologyEnum,
              simpleProperty.getProjectId(),
              simpleProperty.getOntologyId()));
    }
    return propertyRefs;
  }
}
