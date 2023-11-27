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
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.predicate.SubPropertyService;
import com.antgroup.openspg.core.spgschema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.ConstraintRepository;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.core.spgschema.service.semantic.LogicalRuleService;
import com.antgroup.openspg.core.spgschema.service.semantic.SemanticService;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyAdvancedConfigHandler {

  @Autowired private PropertyRepository propertyRepository;
  @Autowired private ConstraintRepository constraintRepository;
  @Autowired private SubPropertyService subPropertyService;
  @Autowired private SemanticService semanticService;
  @Autowired private LogicalRuleService logicalRuleService;

  public void alterAdvancedConfig(Property property, AlterOperationEnum alterOperation) {
    PropertyAdvancedConfig advancedConfig = property.getAdvancedConfig();
    switch (alterOperation) {
      case CREATE:
        this.createAdvancedConfig(advancedConfig);
        break;
      case DELETE:
        this.deleteAdvancedConfig(advancedConfig);
        break;
      case UPDATE:
        this.updateAdvancedConfig(property);
        break;
      default:
        break;
    }
  }

  private void createAdvancedConfig(PropertyAdvancedConfig advancedConfig) {
    if (advancedConfig == null) {
      return;
    }

    if (advancedConfig.getConstraint() != null) {
      constraintRepository.upsert(advancedConfig.getConstraint());
    }
    if (advancedConfig.getLogicalRule() != null) {
      logicalRuleService.create(advancedConfig.getLogicalRule());
    }
    if (CollectionUtils.isNotEmpty(advancedConfig.getSubProperties())) {
      advancedConfig.getSubProperties().forEach(e -> subPropertyService.create(e));
    }
    if (CollectionUtils.isNotEmpty(advancedConfig.getSemantics())) {
      advancedConfig.getSemantics().forEach(e -> semanticService.saveOrUpdate(e));
    }
  }

  private void deleteAdvancedConfig(PropertyAdvancedConfig advancedConfig) {
    if (advancedConfig == null) {
      return;
    }

    if (advancedConfig.getConstraint() != null) {
      constraintRepository.deleteById(advancedConfig.getConstraint().getId());
    }
    if (advancedConfig.getLogicalRule() != null) {
      logicalRuleService.delete(advancedConfig.getLogicalRule());
    }
    if (CollectionUtils.isNotEmpty(advancedConfig.getSubProperties())) {
      advancedConfig.getSubProperties().forEach(e -> subPropertyService.delete(e));
    }
    if (CollectionUtils.isNotEmpty(advancedConfig.getSemantics())) {
      advancedConfig.getSemantics().forEach(e -> semanticService.delete(e));
    }
  }

  private void updateAdvancedConfig(Property property) {
    SPGOntologyEnum ontologyEnum =
        property instanceof Relation ? SPGOntologyEnum.RELATION : SPGOntologyEnum.PROPERTY;
    Long uniqueId = property.getUniqueId();
    SimpleProperty simpleProperty = propertyRepository.queryByUniqueId(uniqueId, ontologyEnum);
    if (null == simpleProperty) {
      return;
    }

    PropertyAdvancedConfig advancedConfig = property.getAdvancedConfig();
    if (advancedConfig.getConstraint() != null) {
      constraintRepository.upsert(advancedConfig.getConstraint());
    } else if (simpleProperty.getConstraintId() != null && simpleProperty.getConstraintId() > 0L) {
      constraintRepository.deleteById(simpleProperty.getConstraintId());
    }
    if (advancedConfig.getLogicalRule() != null) {
      if (advancedConfig.getLogicalRule().getCode() == null) {
        logicalRuleService.create(advancedConfig.getLogicalRule());
      } else {
        logicalRuleService.update(advancedConfig.getLogicalRule());
      }
    } else if (simpleProperty.getRuleCode() != null) {
      logicalRuleService.deleteByRuleId(Lists.newArrayList(simpleProperty.getRuleCode()));
    }

    if (CollectionUtils.isNotEmpty(advancedConfig.getSubProperties())) {
      this.updateSubProperty(advancedConfig.getSubProperties());
    }

    if (CollectionUtils.isNotEmpty(advancedConfig.getSemantics())) {
      this.updateSemantic(advancedConfig.getSemantics());
    }
  }

  private void updateSubProperty(List<SubProperty> subProperties) {
    List<SubProperty> deleteSubProperties =
        subProperties.stream()
            .filter(e -> AlterOperationEnum.DELETE.equals(e.getAlterOperation()))
            .collect(Collectors.toList());
    deleteSubProperties.forEach(e -> subPropertyService.delete(e));

    List<SubProperty> updateSubProperties =
        subProperties.stream()
            .filter(e -> AlterOperationEnum.UPDATE.equals(e.getAlterOperation()))
            .collect(Collectors.toList());
    updateSubProperties.forEach(e -> subPropertyService.update(e));

    List<SubProperty> createSubProperties =
        subProperties.stream()
            .filter(e -> AlterOperationEnum.CREATE.equals(e.getAlterOperation()))
            .collect(Collectors.toList());
    createSubProperties.forEach(e -> subPropertyService.create(e));
  }

  private void updateSemantic(List<PredicateSemantic> semantics) {
    List<PredicateSemantic> deleteSemantics =
        semantics.stream()
            .filter(e -> AlterOperationEnum.DELETE.equals(e.getAlterOperation()))
            .collect(Collectors.toList());
    deleteSemantics.forEach(e -> semanticService.delete(e));

    List<PredicateSemantic> saveOrUpdateSemantics =
        semantics.stream()
            .filter(
                e ->
                    AlterOperationEnum.UPDATE.equals(e.getAlterOperation())
                        || AlterOperationEnum.CREATE.equals(e.getAlterOperation()))
            .collect(Collectors.toList());
    saveOrUpdateSemantics.forEach(e -> semanticService.saveOrUpdate(e));
  }
}
