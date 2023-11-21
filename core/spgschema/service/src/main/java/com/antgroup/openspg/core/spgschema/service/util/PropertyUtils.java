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

package com.antgroup.openspg.core.spgschema.service.util;

import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.SchemaConstants;
import com.antgroup.openspg.core.spgschema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.service.type.model.BuiltInPropertyEnum;
import com.google.common.collect.Lists;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/** Property copy tool, used to generate a new property based on inherited property. */
public class PropertyUtils {
  public static Relation generateSemanticRelation(
      Property property, Set<SPGTypeIdentifier> spreadStdTypeNames) {
    SPGTypeRef objectTypeRef = property.getObjectTypeRef();
    if (SPGTypeEnum.BASIC_TYPE.equals(objectTypeRef.getSpgTypeEnum())) {
      return null;
    }

    if (SPGTypeEnum.STANDARD_TYPE.equals(objectTypeRef.getSpgTypeEnum())) {
      if (!spreadStdTypeNames.contains(objectTypeRef.getBasicInfo().getName())) {
        return null;
      }
    }

    return PropertyUtils.generateRelation(property);
  }

  /**
   * Create a property type by spo triple and basic info.
   *
   * @param subjectTypeRef subject type ref
   * @param objectTypeRef object type ref
   * @param basicInfo basic information
   * @param multiValue if the value is multi
   * @return new property
   */
  public static Property newProperty(
      SPGTypeRef subjectTypeRef,
      SPGTypeRef objectTypeRef,
      BasicInfo<PredicateIdentifier> basicInfo,
      boolean multiValue) {
    Property property =
        new Property(basicInfo, subjectTypeRef, objectTypeRef, false, new PropertyAdvancedConfig());
    property.setProjectId(subjectTypeRef.getProjectId());

    if (multiValue) {
      BaseConstraintItem multiValueConst = new MultiValConstraint();
      Constraint constraint = new Constraint(null, Lists.newArrayList(multiValueConst));
      property.getAdvancedConfig().setConstraint(constraint);
    }
    return property;
  }

  /**
   * Create a relation type by spo triple.
   *
   * @param subjectTypeRef subject type ref
   * @param objectTypeRef object type ref
   * @param basicInfo basic info
   * @return new relation type
   */
  public static Relation newRelation(
      SPGTypeRef subjectTypeRef,
      SPGTypeRef objectTypeRef,
      BasicInfo<PredicateIdentifier> basicInfo) {
    Relation relation =
        new Relation(basicInfo, subjectTypeRef, objectTypeRef, false, new PropertyAdvancedConfig());
    relation.setProjectId(subjectTypeRef.getProjectId());
    return relation;
  }

  /**
   * Create a system default property.
   *
   * @param subjectTypeRef subject type ref
   * @param defaultPropertyEnum default property enum
   * @return property type
   */
  public static Property newProperty(
      SPGTypeRef subjectTypeRef, BuiltInPropertyEnum defaultPropertyEnum) {
    BasicInfo<PredicateIdentifier> basicInfo =
        new BasicInfo<>(
            new PredicateIdentifier(defaultPropertyEnum.getName()),
            defaultPropertyEnum.getNameZh(),
            defaultPropertyEnum.getDesc());
    SPGTypeRef objectTypeRef =
        new SPGTypeRef(
            new BasicInfo<>(SPGTypeIdentifier.parse(defaultPropertyEnum.getValueType())),
            SPGTypeEnum.toEnum(defaultPropertyEnum.getValueTypeEnum()));
    PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();

    if (defaultPropertyEnum.isMultiValue()) {
      BaseConstraintItem multiValueConst = new MultiValConstraint();
      Constraint constraint = new Constraint(null, Lists.newArrayList(multiValueConst));
      advancedConfig.setConstraint(constraint);
    }
    advancedConfig.setPropertyGroup(
        PropertyGroupEnum.toEnum(defaultPropertyEnum.getPropertyGroup()));

    Property property =
        new Property(basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);
    property.setProjectId(subjectTypeRef.getProjectId());
    return property;
  }

