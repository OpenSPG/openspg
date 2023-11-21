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

package com.antgroup.openspg.core.spgschema.service.alter.check;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.ConceptType;

public class ConceptTypeChecker extends BaseSpgTypeChecker {

  @Override
  public void checkAdvancedConfig(BaseAdvancedType advancedType, SchemaCheckContext context) {
    ConceptType conceptType = (ConceptType) advancedType;
    String schemaTypeName = conceptType.getName();

    OperatorChecker.check(schemaTypeName, conceptType.getAdvancedConfig().getNormalizedOperator());
    OperatorChecker.check(schemaTypeName, conceptType.getAdvancedConfig().getFuseOperator());

    if (null == conceptType.getConceptLayerConfig()) {
      throw new IllegalArgumentException(
          String.format("conceptLayerConfig of %s is null", schemaTypeName));
    }
    if (StringUtils.isBlank(conceptType.getConceptLayerConfig().getHypernymPredicate())) {
      throw new IllegalArgumentException(
          String.format("hypernymPredicate of %s is blank", schemaTypeName));
    }
    if (null != conceptType.getConceptTaxonomicConfig()) {
      SPGTypeIdentifier taxonomicTypeIdentifier =
          conceptType.getConceptTaxonomicConfig().getTaxonomicTypeIdentifier();
      if (taxonomicTypeIdentifier != null && !context.containSpgType(taxonomicTypeIdentifier)) {
        throw new IllegalArgumentException(
            String.format("taxonomic type: %s not exists", taxonomicTypeIdentifier));
      }
    }
  }
}
