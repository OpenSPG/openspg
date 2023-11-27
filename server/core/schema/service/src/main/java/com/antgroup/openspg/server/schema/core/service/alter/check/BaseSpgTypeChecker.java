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

import com.antgroup.openspg.server.core.schema.model.BasicInfo;
import com.antgroup.openspg.server.core.schema.model.SchemaConstants;
import com.antgroup.openspg.server.core.schema.model.SchemaException;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.server.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.core.schema.model.type.StandardType;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The base class of spg type checker, defines the entry method {@code check} for checking whether a
 * type structure is legal and the implementation of the main process. Each spg type such as
 * StandardType, EntityType, ConceptType and EventType needs to inherit this class and implement the
 * abstract method to verify their unique attributes and logic of each type.
 */
public abstract class BaseSpgTypeChecker {

  /** Type name constraint regular expression. */
  private static final String CAPITALIZE_NAME_REG_EXP = "^[A-Z][a-zA-Z0-9]*";

  /** The checker of property type. */
  private final PropertyChecker propertyChecker = new PropertyChecker();

  /** The checker of relation type. */
  private final RelationChecker relationChecker = new RelationChecker();

  /**
   * Check if the structure of BaseAdvancedType altered is valid.
   *
   * @param advancedType the advanced type that altered
   * @param context the information during check
   */
  public void check(BaseAdvancedType advancedType, SchemaCheckContext context) {
    this.checkBaseInfo(advancedType, context);
    if (advancedType.isDelete()) {
      return;
    }

    this.checkAdvancedConfig(advancedType, context);

    if (!advancedType.isDelete()) {
      propertyChecker.check(advancedType, context);
      relationChecker.check(advancedType, context);
    }
  }

  private void checkBaseInfo(BaseAdvancedType advancedType, SchemaCheckContext context) {
    this.checkBasicInfo(advancedType, context);
    this.checkParentInfo(advancedType, context);
    this.checkAlterOperation(advancedType, context);
  }

  private void checkBasicInfo(BaseAdvancedType advancedType, SchemaCheckContext context) {
    BasicInfo<SPGTypeIdentifier> basicInfo = advancedType.getBasicInfo();
    SPGTypeIdentifier spgTypeIdentifier = basicInfo.getName();
    if (!advancedType.isStandardType()
        && !Objects.equals(spgTypeIdentifier.getNamespace(), context.getNamespace())) {
      throw new IllegalArgumentException(
          String.format(
              "type name: %s not match project namespace: %s",
              spgTypeIdentifier, context.getNamespace()));
    }
    if (advancedType.isStandardType()
        && !StandardType.STD_NAMESPACE.equals(spgTypeIdentifier.getNamespace())) {
      throw new IllegalArgumentException(
          String.format("namespace of standard type must be: %s", StandardType.STD_NAMESPACE));
    }
    if (spgTypeIdentifier.toString().length() > SchemaConstants.SCHEMA_SPG_TYPE_MAX_NAME) {
      throw new IllegalArgumentException(
          String.format(
              "length of type name: %s can not be longer than %s",
              spgTypeIdentifier, SchemaConstants.SCHEMA_SPG_TYPE_MAX_NAME));
    }
    if (!capitalizeEntityTypeName(spgTypeIdentifier.getNameEn())) {
      throw new IllegalArgumentException(
          String.format("type name:%s not match: %s", spgTypeIdentifier, CAPITALIZE_NAME_REG_EXP));
    }
    if (StringUtils.isBlank(basicInfo.getNameZh())) {
      throw new IllegalArgumentException(
          String.format("nameZh of type: %s can not be blank", spgTypeIdentifier));
    }
    if (basicInfo.getNameZh().length() > SchemaConstants.SCHEMA_SPG_TYPE_MAX_NAME_ZH) {
      throw new IllegalArgumentException(
          String.format(
              "nameZh of type: %s can not be longer than %s",
              spgTypeIdentifier, SchemaConstants.SCHEMA_SPG_TYPE_MAX_NAME_ZH));
    }
    if (StringUtils.isNotBlank(basicInfo.getDesc())
        && basicInfo.getDesc().length() > SchemaConstants.SCHEMA_SPG_TYPE_MAX_DESCRIPTION) {
      throw new IllegalArgumentException(
          String.format(
              "desc of type: %s can not be longer than %s",
              spgTypeIdentifier, SchemaConstants.SCHEMA_SPG_TYPE_MAX_DESCRIPTION));
    }
  }

