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

package com.antgroup.openspg.server.core.schema.service.alter;

import com.antgroup.openspg.schema.model.SchemaConstants;
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.schema.model.type.BaseSPGType;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class SchemaAlterUtils {

  /**
   * Merge spg types online and spg types altered into a new list of spg type.
   *
   * @param onlineSchema list of spg type online
   * @param alterSchema list of spg type altered
   * @return
   */
  public static List<BaseSPGType> merge(
      List<BaseSPGType> onlineSchema, List<BaseAdvancedType> alterSchema) {
    List<BaseSPGType> newSchema = new ArrayList<>();
    Map<SPGTypeIdentifier, BaseSPGType> alterMap =
        alterSchema.stream()
            .collect(Collectors.toMap(BaseSPGType::getBaseSpgIdentifier, Function.identity()));
    for (BaseSPGType spgType : onlineSchema) {
      BaseSPGType advancedType = alterMap.getOrDefault(spgType.getBaseSpgIdentifier(), spgType);
      newSchema.add(advancedType);
    }

    for (BaseSPGType advancedType : alterSchema) {
      if (advancedType.isCreate()) {
        newSchema.add(advancedType);
      }
    }
    return newSchema;
  }

  /**
   * Sort the list of advanced type by inherit path.
   *
   * @param advancedTypes list of advanced type
   * @return list of advanced type has sorted
   */
  public static List<BaseAdvancedType> sortByInheritPath(List<BaseSPGType> advancedTypes) {
    final Map<SPGTypeIdentifier, BaseAdvancedType> advancedTypeAggMap =
        advancedTypes.stream()
            .filter(e -> e instanceof BaseAdvancedType)
            .map(e -> (BaseAdvancedType) e)
            .collect(Collectors.toMap(BaseAdvancedType::getBaseSpgIdentifier, Function.identity()));

    Stack<BaseAdvancedType> stack = new Stack<>();
    Set<SPGTypeIdentifier> visited = Sets.newHashSet();
    List<BaseAdvancedType> sortedTypes = new ArrayList<>(advancedTypes.size());

    advancedTypeAggMap
        .values()
        .forEach(
            advancedType -> {
              stack.push(advancedType);
              while (true) {
                if (SchemaConstants.ROOT_TYPE_UNIQUE_NAME.equalsIgnoreCase(advancedType.getName())
                    || advancedType.getParentTypeInfo() == null) {
                  break;
                }

                BaseAdvancedType parent =
                    advancedTypeAggMap.get(
                        advancedType.getParentTypeInfo().getParentTypeIdentifier());
                if (parent != null) {
                  stack.push(parent);
                  advancedType = parent;
                } else {
                  break;
                }
              }

              while (!stack.empty()) {
                BaseAdvancedType tw = stack.pop();
                SPGTypeIdentifier spgTypeIdentifier = tw.getBaseSpgIdentifier();
                if (!visited.contains(spgTypeIdentifier)) {
                  visited.add(spgTypeIdentifier);
                  sortedTypes.add(tw);
                }
              }
            });
    return sortedTypes;
  }

  /**
   * Find the spg type in the list by unique name.
   *
   * @param spgTypes list of spg type
   * @param spgTypeName unique name
   * @return spg type
   */
  public static BaseAdvancedType findSpgType(
      List<BaseAdvancedType> spgTypes, SPGTypeIdentifier spgTypeName) {
    if (CollectionUtils.isEmpty(spgTypes)) {
      return null;
    }

    for (BaseAdvancedType advancedType : spgTypes) {
      if (advancedType.getBaseSpgIdentifier().equals(spgTypeName)) {
        return advancedType;
      }
    }
    return null;
  }
}