  /**
   * Generate a relation type by a property.
   *
   * @param property property type detail
   * @return relation type
   */
  public static Relation generateRelation(Property property) {
    BasicInfo<PredicateIdentifier> basicInfo = property.getBasicInfo();
    if (property.getSubjectTypeRef().isEventType()) {
      if (property.isSubjectProperty()) {
        basicInfo =
            new BasicInfo<>(
                new PredicateIdentifier(PropertyGroupEnum.SUBJECT.getNameEn()),
                basicInfo.getNameZh(),
                basicInfo.getDesc());
      } else if (property.isObjectProperty()) {
        basicInfo =
            new BasicInfo<>(
                new PredicateIdentifier(PropertyGroupEnum.OBJECT.getNameEn()),
                basicInfo.getNameZh(),
                basicInfo.getDesc());
      }
    }

    Relation newRelation =
        new Relation(
            basicInfo,
            property.getSubjectTypeRef(),
            property.getObjectTypeRef(),
            property.getInherited(),
            PropertyUtils.copyAdvancedConfig(property.toRef(), property.getAdvancedConfig()),
            true);
    newRelation.setProjectId(property.getProjectId());
    newRelation.setOntologyId(property.getOntologyId());
    newRelation.setAlterOperation(property.getAlterOperation());
    newRelation.setExtInfo(property.getExtInfo());
    return newRelation;
  }

  /**
   * Generate a new property type based on copying the parent class property type.
   *
   * @param subjectTypeRef child type ref
   * @param property parent type property
   * @return new property type
   */
  public static Property inheritProperty(SPGTypeRef subjectTypeRef, Property property) {
    PropertyRef propertyRef =
        new PropertyRef(
            subjectTypeRef,
            property.getBasicInfo(),
            property.getObjectTypeRef(),
            SPGOntologyEnum.PROPERTY,
            property.getProjectId(),
            property.getOntologyId());
    Property newProp =
        new Property(
            property.getBasicInfo(),
            subjectTypeRef,
            property.getObjectTypeRef(),
            true,
            copyAdvancedConfig(propertyRef, property.getAdvancedConfig()));
    newProp.setProjectId(property.getProjectId());
    newProp.setOntologyId(property.getOntologyId());
    newProp.setAlterOperation(property.getAlterOperation());
    newProp.setExtInfo(property.getExtInfo());
    return newProp;
  }

  /**
   * Generate a new property advanced config based on copying the given advanced config.
   *
   * @param propertyRef property type ref
   * @param advancedConfig the given property advanced config
   * @return the new property advanced config
   */
  public static PropertyAdvancedConfig copyAdvancedConfig(
      PropertyRef propertyRef, PropertyAdvancedConfig advancedConfig) {
    PropertyAdvancedConfig config = new PropertyAdvancedConfig();

    config.setMultiVersionConfig(advancedConfig.getMultiVersionConfig());
    config.setMountedConceptConfig(advancedConfig.getMountedConceptConfig());
    config.setEncryptTypeEnum(advancedConfig.getEncryptTypeEnum());
    config.setPropertyGroup(advancedConfig.getPropertyGroup());
    config.setConstraint(advancedConfig.getConstraint());
    config.setWithIndex(withIndex(propertyRef));
    if (CollectionUtils.isNotEmpty(advancedConfig.getSubProperties())) {
      config.setSubProperties(
          advancedConfig.getSubProperties().stream()
              .map(e -> copySubProperty(propertyRef, e))
              .collect(Collectors.toList()));
    }
    if (CollectionUtils.isNotEmpty(advancedConfig.getSemantics())) {
      config.setSemantics(
          advancedConfig.getSemantics().stream()
              .map(e -> copyPredicateSemantic(propertyRef, e))
              .collect(Collectors.toList()));
    }
    config.setLogicalRule(advancedConfig.getLogicalRule());
    return config;
  }

  private static SubProperty copySubProperty(PropertyRef subjectTypeRef, SubProperty subProperty) {
    SubProperty newSubProperty =
        new SubProperty(
            subProperty.getBasicInfo(),
            subjectTypeRef,
            subProperty.getObjectTypeRef(),
            subProperty.getAdvancedConfig());
    newSubProperty.setProjectId(subProperty.getProjectId());
    newSubProperty.setOntologyId(subProperty.getOntologyId());
    newSubProperty.setAlterOperation(subProperty.getAlterOperation());
    newSubProperty.setExtInfo(subProperty.getExtInfo());
    return newSubProperty;
  }

  private static PredicateSemantic copyPredicateSemantic(
      PropertyRef subjectTypeRef, PredicateSemantic semantic) {
    PredicateSemantic newSemantic =
        new PredicateSemantic(
            subjectTypeRef, semantic.getPredicateIdentifier(), semantic.getObjectTypeRef());
    newSemantic.setProjectId(semantic.getProjectId());
    newSemantic.setOntologyId(semantic.getOntologyId());
    newSemantic.setAlterOperation(semantic.getAlterOperation());
    newSemantic.setExtInfo(semantic.getExtInfo());
    return newSemantic;
  }

  private static boolean withIndex(PropertyRef propertyRef) {
    return SchemaConstants.CONCEPT_NAME_PROPERTY_NAME.equals(propertyRef.getName())
        && propertyRef.getSubjectTypeRef().isConceptType();
  }
}
