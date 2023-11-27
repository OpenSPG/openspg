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

package com.antgroup.openspg.server.schema.core.service.type.convertor;

import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType;
import com.antgroup.openspg.core.spgschema.service.util.PropertyUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/** Assemble SPG type. */
public class SPGTypeAssemble {

  public static List<BaseSPGType> assemble(
      List<SimpleSPGType> simpleSpgTypes,
      List<Property> propertyList,
      List<Relation> relationLit,
      Set<SPGTypeIdentifier> spreadStdTypeNames) {
    Map<Long, List<Property>> entityPropertyMap =
        propertyList.stream()
            .collect(Collectors.groupingBy(e -> e.getSubjectTypeRef().getUniqueId()));
    Map<Long, List<Relation>> entityRelationMap =
        relationLit.stream()
            .collect(Collectors.groupingBy(e -> e.getSubjectTypeRef().getUniqueId()));

    List<BaseSPGType> advancedTypes = new ArrayList<>();
    for (SimpleSPGType simpleSpgType : simpleSpgTypes) {
      List<Property> propertys = getPropertiesOfType(simpleSpgType, entityPropertyMap);
      List<Relation> relations = getRelationsOfType(simpleSpgType, entityRelationMap);

      generateSemanticRelation(propertys, relations, spreadStdTypeNames);
      advancedTypes.add(SPGTypeConvertor.toBaseSpgType(simpleSpgType, propertys, relations));
    }
    return advancedTypes;
  }

  private static List<Property> getPropertiesOfType(
      SimpleSPGType simpleSpgType, Map<Long, List<Property>> entityPropertyMap) {
    if (simpleSpgType.isBasicType()) {
      return Collections.emptyList();
    }

    List<Property> typeProperties = new ArrayList<>();
    if (simpleSpgType.getParentTypeInfo() == null) {
      List<Property> propertys = entityPropertyMap.get(simpleSpgType.getUniqueId());
      if (CollectionUtils.isNotEmpty(propertys)) {
        typeProperties.addAll(propertys);
      }
      return typeProperties;
    }

    List<Long> inheritPath = simpleSpgType.getParentTypeInfo().getInheritPath();
    SPGTypeRef subjectTypeRef = simpleSpgType.toRef();
    for (Long entityId : inheritPath) {
      if (!entityPropertyMap.containsKey(entityId)) {
        continue;
      }
      for (Property property : entityPropertyMap.get(entityId)) {
        Property p =
            entityId.equals(simpleSpgType.getUniqueId())
                ? property
                : PropertyUtils.inheritProperty(subjectTypeRef, property);
        typeProperties.add(p);
      }
    }
    return typeProperties;
  }

  private static List<Relation> getRelationsOfType(
      SimpleSPGType simpleSpgType, Map<Long, List<Relation>> entityRelationMap) {
    List<Relation> typeRelations = new ArrayList<>();
    if (entityRelationMap.containsKey(simpleSpgType.getUniqueId())) {
      typeRelations.addAll(entityRelationMap.get(simpleSpgType.getUniqueId()));
    }
    return typeRelations;
  }

  private static void generateSemanticRelation(
      List<Property> propertys,
      List<Relation> relations,
      Set<SPGTypeIdentifier> spreadStdTypeNames) {
    for (Property property : propertys) {
      Relation relation = PropertyUtils.generateSemanticRelation(property, spreadStdTypeNames);
      if (null != relation) {
        relations.add(relation);
      }
    }
  }
}
