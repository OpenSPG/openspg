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

package com.antgroup.openspg.server.core.schema.service.alter.check;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.DslSyntaxError;
import com.antgroup.openspg.core.schema.model.SchemaConstants;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.type.*;
import com.antgroup.openspg.core.schema.model.type.BasicType.TextBasicType;
import com.antgroup.openspg.reasoner.catalog.impl.OpenSPGCatalog;
import com.antgroup.openspg.reasoner.lube.block.Block;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext;
import com.antgroup.openspg.reasoner.lube.logical.validate.Validator;
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface;
import com.antgroup.openspg.reasoner.parser.KgDslParser;
import com.antgroup.openspg.server.core.schema.service.type.model.BuiltInPropertyEnum;
import com.google.common.collect.Lists;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import scala.collection.JavaConversions;

/**
 * Check the information of property type in spg type is legal, the list of property in spg type
 * should contain all properties that existed, and the name of property type need compliant with
 * agreed format.
 */
public class PropertyChecker {

  protected static final String PATTERN = "^[a-z][0-9a-zA-Z]*";

  protected static final Pattern PROPERTY_NAME_PATTERN = Pattern.compile(PATTERN);

  protected static SubPropertyChecker subPropertyChecker = new SubPropertyChecker();

  /**
   * check if properties of input advanced type is legal.
   *
   * @param advancedType the altered spg type
   * @param context check context
   */
  public void check(BaseAdvancedType advancedType, SchemaCheckContext context) {
    SPGTypeIdentifier spgTypeIdentifier = advancedType.getBaseSpgIdentifier();

    if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
      List<Property> logicalProperties = new ArrayList<>();
      for (Property property : advancedType.getProperties()) {
        if (property.isDelete()) {
          continue;
        }

        this.checkBasicInfo(spgTypeIdentifier, property, context);
        this.checkBuiltInProperty(advancedType.getSpgTypeEnum(), property);
        this.checkConstraint(property);

        if (property.getLogicalRule() != null) {
          logicalProperties.add(property);
        }
        subPropertyChecker.check(property);
      }

      if (!logicalProperties.isEmpty()) {
        this.checkLogicalProperty(logicalProperties, context);
      }
      this.checkNameDuplicated(advancedType.getProperties());
    }

