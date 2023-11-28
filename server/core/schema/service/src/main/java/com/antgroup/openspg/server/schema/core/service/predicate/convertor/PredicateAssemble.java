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

package com.antgroup.openspg.server.schema.core.service.predicate.convertor;

import com.antgroup.openspg.server.schema.core.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.schema.core.service.predicate.model.SimpleSubProperty;
import com.antgroup.openspg.server.schema.core.service.type.convertor.SPGTypeConvertor;
import com.antgroup.openspg.server.schema.core.service.type.model.SimpleSPGType;
import com.antgroup.openspg.schema.model.SchemaException;
import com.antgroup.openspg.schema.model.constraint.Constraint;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.schema.model.predicate.PropertyRef;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.predicate.SubProperty;
import com.antgroup.openspg.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.schema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.schema.model.semantic.RuleCode;
import com.antgroup.openspg.schema.model.type.BasicType;
import com.antgroup.openspg.schema.model.type.SPGTypeRef;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PredicateAssemble {

  public static List<Property> toProperty(
      List<SimpleProperty> simpleProperties,
      List<SimpleSPGType> spgTypes,
      List<Constraint> constraints,
      List<SubProperty> subProperties,
      List<PredicateSemantic> semantics,
      List<LogicalRule> logicalRules) {
    Map<Long, SimpleSPGType> spgTypeMap =
        spgTypes.stream()
            .collect(Collectors.toMap(SimpleSPGType::getUniqueId, Function.identity()));
    Map<Long, Constraint> constraintMap =
        constraints.stream().collect(Collectors.toMap(Constraint::getId, Function.identity()));
    Map<Long, List<SubProperty>> subPropertyMap =
        subProperties.stream()
            .collect(Collectors.groupingBy((e -> e.getSubjectTypeRef().getUniqueId())));
    Map<Long, List<PredicateSemantic>> semanticMap =
        semantics.stream().collect(Collectors.groupingBy(e -> e.getSubjectTypeRef().getUniqueId()));
    Map<RuleCode, LogicalRule> logicalRuleMap =
        logicalRules.stream()
            .collect(Collectors.toMap((LogicalRule::getCode), Function.identity()));

    List<Property> propertys = new ArrayList<>();
    simpleProperties.forEach(
        simpleProperty -> {
          SimpleSPGType subjectType =
              spgTypeMap.get(simpleProperty.getSubjectTypeId().getUniqueId());
          if (null == subjectType) {
            throw SchemaException.uniqueIdNotExist(simpleProperty.getSubjectTypeId().getUniqueId());
          }
          SimpleSPGType objectType = spgTypeMap.get(simpleProperty.getObjectTypeId().getUniqueId());
          if (null == objectType) {
            throw SchemaException.uniqueIdNotExist(simpleProperty.getObjectTypeId().getUniqueId());
          }

          Constraint constraint = null;
          if (simpleProperty.getConstraintId() != null && simpleProperty.getConstraintId() > 0) {
            constraint = constraintMap.get(simpleProperty.getConstraintId());
          }
          List<SubProperty> subPropertyList =
              subPropertyMap.getOrDefault(simpleProperty.getUniqueId(), Collections.emptyList());
          List<PredicateSemantic> semanticList =
              semanticMap.getOrDefault(simpleProperty.getUniqueId(), Collections.emptyList());
          LogicalRule logicalRule =
              simpleProperty.getRuleCode() == null
                  ? null
                  : logicalRuleMap.get(simpleProperty.getRuleCode());
          Property property =
              toProperty(
                  subjectType,
                  objectType,
                  simpleProperty,
                  constraint,
                  subPropertyList,
                  semanticList,
                  logicalRule);
          propertys.add(property);
        });
    return propertys;
  }

  public static Property toProperty(
      SimpleSPGType subjectType,
      SimpleSPGType objectType,
      SimpleProperty simpleProperty,
      Constraint constraint,
      List<SubProperty> subProperties,
      List<PredicateSemantic> semantics,
      LogicalRule logicalRule) {
    SPGTypeRef subjectTypeRef = subjectType.toRef();
    SPGTypeRef objectTypeRef = objectType.toRef();

    PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
    advancedConfig.setMultiVersionConfig(simpleProperty.getMultiVersionConfig());
    advancedConfig.setMountedConceptConfig(simpleProperty.getMountedConceptConfig());
    advancedConfig.setEncryptTypeEnum(simpleProperty.getEncryptTypeEnum());
    advancedConfig.setPropertyGroup(simpleProperty.getPropertyGroup());
    advancedConfig.setConstraint(constraint);
    advancedConfig.setSubProperties(subProperties == null ? new ArrayList<>() : subProperties);
    advancedConfig.setSemantics(semantics == null ? new ArrayList<>() : semantics);
    advancedConfig.setLogicalRule(logicalRule);

    Property property =
        new Property(
            simpleProperty.getBasicInfo(), subjectTypeRef, objectTypeRef, false, advancedConfig);
    property.setProjectId(simpleProperty.getProjectId());
    property.setOntologyId(simpleProperty.getOntologyId());
    property.setAlterOperation(simpleProperty.getAlterOperation());
    property.setExtInfo(simpleProperty.getExtInfo());
    return property;
  }

  public static List<Relation> toRelation(
      List<SimpleProperty> simplePredicates,
      List<SimpleSPGType> spgTypes,
      List<SubProperty> subProperties,
      List<PredicateSemantic> semantics,
      List<LogicalRule> logicalRules) {
    Map<Long, SimpleSPGType> spgTypeMap =
        spgTypes.stream()
            .collect(Collectors.toMap(SimpleSPGType::getUniqueId, Function.identity()));
    Map<Long, List<SubProperty>> subPropertyMap =
        subProperties.stream()
            .collect(Collectors.groupingBy((e -> e.getSubjectTypeRef().getUniqueId())));
    Map<Long, List<PredicateSemantic>> semanticMap =
        semantics.stream().collect(Collectors.groupingBy(e -> e.getSubjectTypeRef().getUniqueId()));
    Map<RuleCode, LogicalRule> logicalRuleMap =
        logicalRules.stream()
            .collect(Collectors.toMap((LogicalRule::getCode), Function.identity()));

    List<Relation> relations = new ArrayList<>();
    simplePredicates.forEach(
        simplePredicate -> {
          List<SubProperty> subPropertyList =
              subPropertyMap.getOrDefault(simplePredicate.getUniqueId(), Collections.emptyList());
          List<PredicateSemantic> semanticList =
              semanticMap.getOrDefault(simplePredicate.getUniqueId(), Collections.emptyList());
          LogicalRule logicalRule =
              simplePredicate.getRuleCode() == null
                  ? null
                  : logicalRuleMap.get(simplePredicate.getRuleCode());

          SimpleSPGType subjectType =
              spgTypeMap.get(simplePredicate.getSubjectTypeId().getUniqueId());
          if (null == subjectType) {
            throw SchemaException.uniqueIdNotExist(
                simplePredicate.getSubjectTypeId().getUniqueId());
          }
          SimpleSPGType objectType =
              spgTypeMap.get(simplePredicate.getObjectTypeId().getUniqueId());
          if (null == objectType) {
            throw SchemaException.uniqueIdNotExist(
                simplePredicate.getSubjectTypeId().getUniqueId());
          }

          Relation relation =
              toRelation(
                  subjectType,
                  objectType,
                  simplePredicate,
                  subPropertyList,
                  semanticList,
                  logicalRule);
          relations.add(relation);
        });
    return relations;
  }

  public static Relation toRelation(
      SimpleSPGType subjectType,
      SimpleSPGType objectType,
      SimpleProperty simpleProperty,
      List<SubProperty> subProperties,
      List<PredicateSemantic> semantics,
      LogicalRule logicalRule) {
    SPGTypeRef subjectTypeRef = subjectType.toRef();
    SPGTypeRef objectTypeRef = objectType.toRef();

    PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
    advancedConfig.setMultiVersionConfig(simpleProperty.getMultiVersionConfig());
    advancedConfig.setMountedConceptConfig(simpleProperty.getMountedConceptConfig());
    advancedConfig.setEncryptTypeEnum(simpleProperty.getEncryptTypeEnum());
    advancedConfig.setPropertyGroup(simpleProperty.getPropertyGroup());
    advancedConfig.setSubProperties(subProperties == null ? new ArrayList<>() : subProperties);
    advancedConfig.setSemantics(semantics == null ? new ArrayList<>() : semantics);
    advancedConfig.setLogicalRule(logicalRule);

    Relation relation =
        new Relation(
            simpleProperty.getBasicInfo(),
            subjectTypeRef,
            objectTypeRef,
            false,
            advancedConfig,
            false);
    relation.setProjectId(simpleProperty.getProjectId());
    relation.setOntologyId(simpleProperty.getOntologyId());
    relation.setAlterOperation(simpleProperty.getAlterOperation());
    relation.setExtInfo(simpleProperty.getExtInfo());
    return relation;
  }

  public static List<SubProperty> toSubProperty(
      List<SimpleSubProperty> simpleSubPredicates,
      List<PropertyRef> propertyRefs,
      List<SimpleSPGType> simpleSpgTypes,
      List<Constraint> constraints) {
    Map<Long, PropertyRef> propertyMap =
        propertyRefs.stream()
            .collect(Collectors.toMap((e -> e.getOntologyId().getUniqueId()), Function.identity()));
    Map<Long, SimpleSPGType> spgTypeMap =
        simpleSpgTypes.stream()
            .collect(Collectors.toMap((e -> e.getOntologyId().getUniqueId()), Function.identity()));
    Map<Long, Constraint> constraintMap =
        constraints.stream().collect(Collectors.toMap(Constraint::getId, Function.identity()));

    List<SubProperty> subProperties = new ArrayList<>();
    simpleSubPredicates.forEach(
        simpleSubProperty -> {
          PropertyRef propertyRef = propertyMap.get(simpleSubProperty.getSubjectId().getUniqueId());
          if (null == propertyRef) {
            throw SchemaException.uniqueIdNotExist(simpleSubProperty.getSubjectId().getUniqueId());
          }
          SimpleSPGType spgType = spgTypeMap.get(simpleSubProperty.getObjectId().getUniqueId());
          if (null == spgType) {
            throw SchemaException.uniqueIdNotExist(simpleSubProperty.getObjectId().getUniqueId());
          }
          BasicType objectType = (BasicType) SPGTypeConvertor.toBaseSpgType(spgType);

          PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
          advancedConfig.setMultiVersionConfig(simpleSubProperty.getMultiVersionConfig());
          advancedConfig.setEncryptTypeEnum(simpleSubProperty.getEncryptTypeEnum());
          if (null != simpleSubProperty.getConstraintId()) {
            advancedConfig.setConstraint(constraintMap.get(simpleSubProperty.getConstraintId()));
          }

          SubProperty subProperty =
              new SubProperty(
                  simpleSubProperty.getBasicInfo(),
                  propertyRef,
                  objectType.toRef(),
                  advancedConfig);
          subProperty.setProjectId(simpleSubProperty.getProjectId());
          subProperty.setOntologyId(simpleSubProperty.getOntologyId());
          subProperty.setAlterOperation(simpleSubProperty.getAlterOperation());
          subProperty.setExtInfo(simpleSubProperty.getExtInfo());
          subProperties.add(subProperty);
        });
    return subProperties;
  }
}
