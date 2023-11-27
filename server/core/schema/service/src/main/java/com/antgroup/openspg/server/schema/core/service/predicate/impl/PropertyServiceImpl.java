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

package com.antgroup.openspg.server.schema.core.service.predicate.impl;

import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.RuleCode;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.predicate.PropertyService;
import com.antgroup.openspg.core.spgschema.service.predicate.SubPropertyService;
import com.antgroup.openspg.core.spgschema.service.predicate.convertor.PredicateAssemble;
import com.antgroup.openspg.core.spgschema.service.predicate.convertor.PredicateConvertor;
import com.antgroup.openspg.core.spgschema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.ConstraintRepository;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.core.spgschema.service.semantic.LogicalRuleService;
import com.antgroup.openspg.core.spgschema.service.semantic.SemanticService;
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType;
import com.antgroup.openspg.core.spgschema.service.type.repository.SPGTypeRepository;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("openPropertyService")
public class PropertyServiceImpl implements PropertyService {

  @Autowired private PropertyRepository simplePredicateRepository;
  @Autowired private PropertyAdvancedConfigHandler propertyAdvancedConfigService;
  @Autowired private ConstraintRepository constraintRepository;
  @Autowired private SubPropertyService subPropertyService;
  @Autowired private SemanticService semanticService;
  @Autowired private LogicalRuleService logicalRuleService;
  @Autowired private SPGTypeRepository simpleSpgTypeRepository;

  @Override
  public int create(Property property) {
    if (Boolean.TRUE.equals(property.getInherited())) {
      log.info("property: {} is inherited, skip save", property.getName());
      return 0;
    }

    propertyAdvancedConfigService.alterAdvancedConfig(property, AlterOperationEnum.CREATE);
    int cnt = simplePredicateRepository.save(PredicateConvertor.toSimpleProperty(property));
    log.info("property: {} is created", property.getName());
    return cnt;
  }

  @Override
  public int update(Property property) {
    if (Boolean.TRUE.equals(property.getInherited())) {
      log.info("property: {} is inherited, skip save", property.getName());
      return 0;
    }

    propertyAdvancedConfigService.alterAdvancedConfig(property, AlterOperationEnum.UPDATE);
    int cnt = simplePredicateRepository.update(PredicateConvertor.toSimpleProperty(property));
    log.info("property: {} is created", property.getName());
    return cnt;
  }

  @Override
  public int delete(Property property) {
    if (Boolean.TRUE.equals(property.getInherited())) {
      log.info("property: {} is inherited, skip save", property.getName());
      return 0;
    }

    propertyAdvancedConfigService.alterAdvancedConfig(property, AlterOperationEnum.DELETE);
    int cnt = simplePredicateRepository.delete(PredicateConvertor.toSimpleProperty(property));
    log.info("property: {} is deleted", property.getName());
    return cnt;
  }

  @Override
  public List<Property> queryBySubjectId(List<Long> subjectIds) {
    if (CollectionUtils.isEmpty(subjectIds)) {
      return Collections.emptyList();
    }

    List<SimpleProperty> simpleProperties =
        simplePredicateRepository.queryBySubjectId(subjectIds, SPGOntologyEnum.PROPERTY);
    if (CollectionUtils.isEmpty(simpleProperties)) {
      return Collections.emptyList();
    }

    Set<Long> spgTypeIds = new HashSet<>();
    spgTypeIds.addAll(
        simpleProperties.stream()
            .map(e -> e.getSubjectTypeId().getUniqueId())
            .collect(Collectors.toList()));
    spgTypeIds.addAll(
        simpleProperties.stream()
            .map(e -> e.getObjectTypeId().getUniqueId())
            .collect(Collectors.toList()));
    List<SimpleSPGType> spgTypes =
        simpleSpgTypeRepository.queryByUniqueId(Lists.newArrayList(spgTypeIds));

    List<Long> constraintIds =
        simpleProperties.stream()
            .map(SimpleProperty::getConstraintId)
            .filter(e -> e != null && e > 0)
            .collect(Collectors.toList());
    List<Constraint> constraints = constraintRepository.queryById(constraintIds);

    List<Long> propertyIds =
        simpleProperties.stream().map(SimpleProperty::getUniqueId).collect(Collectors.toList());
    List<SubProperty> subProperties =
        subPropertyService.queryBySubjectId(propertyIds, SPGOntologyEnum.PROPERTY);

    List<PredicateSemantic> semantics =
        semanticService.queryBySubjectIds(propertyIds, SPGOntologyEnum.PROPERTY);

    List<RuleCode> ruleCodes =
        simpleProperties.stream()
            .map(SimpleProperty::getRuleCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    List<LogicalRule> logicalRules = logicalRuleService.queryByRuleCode(ruleCodes);

    return PredicateAssemble.toProperty(
        simpleProperties, spgTypes, constraints, subProperties, semantics, logicalRules);
  }
}
