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

package com.antgroup.openspg.server.schema.core.service.concept.impl;

import com.antgroup.openspg.server.schema.core.service.concept.ConceptSemanticService;
import com.antgroup.openspg.server.schema.core.service.concept.convertor.ConceptSemanticConvertor;
import com.antgroup.openspg.server.schema.core.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.server.schema.core.service.semantic.LogicalRuleService;
import com.antgroup.openspg.server.schema.core.service.semantic.model.LogicalCausationQuery;
import com.antgroup.openspg.server.schema.core.service.semantic.model.SimpleSemantic;
import com.antgroup.openspg.server.schema.core.service.semantic.repository.SemanticRepository;
import com.antgroup.openspg.server.core.schema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.server.core.schema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.server.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.server.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.server.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.server.core.schema.model.semantic.SystemPredicateEnum;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConceptSemanticServiceImpl implements ConceptSemanticService {

  @Autowired private SemanticRepository semanticRepository;
  @Autowired private LogicalRuleService logicalRuleService;
  @Autowired private PropertyRepository propertyRepository;

  @Override
  public List<DynamicTaxonomySemantic> queryDynamicTaxonomySemantic(
      SPGTypeIdentifier conceptTypeIdentifier, ConceptIdentifier conceptIdentifier) {
    Long taxonomicRelationId =
        propertyRepository.queryUniqueIdByPO(
            SystemPredicateEnum.BELONG_TO.getName(), conceptTypeIdentifier.toString());
    if (null == taxonomicRelationId) {
      return new ArrayList<>();
    }

    LogicalCausationQuery query =
        new LogicalCausationQuery()
            .setPredicateName(SystemPredicateEnum.BELONG_TO.getName())
            .setSubjectName(taxonomicRelationId.toString())
            .setObjectTypeNames(Lists.newArrayList(conceptTypeIdentifier.toString()))
            .setObjectName(conceptIdentifier == null ? null : conceptIdentifier.toString());
    List<SimpleSemantic> simpleSemantics = semanticRepository.queryConceptSemanticByCond(query);
    if (CollectionUtils.isEmpty(simpleSemantics)) {
      return new ArrayList<>();
    }

    List<LogicalRule> logicalRules = new ArrayList<>();
    List<RuleCode> ruleCodes =
        simpleSemantics.stream()
            .map(SimpleSemantic::getRuleCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(ruleCodes)) {
      logicalRules = logicalRuleService.queryByRuleCode(ruleCodes);
    }

    return ConceptSemanticConvertor.convertList(simpleSemantics, logicalRules);
  }

  @Override
  public int deleteDynamicTaxonomySemantic(
      SPGTypeIdentifier conceptTypeIdentifier, ConceptIdentifier conceptIdentifier) {
    List<DynamicTaxonomySemantic> semantics =
        this.queryDynamicTaxonomySemantic(conceptTypeIdentifier, conceptIdentifier);
    List<RuleCode> ruleCodes =
        semantics.stream()
            .map(DynamicTaxonomySemantic::getRuleCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    this.deleteLogicalRule(ruleCodes);

    return semanticRepository.deleteByObject(
        SystemPredicateEnum.BELONG_TO.getName(),
        conceptTypeIdentifier.toString(),
        conceptIdentifier == null ? null : conceptIdentifier.getId(),
        SPGOntologyEnum.CONCEPT);
  }

  @Override
  public int upsertDynamicTaxonomySemantic(DynamicTaxonomySemantic dynamicTaxonomySemantic) {
    List<DynamicTaxonomySemantic> existSemantics =
        this.queryDynamicTaxonomySemantic(
            dynamicTaxonomySemantic.getConceptTypeIdentifier(),
            dynamicTaxonomySemantic.getConceptIdentifier());
    if (CollectionUtils.isEmpty(existSemantics)) {
      if (dynamicTaxonomySemantic.getLogicalRule() != null) {
        logicalRuleService.create(dynamicTaxonomySemantic.getLogicalRule());
      }

      Long taxonomicRelationId =
          propertyRepository.queryUniqueIdByPO(
              SystemPredicateEnum.BELONG_TO.getName(),
              dynamicTaxonomySemantic.getConceptTypeIdentifier().toString());
      return semanticRepository.saveOrUpdate(
          ConceptSemanticConvertor.convert(dynamicTaxonomySemantic, taxonomicRelationId));
    }

    LogicalRule logicalRule = existSemantics.get(0).getLogicalRule();
    if (logicalRule == null) {
      return logicalRuleService.create(dynamicTaxonomySemantic.getLogicalRule());
    }

    LogicalRule update =
        new LogicalRule(
            logicalRule.getCode(),
            logicalRule.getVersion(),
            null,
            null,
            null,
            dynamicTaxonomySemantic.getLogicalRule().getContent(),
            null);
    return logicalRuleService.update(update);
  }

  @Override
  public List<LogicalCausationSemantic> queryLogicalCausationSemantic(LogicalCausationQuery query) {
    List<SimpleSemantic> exist = semanticRepository.queryConceptSemanticByCond(query);
    if (CollectionUtils.isEmpty(exist)) {
      return Collections.emptyList();
    }

    List<RuleCode> ruleCodes =
        exist.stream()
            .map(SimpleSemantic::getRuleCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    List<LogicalRule> logicalRules = logicalRuleService.queryByRuleCode(ruleCodes);
    return ConceptSemanticConvertor.convertSemanticList(exist, logicalRules);
  }

  @Override
  public int deleteLogicalCausationSemantic(LogicalCausationSemantic conceptSemantic) {
    List<SimpleSemantic> exist = this.queryExistSemantic(conceptSemantic);
    if (CollectionUtils.isEmpty(exist)) {
      return 0;
    }

    List<RuleCode> ruleCodes =
        exist.stream()
            .map(SimpleSemantic::getRuleCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    logicalRuleService.deleteByRuleId(ruleCodes);

    return semanticRepository.deleteConceptSemantic(conceptSemantic);
  }

  @Override
  public int upsertLogicalCausationSemantic(LogicalCausationSemantic conceptSemantic) {
    List<SimpleSemantic> exist = this.queryExistSemantic(conceptSemantic);
    if (CollectionUtils.isNotEmpty(exist)) {
      RuleCode ruleCode = exist.get(0).getRuleCode();
      if (ruleCode != null) {
        LogicalRule update =
            new LogicalRule(
                ruleCode,
                null,
                null,
                null,
                null,
                conceptSemantic.getLogicalRule().getContent(),
                null);
        return logicalRuleService.update(update);
      } else {
        return logicalRuleService.create(conceptSemantic.getLogicalRule());
      }
    } else {
      logicalRuleService.create(conceptSemantic.getLogicalRule());
      return semanticRepository.saveOrUpdate(ConceptSemanticConvertor.convert(conceptSemantic));
    }
  }

  private void deleteLogicalRule(List<RuleCode> ruleCodes) {
    if (CollectionUtils.isEmpty(ruleCodes)) {
      return;
    }

    logicalRuleService.deleteByRuleId(ruleCodes);
  }

  private List<SimpleSemantic> queryExistSemantic(LogicalCausationSemantic conceptSemantic) {
    LogicalCausationQuery query =
        new LogicalCausationQuery()
            .setSubjectTypeNames(
                Lists.newArrayList(conceptSemantic.getSubjectTypeIdentifier().toString()))
            .setSubjectName(conceptSemantic.getSubjectIdentifier().getId())
            .setPredicateName(conceptSemantic.getPredicateIdentifier().getName())
            .setObjectTypeNames(
                Lists.newArrayList(conceptSemantic.getObjectTypeIdentifier().toString()))
            .setObjectName(conceptSemantic.getObjectIdentifier().getId());
    return semanticRepository.queryConceptSemanticByCond(query);
  }
}
