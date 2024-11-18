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

package com.antgroup.openspg.server.core.schema.service.alter.check;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public class RelationChecker extends PropertyChecker {

  @Override
  public void check(BaseAdvancedType advancedType, SchemaCheckContext context) {
    SPGTypeIdentifier spgTypeIdentifier = advancedType.getBaseSpgIdentifier();
    if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
      List<Relation> logicalRelations = new ArrayList<>();
      for (Relation relation : advancedType.getRelations()) {
        if (relation.isDelete() || Boolean.TRUE.equals(relation.isSemanticRelation())) {
          continue;
        }

        this.checkBasicInfo(spgTypeIdentifier, relation, context);
        if (relation.getLogicalRule() != null) {
          logicalRelations.add(relation);
        }
        subPropertyChecker.check(relation);
      }

      if (!logicalRelations.isEmpty()) {
        this.checkLogicalRelation(logicalRelations, context);
      }
      this.checkRelationNameDuplicated(advancedType.getRelations());
    }

    if (advancedType.isUpdate()) {
      this.checkContainExistRelation(advancedType, context);
    }
  }

  private void checkLogicalRelation(List<Relation> ruleRelations, SchemaCheckContext context) {
    Catalog catalog = this.buildCatalog(context);

    ruleRelations.forEach(
        relation -> {
          String dsl = relation.getLogicalRule().getContent();
          if (StringUtils.isBlank(dsl)) {
            return;
          }
          this.checkDSL(dsl, catalog);
        });
  }

  private void checkRelationNameDuplicated(List<Relation> relations) {
    Set<String> names = new HashSet<>();
    for (Relation relation : relations) {
      if (relation.isDelete() || Boolean.TRUE.equals(relation.isSemanticRelation())) {
        continue;
      }

      String name = relation.getSpgTripleName().toString();
      if (names.contains(name)) {
        throw new IllegalArgumentException(String.format("relation name: %s is duplicated", name));
      }
      names.add(name);
    }
  }

  private void checkContainExistRelation(
      BaseAdvancedType advancedType, SchemaCheckContext context) {
    List<Relation> existRelations =
        context.getExitType(advancedType.getBaseSpgIdentifier()).getRelations();
    if (CollectionUtils.isEmpty(existRelations)) {
      return;
    }

    Set<String> names = this.getAllRelationName(advancedType.getRelations());
    for (Relation existRelation : existRelations) {
      if (!Boolean.TRUE.equals(existRelation.isSemanticRelation())
          && !names.contains(existRelation.getName())) {
        throw new IllegalArgumentException(
            String.format("missing relation: %s", existRelation.getSpgTripleName()));
      }
    }
  }

  private Set<String> getAllRelationName(List<Relation> relations) {
    Set<String> names = new HashSet<>();
    for (Relation relation : relations) {
      names.add(relation.getName());
    }
    return names;
  }
}
