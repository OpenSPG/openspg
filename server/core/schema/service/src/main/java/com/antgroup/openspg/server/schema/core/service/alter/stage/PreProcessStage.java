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

package com.antgroup.openspg.server.schema.core.service.alter.stage;

import com.antgroup.openspg.server.schema.core.service.alter.check.SchemaAlterChecker;
import com.antgroup.openspg.server.schema.core.service.alter.check.SchemaCheckContext;
import com.antgroup.openspg.server.schema.core.service.alter.check.SchemaMap;
import com.antgroup.openspg.server.schema.core.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.server.schema.core.service.alter.stage.handler.BuiltInPropertyHandler;
import com.antgroup.openspg.server.schema.core.service.type.SPGTypeService;
import com.antgroup.openspg.server.schema.core.service.util.PropertyUtils;
import com.antgroup.openspg.schema.model.SchemaConstants;
import com.antgroup.openspg.schema.model.SchemaException;
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.schema.model.type.BaseSPGType;
import com.antgroup.openspg.schema.model.type.StandardType;
import com.antgroup.openspg.schema.model.type.WithAlterOperation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Build new schema stage, load online schema, build a new schema of project by merging alter draft.
 */
@Slf4j
public class PreProcessStage extends BaseAlterStage {

  public PreProcessStage() {
    super("pre-process-stage");
  }

  @Autowired private BuiltInPropertyHandler builtInPropertyHandler;
  @Autowired private SPGTypeService spgTypeService;

  @Override
  public void execute(SchemaAlterContext context) {
    SchemaAlterChecker checker = new SchemaAlterChecker();
    SchemaCheckContext checkContext =
        SchemaCheckContext.build(
            context.getProject(), context.getReleasedSchema(), context.getAlterSchema());

    this.completeInheritedProperty(checkContext);

    checker.check(checkContext);

    builtInPropertyHandler.handle(context);
  }

  private void completeInheritedProperty(SchemaCheckContext context) {
    List<BaseSPGType> createSpgTypes =
        context.getAlterTypes().stream()
            .filter(WithAlterOperation::isCreate)
            .collect(Collectors.toList());
    if (createSpgTypes.isEmpty()) {
      return;
    }

    SchemaMap onlineSchemaMap = context.getOnlineSchemaMap();
    SchemaMap alterSchemaMap = context.getAlterSchemaMap();
    Set<SPGTypeIdentifier> spreadStandardTypeIdentifiers =
        this.getSpreadStandardTypeIdentifiers(context);

    SPGTypeIdentifier rootIdentifier =
        SPGTypeIdentifier.parse(SchemaConstants.ROOT_TYPE_UNIQUE_NAME);
    BaseSPGType rootType = spgTypeService.querySPGTypeByIdentifier(rootIdentifier);

    for (BaseSPGType spgType : createSpgTypes) {
      List<BaseSPGType> parentTypes = new ArrayList<>();
      SPGTypeIdentifier parentTypeIdentifier =
          spgType.getParentTypeInfo().getParentTypeIdentifier();
      while (parentTypeIdentifier != null
          && !SchemaConstants.ROOT_TYPE_UNIQUE_NAME.equals(parentTypeIdentifier.toString())) {
        BaseSPGType parentType = alterSchemaMap.getSpgTypeMap().get(parentTypeIdentifier);
        if (parentType == null) {
          parentType = onlineSchemaMap.getSpgTypeMap().get(parentTypeIdentifier);
        }

        if (null == parentType || parentType.isDelete()) {
          throw SchemaException.spgTypeNotExist(parentTypeIdentifier.toString());
        }
        parentTypes.add(parentType);

        parentTypeIdentifier = parentType.getParentTypeInfo().getParentTypeIdentifier();
      }
      parentTypes.add(rootType);

      this.addInheritedProperty(spgType, parentTypes, spreadStandardTypeIdentifiers);
    }
  }

  private void addInheritedProperty(
      BaseSPGType spgType,
      List<BaseSPGType> parentTypes,
      Set<SPGTypeIdentifier> spreadStandardTypeIdentifiers) {
    for (BaseSPGType parentType : parentTypes) {
      if (CollectionUtils.isEmpty(parentType.getProperties())) {
        continue;
      }

      for (Property property : parentType.getProperties()) {
        if (Boolean.TRUE.equals(property.getInherited())) {
          continue;
        }

        Property newProp = PropertyUtils.inheritProperty(spgType.toRef(), property);
        spgType.getProperties().add(newProp);

        Relation relation =
            PropertyUtils.generateSemanticRelation(property, spreadStandardTypeIdentifiers);
        if (relation != null) {
          spgType.getRelations().add(relation);
        }
      }
    }
  }

  private Set<SPGTypeIdentifier> getSpreadStandardTypeIdentifiers(SchemaCheckContext context) {
    Set<SPGTypeIdentifier> spreadStandardTypeIdentifiers = new HashSet<>();
    for (BaseAdvancedType advancedType : context.getAlterTypes()) {
      if (advancedType.isStandardType() && !advancedType.isDelete()) {
        StandardType standardType = (StandardType) advancedType;
        if (Boolean.TRUE.equals(standardType.getSpreadable())) {
          spreadStandardTypeIdentifiers.add(standardType.getBaseSpgIdentifier());
        }
      }
    }

    for (BaseSPGType spgType : context.getOnlineTypes()) {
      if (spgType.isStandardType()
          && !spreadStandardTypeIdentifiers.contains(spgType.getBaseSpgIdentifier())) {
        StandardType standardType = (StandardType) spgType;
        if (Boolean.TRUE.equals(standardType.getSpreadable())) {
          spreadStandardTypeIdentifiers.add(standardType.getBaseSpgIdentifier());
        }
      }
    }
    return spreadStandardTypeIdentifiers;
  }
}
