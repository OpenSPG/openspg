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

package com.antgroup.openspg.server.schema.core.service.alter.check;

import com.antgroup.openspg.common.model.base.BaseToString;
import com.antgroup.openspg.common.model.project.Project;
import com.antgroup.openspg.core.spgschema.model.SchemaConstants;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ConceptType;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.StandardType;
import com.antgroup.openspg.core.spgschema.model.type.WithBasicInfo;
import com.antgroup.openspg.core.spgschema.service.util.PropertyUtils;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Schema meta-information check context, holds all information that used during schema checking,
 * including project information, effective schema information, this changed schema information, and
 * Thing type default properties.
 */
public final class SchemaCheckContext extends BaseToString {

  private static final long serialVersionUID = -6925408674426546715L;

  /** Project information that used. */
  private final Project project;

  /** The list of spg type that is effective online. */
  private final List<BaseSPGType> onlineTypes;

  /** The list of spg type that is altered. */
  private final List<BaseAdvancedType> alterTypes;

  /** The schema container that built by onlineTypes. */
  private final SchemaMap onlineSchemaMap;

  /** The schema container that built by alterTypes. */
  private final SchemaMap alterSchemaMap;

  /**
   * Build schema checking context.
   *
   * @param project project that used
   * @param onlineSchema list of spg type that is effective
   * @param alterSchema list of spg type that altered
   * @return schema checking context.
   */
  public static SchemaCheckContext build(
      Project project, List<BaseSPGType> onlineSchema, List<BaseAdvancedType> alterSchema) {
    SchemaMap onlineSchemaMap = new SchemaMap();
    if (CollectionUtils.isNotEmpty(onlineSchema)) {
      onlineSchema.forEach(onlineSchemaMap::addSpgType);
    }
    SchemaMap alterSchemaMap = new SchemaMap();
    if (CollectionUtils.isNotEmpty(alterSchema)) {
      alterSchema.forEach(alterSchemaMap::addSpgType);
    }
    return new SchemaCheckContext(
        project, onlineSchema, alterSchema, onlineSchemaMap, alterSchemaMap);
  }

  private SchemaCheckContext(
      Project project,
      List<BaseSPGType> onlineTypes,
      List<BaseAdvancedType> alterTypes,
      SchemaMap onlineSchemaMap,
      SchemaMap alterSchemaMap) {
    this.project = project;
    this.onlineTypes = onlineTypes;
    this.alterTypes = alterTypes;
    this.onlineSchemaMap = onlineSchemaMap;
    this.alterSchemaMap = alterSchemaMap;
  }

  public Long getProjectId() {
    return project.getId();
  }

  public String getNamespace() {
    return project.getNamespace();
  }

  public SchemaMap getOnlineSchemaMap() {
    return onlineSchemaMap;
  }

  public SchemaMap getAlterSchemaMap() {
    return alterSchemaMap;
  }

  public Project getProject() {
    return project;
  }

  public List<BaseSPGType> getOnlineTypes() {
    return onlineTypes;
  }

  public List<BaseAdvancedType> getAlterTypes() {
    return alterTypes;
  }

  /**
   * If the spg type exists in online env or altered, except for the spg type that is deleted.
   *
   * @param spgTypeIdentifier identity of spg type
   * @return true or false
   */
  public boolean containSpgType(SPGTypeIdentifier spgTypeIdentifier) {
    if (SchemaConstants.ROOT_TYPE_UNIQUE_NAME.equals(spgTypeIdentifier.toString())) {
      return true;
    }

    BaseSPGType alterType = alterSchemaMap.getSpgTypeMap().get(spgTypeIdentifier);
    if (alterType != null) {
      if (alterType.isConceptType()) {
        ConceptType conceptType = (ConceptType) alterType;
        if (conceptType.isTaxonomicConcept()) {
          return true;
        }
      }
      return !alterType.isDelete();
    }
    return onlineSchemaMap.getSpgTypeMap().containsKey(spgTypeIdentifier);
  }

  /**
   * Get spg type that has existed online by name
   *
   * @param spgTypeName name of spg type
   * @return spg type
   */
  public BaseAdvancedType getExitType(SPGTypeIdentifier spgTypeName) {
    return (BaseAdvancedType) onlineSchemaMap.getSpgTypeMap().get(spgTypeName);
  }

  public List<BaseSPGType> getMergedSchema() {
    Set<SPGTypeIdentifier> spreadableStdTypeNames =
        Streams.concat(onlineTypes.stream(), alterTypes.stream())
            .filter(e -> !e.isDelete() && SchemaCheckContext.isSpreadableStdType(e))
            .map(WithBasicInfo::getBaseSpgIdentifier)
            .collect(Collectors.toSet());

    List<BaseSPGType> newSchema = new ArrayList<>();
    Map<SPGTypeIdentifier, BaseSPGType> alterMap =
        alterTypes.stream()
            .collect(Collectors.toMap(BaseSPGType::getBaseSpgIdentifier, Function.identity()));
    for (BaseSPGType spgType : onlineTypes) {
      if (!alterMap.containsKey(spgType.getBaseSpgIdentifier())) {
        newSchema.add(spgType);
      } else {
        newSchema.add(
            this.getAlterType(
                alterMap.get(spgType.getBaseSpgIdentifier()), spreadableStdTypeNames));
      }
    }

    for (BaseSPGType spgType : alterTypes) {
      if (spgType.isCreate()) {
        newSchema.add(
            this.getAlterType(
                alterMap.get(spgType.getBaseSpgIdentifier()), spreadableStdTypeNames));
      }
    }
    return newSchema;
  }

  private BaseSPGType getAlterType(
      BaseSPGType spgType, Set<SPGTypeIdentifier> spreadableStdTypeNames) {
    if (CollectionUtils.isEmpty(spgType.getProperties())) {
      return spgType;
    }

    List<Relation> relations = spgType.getRelations();
    if (relations == null) {
      relations = new ArrayList<>();
    }

    Set<SPGTripleIdentifier> relationIdentifiers =
        relations.stream().map(Property::getSpgTripleName).collect(Collectors.toSet());
    for (Property property : spgType.getProperties()) {
      if (!relationIdentifiers.contains(property.getSpgTripleName())
          && isSemanticProperty(property, spreadableStdTypeNames)) {
        relations.add(PropertyUtils.generateRelation(property));
      }
    }
    spgType.setRelations(relations);
    return spgType;
  }

  private static boolean isSemanticProperty(
      Property property, Set<SPGTypeIdentifier> spreadableStdTypeNames) {
    SPGTypeRef objectTypeRef = property.getObjectTypeRef();
    if (SPGTypeEnum.BASIC_TYPE.equals(objectTypeRef.getSpgTypeEnum())) {
      return false;
    }

    if (SPGTypeEnum.STANDARD_TYPE.equals(objectTypeRef.getSpgTypeEnum())) {
      return spreadableStdTypeNames.contains(objectTypeRef.getBasicInfo().getName());
    }
    return true;
  }

  private static boolean isSpreadableStdType(BaseSPGType advancedType) {
    return advancedType instanceof StandardType && ((StandardType) advancedType).getSpreadable();
  }
}