    if (advancedType.isUpdate()) {
      this.checkContainExistProp(advancedType, context);
    }
  }

  protected void checkBasicInfo(
      SPGTypeIdentifier spgTypeIdentifier, Property property, SchemaCheckContext context) {
    if (null == property.getBasicInfo()) {
      throw new IllegalArgumentException(
          String.format("property/relation:%s basic info can not be null", spgTypeIdentifier));
    }
    if (null == property.getName()) {
      throw new IllegalArgumentException(
          String.format("property/relation:%s name can not be null", spgTypeIdentifier));
    }
    if (null == property.getBasicInfo().getNameZh()) {
      throw new IllegalArgumentException(
          String.format(
              "property/relation: %s nameZh can not be null", property.getBasicInfo().getNameZh()));
    }
    if (!PROPERTY_NAME_PATTERN.matcher(property.getName()).matches()) {
      throw new IllegalArgumentException(
          String.format(
              "the pattern of property/relation name: %s not match: %s",
              property.getName(), PATTERN));
    }
    if (property.getName().length() > SchemaConstants.SCHEMA_PROPERTY_MAX_NAME) {
      throw new IllegalArgumentException(
          String.format(
              "the length of property/relation's name: %s can not be larger than: %s",
              property.getName(), SchemaConstants.SCHEMA_PROPERTY_MAX_NAME));
    }
    if (property.getBasicInfo().getNameZh().length()
        > SchemaConstants.SCHEMA_PROPERTY_MAX_NAME_ZH) {
      throw new IllegalArgumentException(
          String.format(
              "the length of property/relation's nameZh: %s can not be larger than: %s",
              property.getBasicInfo().getNameZh(), SchemaConstants.SCHEMA_PROPERTY_MAX_NAME_ZH));
    }
    if (null == property.getObjectTypeRef()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeRef of property/relation: %s can not be null",
              property.getBasicInfo().getName()));
    }
    if (null == property.getObjectTypeRef().getBasicInfo()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeRef.basicInfo of property/relation: %s can not be null",
              property.getBasicInfo().getName()));
    }
    if (null == property.getObjectTypeRef().getBaseSpgIdentifier()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeRef.typeName of property/relation: %s can not be null",
              property.getBasicInfo().getName()));
    }
    if (!context.containSpgType(property.getObjectTypeRef().getBaseSpgIdentifier())) {
      throw new IllegalArgumentException(
          String.format(
              "property/relation: %s depends on type: %s, but not exist",
              property.getBasicInfo().getName(), property.getObjectTypeRef().getName()));
    }
    if (null == property.getObjectTypeRef().getSpgTypeEnum()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeEnum of property/relation: %s can not be null",
              property.getBasicInfo().getName()));
    }
  }

  private void checkLogicalProperty(List<Property> logicalProperties, SchemaCheckContext context) {
    Catalog catalog = this.buildCatalog(context);

    logicalProperties.forEach(
        property -> {
          String dsl = property.getLogicalRule().getContent();
          if (StringUtils.isBlank(dsl)) {
            return;
          }
          this.checkDSL(dsl, catalog);
        });
  }

  protected Catalog buildCatalog(SchemaCheckContext context) {
    List<BaseSPGType> spgTypes = context.getMergedSchema();
    ProjectSchema projectSchema = new ProjectSchema(spgTypes);
    Catalog catalog = new OpenSPGCatalog(context.getProjectId(), null, projectSchema);
    catalog.init();
    return catalog;
  }

  protected void checkDSL(String dsl, Catalog catalog) {
    try {
      ParserInterface parser = new KgDslParser();
      List<Block> blocks =
          Lists.newArrayList(
              JavaConversions.asJavaCollection(
                  parser.parseMultipleStatement(dsl, new scala.collection.immutable.HashMap<>())));
      LogicalPlannerContext context =
          new LogicalPlannerContext(catalog, parser, new scala.collection.immutable.HashMap<>());
      for (Block block : blocks) {
        Validator.validate(parser, block, context);
      }
    } catch (Exception e) {
      throw DslSyntaxError.dslSyntaxError(e);
    }
  }

  protected void checkBuiltInProperty(SPGTypeEnum spgTypeEnum, Property property) {
    if (property.getAlterOperation() == null) {
      return;
    }

    Set<String> builtInPropertyNames = BuiltInPropertyEnum.getBuiltInPropertyName(spgTypeEnum);
    if (builtInPropertyNames.contains(property.getName())) {
      throw new IllegalArgumentException(
          String.format(
              "property: %s is defined by system, no need to define or alter", property.getName()));
    }
  }

  private void checkConstraint(Property property) {
    if (property.getObjectTypeRef().isBasicType()
        && !(BasicType.from(property.getObjectTypeRef().getName()) instanceof TextBasicType)) {
      CommonChecker.containForbiddenConstraintType(
          property.getConstraint(), ConstraintTypeEnum.MULTI_VALUE);
    }
  }

  private void checkNameDuplicated(List<Property> propertyList) {
    Set<String> names = new HashSet<>();
    for (Property property : propertyList) {
      if (property.isDelete()) {
        continue;
      }

      String name = property.getName();
      if (names.contains(name)) {
        throw new IllegalArgumentException(String.format("property name: %s is duplicated", name));
      }
      names.add(name);
    }
  }

  private void checkContainExistProp(BaseAdvancedType advancedType, SchemaCheckContext context) {
    List<Property> existProperties =
        context.getExitType(advancedType.getBaseSpgIdentifier()).getProperties();
    if (CollectionUtils.isEmpty(existProperties)) {
      return;
    }

    Set<String> names = this.getAllPropertyName(advancedType.getProperties());
    for (Property existProp : existProperties) {
      if (!names.contains(existProp.getName())) {
        throw new IllegalArgumentException(
            String.format("missing property: %s", existProp.getSpgTripleName()));
      }
    }
  }

  private Set<String> getAllPropertyName(List<Property> propertyList) {
    Set<String> names = new HashSet<>();
    for (Property property : propertyList) {
      String name = property.getName();
      names.add(name);
    }
    return names;
  }
}
