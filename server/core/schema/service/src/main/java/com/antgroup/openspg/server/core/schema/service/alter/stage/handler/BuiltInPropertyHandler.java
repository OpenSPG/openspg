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

package com.antgroup.openspg.server.core.schema.service.alter.stage.handler;

import com.antgroup.openspg.server.core.schema.service.alter.SchemaAlterUtils;
import com.antgroup.openspg.server.core.schema.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import com.antgroup.openspg.server.core.schema.service.type.model.BuiltInPropertyEnum;
import com.antgroup.openspg.server.core.schema.service.util.PropertyUtils;
import com.antgroup.openspg.schema.model.BasicInfo;
import com.antgroup.openspg.schema.model.SchemaConstants;
import com.antgroup.openspg.schema.model.SchemaException;
import com.antgroup.openspg.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.schema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.schema.model.type.BaseSPGType;
import com.antgroup.openspg.schema.model.type.ConceptType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuiltInPropertyHandler {

  @Autowired private SPGTypeService spgTypeService;

  public void handle(SchemaAlterContext context) {
    for (BaseAdvancedType advancedType : new ArrayList<>(context.getAlterSchema())) {
      if (!SchemaConstants.ROOT_TYPE_UNIQUE_NAME.equals(
          advancedType.getParentTypeInfo().getParentTypeIdentifier().toString())) {
        continue;
      }
      if (advancedType.isCreate()) {
        this.addBuiltInProperty(advancedType);
      }

      if (advancedType.isConceptType()) {
        this.addConceptConfig((ConceptType) advancedType, context.getAlterSchema());
      }
    }
  }

  private void addBuiltInProperty(BaseAdvancedType advancedType) {
    List<BuiltInPropertyEnum> builtInProperties =
        BuiltInPropertyEnum.getBuiltInProperty(advancedType.getSpgTypeEnum());
    for (BuiltInPropertyEnum builtInPropertyEnum : builtInProperties) {
      Property property = PropertyUtils.newProperty(advancedType.toRef(), builtInPropertyEnum);
      property.setAlterOperation(AlterOperationEnum.CREATE);
      advancedType.getProperties().add(property);
    }
  }

  private void addConceptConfig(ConceptType conceptType, List<BaseAdvancedType> alterSchema) {
    if (conceptType.isCreate()) {
      this.addHypernymRelation(conceptType);
      if (conceptType.isTaxonomicConcept()) {
        this.addTaxonomicRelation(conceptType, alterSchema);
      }
    } else if (conceptType.isDelete()) {
      if (conceptType.isTaxonomicConcept()) {
        this.deleteTaxonomicRelation(conceptType, alterSchema);
      }
    }
  }

  private void addHypernymRelation(ConceptType conceptType) {
    String relationName = conceptType.getConceptLayerConfig().getHypernymPredicate();
    SystemPredicateEnum systemPredicate = SystemPredicateEnum.toEnum(relationName);
    BasicInfo<PredicateIdentifier> basicInfo =
        new BasicInfo<>(
            new PredicateIdentifier(systemPredicate.getName()),
            systemPredicate.getNameZh(),
            systemPredicate.getNameZh());
    Relation relation =
        PropertyUtils.newRelation(conceptType.toRef(), conceptType.toRef(), basicInfo);
    relation.setAlterOperation(AlterOperationEnum.CREATE);
    conceptType.getRelations().add(relation);
  }

  private void addTaxonomicRelation(ConceptType conceptType, List<BaseAdvancedType> alterTypes) {
    BasicInfo<PredicateIdentifier> basicInfo =
        new BasicInfo<>(
            new PredicateIdentifier(SystemPredicateEnum.BELONG_TO.getName()),
            SystemPredicateEnum.BELONG_TO.getNameZh(),
            null);
    BaseSPGType taxonomicType = this.getTaxonomicType(conceptType, alterTypes);
    Property taxonomicProperty =
        PropertyUtils.newProperty(taxonomicType.toRef(), conceptType.toRef(), basicInfo, false);
    taxonomicProperty.setAlterOperation(AlterOperationEnum.CREATE);
    taxonomicType.getProperties().add(taxonomicProperty);

    if (taxonomicType.getAlterOperation() == null) {
      taxonomicType.setAlterOperation(AlterOperationEnum.UPDATE);
      alterTypes.listIterator().add((BaseAdvancedType) taxonomicType);
    }
  }

  private void deleteTaxonomicRelation(ConceptType conceptType, List<BaseAdvancedType> alterTypes) {
    BaseSPGType taxonomicType = this.getTaxonomicType(conceptType, alterTypes);
    SPGTripleIdentifier tripleIdentifier =
        new SPGTripleIdentifier(
            taxonomicType.getBaseSpgIdentifier(),
            new PredicateIdentifier(SystemPredicateEnum.BELONG_TO.getName()),
            conceptType.getBaseSpgIdentifier());
    Property taxonomicProperty = taxonomicType.getPropertyByName(tripleIdentifier);
    if (taxonomicProperty != null) {
      taxonomicProperty.setAlterOperation(AlterOperationEnum.DELETE);
    }

    if (taxonomicType.getAlterOperation() == null) {
      taxonomicType.setAlterOperation(AlterOperationEnum.UPDATE);
      alterTypes.listIterator().add((BaseAdvancedType) taxonomicType);
    }
  }

  private BaseSPGType getTaxonomicType(
      BaseAdvancedType conceptType, List<BaseAdvancedType> alterSchema) {
    SPGTypeIdentifier taxonomicTypeIdentifier =
        ((ConceptType) conceptType).getConceptTaxonomicConfig().getTaxonomicTypeIdentifier();
    BaseSPGType taxonomicType = SchemaAlterUtils.findSpgType(alterSchema, taxonomicTypeIdentifier);

    if (taxonomicType == null) {
      taxonomicType = spgTypeService.querySPGTypeByIdentifier(taxonomicTypeIdentifier);
      if (null == taxonomicType) {
        throw SchemaException.spgTypeNotExist(taxonomicTypeIdentifier.toString());
      }
    }
    return taxonomicType;
  }
}
