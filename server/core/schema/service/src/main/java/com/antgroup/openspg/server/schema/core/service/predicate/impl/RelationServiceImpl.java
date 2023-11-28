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

import com.antgroup.openspg.server.schema.core.service.predicate.RelationService;
import com.antgroup.openspg.server.schema.core.service.predicate.SubPropertyService;
import com.antgroup.openspg.server.schema.core.service.predicate.convertor.PredicateAssemble;
import com.antgroup.openspg.server.schema.core.service.predicate.convertor.PredicateConvertor;
import com.antgroup.openspg.server.schema.core.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.schema.core.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.server.schema.core.service.semantic.LogicalRuleService;
import com.antgroup.openspg.server.schema.core.service.semantic.SemanticService;
import com.antgroup.openspg.server.schema.core.service.type.model.SimpleSPGType;
import com.antgroup.openspg.server.schema.core.service.type.repository.SPGTypeRepository;
import com.antgroup.openspg.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.predicate.SubProperty;
import com.antgroup.openspg.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.schema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.schema.model.semantic.RuleCode;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
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
@Service("openRelationService")
public class RelationServiceImpl implements RelationService {

  @Autowired private PropertyRepository propertyRepository;
  @Autowired private PropertyAdvancedConfigHandler propertyAdvancedConfigService;
  @Autowired private SubPropertyService subPropertyService;
  @Autowired private SemanticService semanticService;
  @Autowired private LogicalRuleService logicalRuleService;
  @Autowired private SPGTypeRepository simpleSpgTypeRepository;

  @Override
  public int create(Relation relation) {
    if (Boolean.TRUE.equals(relation.getInherited())) {
      log.info("relation: {} is inherited, skip save", relation.getName());
      return 0;
    }
    if (Boolean.TRUE.equals(relation.isSemanticRelation())) {
      log.info("relation: {} is semantic, skip save", relation.getName());
      return 0;
    }

    propertyAdvancedConfigService.alterAdvancedConfig(relation, AlterOperationEnum.CREATE);
    int cnt = propertyRepository.save(PredicateConvertor.toSimpleProperty(relation));

    log.info("property: {} is created", relation.getName());
    return cnt;
  }

  @Override
  public int update(Relation relation) {
    if (Boolean.TRUE.equals(relation.getInherited())) {
      log.info("relation: {} is inherited, skip save", relation.getName());
      return 0;
    }
    if (Boolean.TRUE.equals(relation.isSemanticRelation())) {
      log.info("relation: {} is semantic, skip save", relation.getName());
      return 0;
    }

    propertyAdvancedConfigService.alterAdvancedConfig(relation, AlterOperationEnum.UPDATE);
    int cnt = propertyRepository.update(PredicateConvertor.toSimpleProperty(relation));

    log.info("property: {} is updated", relation.getName());
    return cnt;
  }

  @Override
  public int delete(Relation relation) {
    if (Boolean.TRUE.equals(relation.getInherited())) {
      log.info("relation: {} is inherited, skip save", relation.getName());
      return 0;
    }
    if (Boolean.TRUE.equals(relation.isSemanticRelation())) {
      log.info("relation: {} is semantic, skip save", relation.getName());
      return 0;
    }

    propertyAdvancedConfigService.alterAdvancedConfig(relation, AlterOperationEnum.DELETE);
    int cnt = propertyRepository.delete(PredicateConvertor.toSimpleProperty(relation));

    log.info("property: {} is updated", relation.getName());
    return cnt;
  }

  @Override
  public List<Relation> queryBySubjectId(List<Long> subjectIds) {
    if (CollectionUtils.isEmpty(subjectIds)) {
      return Collections.emptyList();
    }

    List<SimpleProperty> simplePredicates =
        propertyRepository.queryBySubjectId(subjectIds, SPGOntologyEnum.RELATION);
    if (CollectionUtils.isEmpty(simplePredicates)) {
      return Collections.emptyList();
    }

    Set<Long> spgTypeIds = new HashSet<>();
    spgTypeIds.addAll(
        simplePredicates.stream()
            .map(e -> e.getSubjectTypeId().getUniqueId())
            .collect(Collectors.toList()));
    spgTypeIds.addAll(
        simplePredicates.stream()
            .map(e -> e.getObjectTypeId().getUniqueId())
            .collect(Collectors.toList()));
    List<SimpleSPGType> spgTypes =
        simpleSpgTypeRepository.queryByUniqueId(Lists.newArrayList(spgTypeIds));

    List<Long> relationIds =
        simplePredicates.stream().map(SimpleProperty::getUniqueId).collect(Collectors.toList());
    List<SubProperty> subProperties =
        subPropertyService.queryBySubjectId(relationIds, SPGOntologyEnum.RELATION);

    List<PredicateSemantic> semantics =
        semanticService.queryBySubjectIds(relationIds, SPGOntologyEnum.RELATION);

    List<RuleCode> ruleCodes =
        simplePredicates.stream()
            .map(SimpleProperty::getRuleCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    List<LogicalRule> logicalRules = logicalRuleService.queryByRuleCode(ruleCodes);

    return PredicateAssemble.toRelation(
        simplePredicates, spgTypes, subProperties, semantics, logicalRules);
  }
}
