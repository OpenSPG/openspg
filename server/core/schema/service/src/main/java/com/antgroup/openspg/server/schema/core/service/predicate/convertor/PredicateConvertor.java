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

package com.antgroup.openspg.server.schema.core.service.predicate.convertor;

import com.antgroup.openspg.server.schema.core.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.schema.core.service.predicate.model.SimpleSubProperty;
import com.antgroup.openspg.schema.model.OntologyId;
import com.antgroup.openspg.schema.model.predicate.EncryptTypeEnum;
import com.antgroup.openspg.schema.model.predicate.MountedConceptConfig;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.predicate.SubProperty;
import com.antgroup.openspg.schema.model.semantic.RuleCode;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.schema.model.type.MultiVersionConfig;

public class PredicateConvertor {

  public static SimpleProperty toSimpleProperty(Property property) {
    OntologyId subjectOntologyId = property.getSubjectTypeRef().getOntologyId();
    OntologyId objectOntologyId = property.getObjectTypeRef().getOntologyId();
    MultiVersionConfig multiVersionConfig = property.getMultiVersionConfig();
    MountedConceptConfig mountedConceptConfig = property.getMountedConceptConfig();
    EncryptTypeEnum encryptTypeEnum = property.getEncryptTypeEnum();
    PropertyGroupEnum propertyGroup = property.getPropertyGroup();
    Long constraintId = property.getConstraint() == null ? null : property.getConstraint().getId();
    RuleCode ruleCode =
        property.getLogicalRule() == null ? null : property.getLogicalRule().getCode();
    SPGOntologyEnum ontologyEnum =
        property instanceof Relation ? SPGOntologyEnum.RELATION : SPGOntologyEnum.PROPERTY;

    SimpleProperty simpleProperty =
        new SimpleProperty(
            property.getBasicInfo(),
            subjectOntologyId,
            objectOntologyId,
            property.getObjectTypeRef().getSpgTypeEnum(),
            multiVersionConfig,
            mountedConceptConfig,
            encryptTypeEnum,
            propertyGroup,
            constraintId,
            ruleCode,
            ontologyEnum);
    simpleProperty.setProjectId(property.getProjectId());
    simpleProperty.setOntologyId(property.getOntologyId());
    simpleProperty.setAlterOperation(property.getAlterOperation());
    simpleProperty.setExtInfo(property.getExtInfo());
    return simpleProperty;
  }

  public static SimpleSubProperty toSimpleSubProperty(SubProperty subProperty) {
    Long constraintId =
        subProperty.getConstraint() == null ? null : subProperty.getConstraint().getId();
    SimpleSubProperty simpleSubProperty =
        new SimpleSubProperty(
            subProperty.getBasicInfo(),
            subProperty.getSubjectTypeRef().getOntologyId(),
            subProperty.getObjectTypeRef().getOntologyId(),
            subProperty.getMultiVersionConfig(),
            subProperty.getEncryptTypeEnum(),
            constraintId,
            SPGOntologyEnum.RELATION.equals(subProperty.getSubjectTypeRef().getOntologyType()));
    simpleSubProperty.setProjectId(subProperty.getProjectId());
    simpleSubProperty.setOntologyId(subProperty.getOntologyId());
    simpleSubProperty.setAlterOperation(subProperty.getAlterOperation());
    simpleSubProperty.setExtInfo(subProperty.getExtInfo());
    return simpleSubProperty;
  }
}
