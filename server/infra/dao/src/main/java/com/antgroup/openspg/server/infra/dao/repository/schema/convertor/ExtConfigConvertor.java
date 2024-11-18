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

package com.antgroup.openspg.server.infra.dao.repository.schema.convertor;

import com.antgroup.openspg.core.schema.model.SchemaConstants;
import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.server.api.facade.SchemaJsonUtils;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.core.schema.service.type.model.SimpleSPGType;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class ExtConfigConvertor {

  public static String getExtConfig(SimpleSPGType advancedType) {
    SchemaExtInfo extInfo = advancedType.getExtInfo();
    if (extInfo == null) {
      extInfo = new SchemaExtInfo();
    }

    switch (advancedType.getSpgTypeEnum()) {
      case STANDARD_TYPE:
        if (CollectionUtils.isNotEmpty(advancedType.getConstraintItems())) {
          extInfo.put(
              SchemaConstants.STANDARD_CONSTRAINT_KEY,
              advancedType.getConstraintItems().stream()
                  .map(ConstraintItemConfigDOConvertor::toConfigDO)
                  .collect(Collectors.toList()));
        }
        extInfo.put(SchemaConstants.SPREADABLE, Boolean.TRUE.equals(advancedType.getSpreadable()));
        break;
      case CONCEPT_TYPE:
        extInfo.put(
            SchemaConstants.CONCEPT_TAXONOMIC_KEY, advancedType.getConceptTaxonomicConfig());
        extInfo.put(SchemaConstants.CONCEPT_LAYER_KEY, advancedType.getConceptLayerConfig());
        extInfo.put(
            SchemaConstants.MULTI_VERSION_CONFIG_KEY, advancedType.getConceptMultiVersionConfig());
        break;
      default:
        break;
    }
    return SchemaJsonUtils.serialize(extInfo);
  }

  public static String getExtConfig(SimpleProperty simplePredicate) {
    SchemaExtInfo extInfo = simplePredicate.getExtInfo();
    if (extInfo == null) {
      extInfo = new SchemaExtInfo();
    }

    if (simplePredicate.getRuleCode() != null) {
      extInfo.put(
          SchemaConstants.PROPERTY_RULE_CONFIG_KEY, simplePredicate.getRuleCode().getCode());
    }
    if (simplePredicate.getMountedConceptConfig() != null) {
      extInfo.put(
          SchemaConstants.MOUNT_CONCEPT_CONFIG_KEY, simplePredicate.getMountedConceptConfig());
    }
    if (simplePredicate.getPropertyGroup() != null) {
      extInfo.put(SchemaConstants.PROPERTY_GROUP_KEY, simplePredicate.getPropertyGroup().name());
    }
    extInfo.put(SchemaConstants.VALUE_TYPE_KEY, simplePredicate.getObjectTypeEnum().name());
    return SchemaJsonUtils.serialize(extInfo);
  }

  public static <T> T get(SchemaExtInfo schemaExtInfo, String key, Class<T> clazz) {
    if (null == schemaExtInfo) {
      return null;
    }
    if (!schemaExtInfo.containsKey(key)) {
      return null;
    }
    return SchemaJsonUtils.deserialize(schemaExtInfo.get(key).toString(), clazz);
  }
}