  private static Boolean capitalizeEntityTypeName(String name) {
    Pattern pattern = Pattern.compile(CAPITALIZE_NAME_REG_EXP);
    Matcher matcher = pattern.matcher(name);
    return matcher.matches();
  }

  private void checkParentInfo(BaseAdvancedType advancedType, SchemaCheckContext context) {
    if (null == advancedType.getParentTypeInfo() || null == advancedType.getParentTypeName()) {
      throw new IllegalArgumentException(
          String.format("parent type of: %s can not be null", advancedType.getBaseSpgIdentifier()));
    }

    if (!advancedType.isDelete() && !context.containSpgType(advancedType.getParentTypeName())) {
      throw new IllegalArgumentException(
          String.format("parent type: %s not exist", advancedType.getParentTypeName()));
    }
  }

  private void checkAlterOperation(BaseAdvancedType advancedType, SchemaCheckContext context) {
    if (null == advancedType.getAlterOperation()) {
      throw new IllegalArgumentException("alter operation can not be null");
    }

    Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap = context.getOnlineSchemaMap().getSpgTypeMap();
    switch (advancedType.getAlterOperation()) {
      case CREATE:
        if (spgTypeMap.containsKey(advancedType.getBaseSpgIdentifier())) {
          throw SchemaException.spgTypeAlreadyExists(advancedType.getName());
        }
        break;
      case UPDATE:
      case DELETE:
        if (!spgTypeMap.containsKey(advancedType.getBaseSpgIdentifier())) {
          throw SchemaException.spgTypeNotExist(advancedType.getName());
        }
        break;
      default:
        break;
    }

    this.setPropertyAlterOperation(advancedType);
    this.setRelationAlterOperation(advancedType);
  }

  private void setPropertyAlterOperation(BaseAdvancedType advancedType) {
    if (CollectionUtils.isEmpty(advancedType.getProperties())) {
      return;
    }

    advancedType
        .getProperties()
        .forEach(
            property -> {
              if (Boolean.TRUE.equals(property.getInherited())) {
                return;
              }

              property.setProjectId(advancedType.getProjectId());
              if (!advancedType.isUpdate()) {
                property.setAlterOperation(advancedType.getAlterOperation());
              }

              if (CollectionUtils.isNotEmpty(property.getSubProperties())) {
                property
                    .getSubProperties()
                    .forEach(
                        subProperty -> {
                          subProperty.setProjectId(property.getProjectId());
                          if (!property.isUpdate()) {
                            subProperty.setAlterOperation(property.getAlterOperation());
                          }
                        });
              }

              if (CollectionUtils.isNotEmpty(property.getSemantics())) {
                property
                    .getSemantics()
                    .forEach(
                        semantic -> {
                          if (!property.isUpdate()) {
                            semantic.setAlterOperation(property.getAlterOperation());
                          }
                        });
              }
            });
  }

  private void setRelationAlterOperation(BaseAdvancedType advancedType) {
    if (CollectionUtils.isEmpty(advancedType.getRelations())) {
      return;
    }

    advancedType
        .getRelations()
        .forEach(
            relation -> {
              if (Boolean.TRUE.equals(relation.getInherited())) {
                return;
              }

              if (!advancedType.isUpdate()) {
                relation.setAlterOperation(advancedType.getAlterOperation());
              }

              relation.setProjectId(advancedType.getProjectId());
              if (CollectionUtils.isNotEmpty(relation.getSubProperties())) {
                relation
                    .getSubProperties()
                    .forEach(
                        subProperty -> {
                          subProperty.setProjectId(relation.getProjectId());
                          if (!relation.isUpdate()) {
                            subProperty.setAlterOperation(relation.getAlterOperation());
                          }
                        });
              }

              if (CollectionUtils.isNotEmpty(relation.getAdvancedConfig().getSemantics())) {
                relation
                    .getAdvancedConfig()
                    .getSemantics()
                    .forEach(
                        semantic -> {
                          if (!relation.isUpdate()) {
                            semantic.setAlterOperation(relation.getAlterOperation());
                          }
                        });
              }
            });
  }

  /**
   * Verify whether the advanced configuration of the type is legal
   *
   * @param advancedType the advanced type that altered
   * @param context the information during check
   */
  public abstract void checkAdvancedConfig(
      BaseAdvancedType advancedType, SchemaCheckContext context);
}
