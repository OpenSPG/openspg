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
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.spgschema.model.type.ConceptLayerConfig;
import com.antgroup.openspg.core.spgschema.model.type.ConceptTaxonomicConfig;
import com.antgroup.openspg.core.spgschema.model.type.ConceptType;
import com.antgroup.openspg.core.spgschema.model.type.EntityType;
import com.antgroup.openspg.core.spgschema.model.type.EventType;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.StandardType;

import com.google.common.collect.Lists;

import java.util.List;


public class SPGTypeMockFactory {

    public static EntityType mockThingType() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.THING_TYPE_NAME), "事物", "desc");
        List<Property> propertys = PropertyMockFactory.mockThingProperty();
        EntityType entityType = new EntityType(basicInfo, null, propertys, null, new SPGTypeAdvancedConfig());
        entityType.setOntologyId(new OntologyId(MockConstants.THING_TYPE_ID));
        return entityType;
    }

    public static StandardType mockStdType(String uniqueName, Long uniqueId) {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(uniqueName), "name", "desc");
        StandardType standardType = new StandardType(basicInfo, null,
            null, null, new SPGTypeAdvancedConfig(), true, null);
        standardType.setOntologyId(new OntologyId(uniqueId));
        return standardType;
    }

    public static EntityType mockEntityType() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.ENTITY1_TYPE_NAME), "测试", "desc");
        ParentTypeInfo parentTypeInfo = new ParentTypeInfo(null, null,
            SPGTypeIdentifier.parse("Thing"), null);
        List<Property> propertys = mockProperties(MockConstants.ENTITY1_TYPE_NAME, MockConstants.ENTITY1_TYPE_ID);
        List<Relation> relations = mockRelations(MockConstants.ENTITY1_TYPE_NAME, MockConstants.ENTITY1_TYPE_ID,
            MockConstants.EVENT_TYPE_NAME, MockConstants.EVENT_TYPE_ID);
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig();
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, propertys, relations, advancedConfig);
        entityType.setOntologyId(new OntologyId(6L));
        return entityType;
    }

    public static ConceptType mockConceptType() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.CONCEPT_TYPE_NAME), "测试", "desc");
        ParentTypeInfo parentTypeInfo = new ParentTypeInfo(null, null,
            SPGTypeIdentifier.parse("Thing"), null);
        List<Property> propertys = mockProperties(MockConstants.CONCEPT_TYPE_NAME, MockConstants.CONCEPT_TYPE_ID);
        List<Relation> relations = mockRelations(MockConstants.CONCEPT_TYPE_NAME, MockConstants.CONCEPT_TYPE_ID,
            MockConstants.ENTITY1_TYPE_NAME, MockConstants.ENTITY1_TYPE_ID);
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig();

        ConceptLayerConfig conceptLayerConfig = new ConceptLayerConfig(
            SystemPredicateEnum.IS_A.getName(), null);
        ConceptTaxonomicConfig conceptTaxonomicConfig = new ConceptTaxonomicConfig(
            SPGTypeIdentifier.parse(MockConstants.ENTITY1_TYPE_NAME));
        MultiVersionConfig multiVersionConfig = new MultiVersionConfig("yyyymmdd", 1, 3);

        ConceptType conceptType = new ConceptType(basicInfo, parentTypeInfo, propertys, relations,
            advancedConfig, conceptLayerConfig, conceptTaxonomicConfig, multiVersionConfig);
        conceptType.setOntologyId(new OntologyId(MockConstants.CONCEPT_TYPE_ID));
        return conceptType;
    }

    public static EventType mockEventType() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.EVENT_TYPE_NAME), "测试事件", "desc");
        ParentTypeInfo parentTypeInfo = new ParentTypeInfo(null, null,
            SPGTypeIdentifier.parse("Thing"), null);
        List<Property> propertys = mockEventProperties(basicInfo.getName().toString(), 8L);
        List<Relation> relations = mockRelations(MockConstants.EVENT_TYPE_NAME, MockConstants.EVENT_TYPE_ID,
            MockConstants.ENTITY1_TYPE_NAME, MockConstants.ENTITY1_TYPE_ID);
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig();
        EventType eventType = new EventType(basicInfo, parentTypeInfo, propertys, relations, advancedConfig);
        eventType.setOntologyId(new OntologyId(8L));
        return eventType;
    }

    public static EntityType mockEntityType(String uniqueName, Long uniqueId) {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(uniqueName), "测试", "desc");
        ParentTypeInfo parentTypeInfo = new ParentTypeInfo(null, null,
            SPGTypeIdentifier.parse("Thing"), null);
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig();
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, advancedConfig);
        entityType.setOntologyId(new OntologyId(uniqueId));
        return entityType;
    }

    public static SPGTypeRef mockSpgTypeRef(String uniqueName, Long ontologyId) {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(uniqueName), "测试", "描述");
        SPGTypeRef ref = new SPGTypeRef(basicInfo, SPGTypeEnum.ENTITY_TYPE);
        ref.setOntologyId(new OntologyId(ontologyId));
        return ref;
    }

    public static SPGTypeRef mockTextTypeRef() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.TEXT_TYPE_NAME), "文本", "描述");
        SPGTypeRef ref = new SPGTypeRef(basicInfo, SPGTypeEnum.BASIC_TYPE);
        ref.setOntologyId(new OntologyId(MockConstants.TEXT_TYPE_ID));
        return ref;
    }

    public static SPGTypeRef mockStdTypeRef() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.PHONE_TYPE_NAME), "手机号", "描述");
        SPGTypeRef ref = new SPGTypeRef(basicInfo, SPGTypeEnum.STANDARD_TYPE);
        ref.setOntologyId(new OntologyId(MockConstants.PHONE_TYPE_ID));
        return ref;
    }

    public static List<Property> mockEventProperties(String spgTypeName, Long ontologyId) {
        List<Property> propertys = PropertyMockFactory.mockEventBuiltInProperties(spgTypeName, ontologyId);
        propertys.add(PropertyMockFactory.mockBasicProperty(spgTypeName, ontologyId));
        propertys.add(PropertyMockFactory.mockStandardProperty(spgTypeName, ontologyId));
        propertys.add(PropertyMockFactory.mockConceptProperty(spgTypeName, ontologyId));
        propertys.add(PropertyMockFactory.mockEntityProperty(spgTypeName, ontologyId,
            MockConstants.ENTITY2_TYPE_NAME, MockConstants.ENTITY2_TYPE_ID));
        return propertys;
    }

    public static List<Property> mockProperties(String spgTypeName, Long ontologyId) {
        return Lists.newArrayList(
            PropertyMockFactory.mockBasicProperty(spgTypeName, ontologyId),
            PropertyMockFactory.mockStandardProperty(spgTypeName, ontologyId),
            PropertyMockFactory.mockConceptProperty(spgTypeName, ontologyId),
            PropertyMockFactory.mockEntityProperty(spgTypeName, ontologyId,
                MockConstants.ENTITY2_TYPE_NAME, MockConstants.ENTITY2_TYPE_ID)
        );
    }

    public static List<Relation> mockRelations(String subjectName, Long subjectId, String objectName, Long objectId) {
        return Lists.newArrayList(
            RelationMockFactory.mockRelation(subjectName, subjectId, objectName, objectId)
        );
    }
}
