/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.core.schema.service.concept.convertor;

import com.antgroup.openspg.core.schema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.core.schema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.server.core.schema.service.semantic.model.SimpleSemantic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class ConceptSemanticConvertor {

  public static List<DynamicTaxonomySemantic> convertList(
      List<SimpleSemantic> simpleSemanticList, List<LogicalRule> logicalRuleList) {
    if (CollectionUtils.isEmpty(simpleSemanticList)) {
      return Collections.emptyList();
    }

    Map<RuleCode, LogicalRule> logicalRuleMap =
        logicalRuleList.stream()
            .collect(Collectors.toMap(LogicalRule::getCode, Function.identity()));

    List<DynamicTaxonomySemantic> semantics = new ArrayList<>();
    simpleSemanticList.forEach(
        simpleSemantic ->
            semantics.add(
                convert(simpleSemantic, logicalRuleMap.get(simpleSemantic.getRuleCode()))));
    return semantics;
  }

  public static DynamicTaxonomySemantic convert(
      SimpleSemantic simpleSemantic, LogicalRule logicalRule) {
    return new DynamicTaxonomySemantic(
        simpleSemantic.getObjectTypeIdentifier(),
        new ConceptIdentifier(simpleSemantic.getObjectId()),
        logicalRule);
  }

  public static SimpleSemantic convert(DynamicTaxonomySemantic semantic, Long taxonomicRelationId) {
    return new SimpleSemantic(
        SPGOntologyEnum.CONCEPT,
        taxonomicRelationId.toString(),
        semantic.getConceptIdentifier().getId(),
        semantic.getPredicateIdentifier(),
        null,
        semantic.getConceptTypeIdentifier(),
        semantic.getLogicalRule() == null ? null : semantic.getLogicalRule().getCode());
  }

  public static SimpleSemantic convert(LogicalCausationSemantic semantic) {
    return new SimpleSemantic(
        SPGOntologyEnum.CONCEPT,
        semantic.getSubjectIdentifier().getId(),
        semantic.getObjectIdentifier().getId(),
        semantic.getPredicateIdentifier(),
        semantic.getSubjectTypeIdentifier(),
        semantic.getObjectTypeIdentifier(),
        semantic.getLogicalRule() == null ? null : semantic.getLogicalRule().getCode());
  }

  public static List<LogicalCausationSemantic> convertSemanticList(
      List<SimpleSemantic> simpleSemanticList, List<LogicalRule> logicalRuleList) {
    if (CollectionUtils.isEmpty(simpleSemanticList)) {
      return Collections.emptyList();
    }

    List<LogicalCausationSemantic> semantics = new ArrayList<>();
    Map<RuleCode, LogicalRule> logicalRuleMap =
        logicalRuleList.stream()
            .collect(Collectors.toMap(LogicalRule::getCode, Function.identity(), (o1, o2) -> o1));
    simpleSemanticList.forEach(
        simpleSemantic ->
            semantics.add(
                convertSemantic(simpleSemantic, logicalRuleMap.get(simpleSemantic.getRuleCode()))));
    return semantics;
  }

  public static LogicalCausationSemantic convertSemantic(
      SimpleSemantic simpleSemantic, LogicalRule logicalRule) {
    return new LogicalCausationSemantic(
        simpleSemantic.getSubjectTypeIdentifier(),
        new ConceptIdentifier(simpleSemantic.getSubjectId()),
        simpleSemantic.getPredicateIdentifier(),
        simpleSemantic.getObjectTypeIdentifier(),
        new ConceptIdentifier(simpleSemantic.getObjectId()),
        logicalRule);
  }
}
