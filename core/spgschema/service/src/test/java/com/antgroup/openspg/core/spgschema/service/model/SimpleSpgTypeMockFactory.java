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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.SchemaExtInfo;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.spgschema.model.type.ConceptLayerConfig;
import com.antgroup.openspg.core.spgschema.model.type.ConceptTaxonomicConfig;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.VisibleScopeEnum;
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType;
import com.google.common.collect.Lists;
import java.util.List;

public class SimpleSpgTypeMockFactory {

  public static List<SimpleSPGType> mockBasicTypes() {
    SimpleSPGType text =
        new SimpleSPGType(
            null,
            new OntologyId(1L),
            null,
            new SchemaExtInfo(),
            new BasicInfo<>(SPGTypeIdentifier.parse("Text"), "text", "desc"),
            null,
            SPGTypeEnum.BASIC_TYPE,
            VisibleScopeEnum.PUBLIC,
            null,
            null,
            null,
            null,
            false,
            null);

    SimpleSPGType flo =
        new SimpleSPGType(
            null,
            new OntologyId(2L),
            null,
            new SchemaExtInfo(),
            new BasicInfo<>(SPGTypeIdentifier.parse("Float"), "float", "desc"),
            null,
            SPGTypeEnum.BASIC_TYPE,
            VisibleScopeEnum.PUBLIC,
            null,
            null,
            null,
            null,
            false,
            null);

    SimpleSPGType inte =
        new SimpleSPGType(
            null,
            new OntologyId(3L),
            null,
            new SchemaExtInfo(),
            new BasicInfo<>(SPGTypeIdentifier.parse("Integer"), "integer", "desc"),
            null,
            SPGTypeEnum.BASIC_TYPE,
            VisibleScopeEnum.PUBLIC,
            null,
            null,
            null,
            null,
            false,
            null);

    return Lists.newArrayList(text, flo, inte);
  }

  public static List<SimpleSPGType> mockStandardTypes() {
    SimpleSPGType mobile =
        new SimpleSPGType(
            null,
            new OntologyId(4L),
            null,
            new SchemaExtInfo(),
            new BasicInfo<>(SPGTypeIdentifier.parse("STD.ChinaMobile"), "手机号", "desc"),
            null,
            SPGTypeEnum.STANDARD_TYPE,
            VisibleScopeEnum.PUBLIC,
            null,
            null,
            null,
            null,
            true,
            ConstraintMockFactory.mockMobileConstraintItem());

    SimpleSPGType timestamp =
        new SimpleSPGType(
            null,
            new OntologyId(5L),
            null,
            new SchemaExtInfo(),
            new BasicInfo<>(SPGTypeIdentifier.parse("STD.TimeStamp"), "时间戳", "desc"),
            null,
            SPGTypeEnum.STANDARD_TYPE,
            VisibleScopeEnum.PUBLIC,
            null,
            null,
            null,
            null,
            false,
            null);

    return Lists.newArrayList(mobile, timestamp);
  }

  public static List<SimpleSPGType> mockProjectTypes(Long projectId) {
    SimpleSPGType entity = mockSimpleEntity(projectId);
    SimpleSPGType concept = mockSimpleConcept(projectId);
    SimpleSPGType event = mockSimpleEvent(projectId);

    return Lists.newArrayList(entity, concept, event);
  }

  public static SimpleSPGType mockSimpleThing(Long projectId) {
    return new SimpleSPGType(
        projectId,
        new OntologyId(MockConstants.THING_TYPE_ID),
        null,
        new SchemaExtInfo(),
        new BasicInfo<>(SPGTypeIdentifier.parse(MockConstants.THING_TYPE_NAME), "测试", "desc"),
        null,
        SPGTypeEnum.ENTITY_TYPE,
        VisibleScopeEnum.PUBLIC,
        OperatorMockFactory.mockEntityOperatorConfig(),
        null,
        null,
        null,
        false,
        null);
  }

  public static SimpleSPGType mockSimpleEntity(Long projectId) {
    return new SimpleSPGType(
        projectId,
        new OntologyId(MockConstants.ENTITY1_TYPE_ID),
        null,
        new SchemaExtInfo(),
        new BasicInfo<>(SPGTypeIdentifier.parse(MockConstants.ENTITY1_TYPE_NAME), "测试", "desc"),
        new ParentTypeInfo(
            MockConstants.ENTITY1_TYPE_ID,
            MockConstants.THING_TYPE_ID,
            SPGTypeIdentifier.parse(MockConstants.THING_TYPE_NAME),
            Lists.newArrayList(MockConstants.THING_TYPE_ID, MockConstants.ENTITY1_TYPE_ID)),
        SPGTypeEnum.ENTITY_TYPE,
        VisibleScopeEnum.PUBLIC,
        OperatorMockFactory.mockEntityOperatorConfig(),
        null,
        null,
        null,
        false,
        null);
  }

  public static SimpleSPGType mockSimpleConcept(Long projectId) {
    return new SimpleSPGType(
        projectId,
        new OntologyId(MockConstants.CONCEPT_TYPE_ID),
        null,
        new SchemaExtInfo(),
        new BasicInfo<>(SPGTypeIdentifier.parse(MockConstants.CONCEPT_TYPE_NAME), "测试", "desc"),
        new ParentTypeInfo(
            MockConstants.CONCEPT_TYPE_ID,
            MockConstants.THING_TYPE_ID,
            SPGTypeIdentifier.parse(MockConstants.THING_TYPE_NAME),
            Lists.newArrayList(MockConstants.THING_TYPE_ID, MockConstants.CONCEPT_TYPE_ID)),
        SPGTypeEnum.CONCEPT_TYPE,
        VisibleScopeEnum.PUBLIC,
        OperatorMockFactory.mockConceptOperatorConfig(),
        new ConceptLayerConfig(SystemPredicateEnum.IS_A.getName(), null),
        new ConceptTaxonomicConfig(SPGTypeIdentifier.parse(MockConstants.ENTITY1_TYPE_NAME)),
        new MultiVersionConfig("yyyymmdd", 1, 30),
        false,
        null);
  }

  public static SimpleSPGType mockSimpleEvent(Long projectId) {
    return new SimpleSPGType(
        projectId,
        new OntologyId(MockConstants.EVENT_TYPE_ID),
        null,
        new SchemaExtInfo(),
        new BasicInfo<>(SPGTypeIdentifier.parse(MockConstants.EVENT_TYPE_NAME), "测试", "desc"),
        new ParentTypeInfo(
            MockConstants.EVENT_TYPE_ID,
            MockConstants.THING_TYPE_ID,
            SPGTypeIdentifier.parse(MockConstants.THING_TYPE_NAME),
            Lists.newArrayList(MockConstants.THING_TYPE_ID, MockConstants.EVENT_TYPE_ID)),
        SPGTypeEnum.EVENT_TYPE,
        VisibleScopeEnum.PUBLIC,
        OperatorMockFactory.mockEventOperatorConfig(),
        null,
        null,
        null,
        false,
        null);
  }
}
