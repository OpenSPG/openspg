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
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.EncryptTypeEnum;
import com.antgroup.openspg.core.spgschema.model.predicate.MountedConceptConfig;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;


public class PropertyMockFactory {

    public static Property mockBasicProperty(String spgTypeName, Long ontologyId) {
        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(spgTypeName, ontologyId);
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("gender"), "性别", "desc gender");
        SPGTypeRef objectTypeRef = SPGTypeMockFactory.mockTextTypeRef();

        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
        advancedConfig.setConstraint(ConstraintMockFactory.mockGenderEnumConstraint());

        Property property = new Property(basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);
        property.setAlterOperation(AlterOperationEnum.CREATE);
        property.setProjectId(1L);
        property.setOntologyId(new OntologyId(MockConstants.BASIC_PROPERTY_ID));
        return property;
    }

    public static Property mockStandardProperty(String spgTypeName, Long ontologyId) {
        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(spgTypeName, ontologyId);
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("mobile"), "手机", "desc mobile");
        SPGTypeRef objectTypeRef = SPGTypeMockFactory.mockStdTypeRef();

        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
        advancedConfig.setEncryptTypeEnum(EncryptTypeEnum.MOBILE);

        Property property = new Property(basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);
        property.setAlterOperation(AlterOperationEnum.CREATE);
        property.setProjectId(1L);
        property.setOntologyId(new OntologyId(MockConstants.STANDARD_PROPERTY_ID));
        return property;
    }

    public static Property mockConceptProperty(String spgTypeName, Long ontologyId) {
        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(spgTypeName, ontologyId);
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("address"), "住址", "desc");
        SPGTypeRef objectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(spgTypeName, ontologyId);

        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
        advancedConfig.setEncryptTypeEnum(EncryptTypeEnum.ADDRESS);
        advancedConfig.setMountedConceptConfig(new MountedConceptConfig("zhejiang", "city"));

        Property property = new Property(basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);
        property.setAlterOperation(AlterOperationEnum.UPDATE);
        property.setProjectId(1L);
        property.setOntologyId(new OntologyId(MockConstants.CONCEPT_PROPERTY_ID));
        return property;
    }

    public static List<Property> mockThingProperty() {
        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(MockConstants.THING_TYPE_NAME,
            MockConstants.THING_TYPE_ID);
        SPGTypeRef objectTypeRef = SPGTypeMockFactory.mockTextTypeRef();

        BasicInfo<PredicateIdentifier> idBasicInfo = new BasicInfo<>(
            new PredicateIdentifier("id"), "id", "desc id");
        Property idProp = new Property(idBasicInfo, subjectTypeRef, objectTypeRef, false, new PropertyAdvancedConfig());
        idProp.setProjectId(1L);
        idProp.setOntologyId(new OntologyId(MockConstants.ID_PROPERTY_ID));

        BasicInfo<PredicateIdentifier> nameBasicInfo = new BasicInfo<>(
            new PredicateIdentifier("name"), "name", "desc id");
        Property nameProp = new Property(nameBasicInfo, subjectTypeRef, objectTypeRef, false,
            new PropertyAdvancedConfig());
        nameProp.setProjectId(1L);
        nameProp.setOntologyId(new OntologyId(MockConstants.NAME_PROPERTY_ID));

        BasicInfo<PredicateIdentifier> descBasicInfo = new BasicInfo<>(
            new PredicateIdentifier("desc"), "desc", "desc id");
        Property descProp = new Property(descBasicInfo, subjectTypeRef, objectTypeRef, false,
            new PropertyAdvancedConfig());
        descProp.setProjectId(1L);
        descProp.setOntologyId(new OntologyId(MockConstants.DESC_PROPERTY_ID));

        return Lists.newArrayList(idProp, nameProp, descProp);
    }

    public static Property mockEntityProperty(String subjectTypeName, Long subjectTypeId,
        String objectTypeName, Long objectTypeId) {
        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(subjectTypeName, subjectTypeId);
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("relate"), "相关实体", "desc");
        SPGTypeRef objectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(objectTypeName, objectTypeId);

        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
        Property property = new Property(
            basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);

        advancedConfig.setMultiVersionConfig(new MultiVersionConfig("yyyymmdd", 1, 30));
        advancedConfig.setSubProperties(SubPropertyMockFactory.mock(property.toRef()));
        property.setAlterOperation(AlterOperationEnum.DELETE);
        property.setProjectId(1L);
        property.setOntologyId(new OntologyId(MockConstants.ENTITY_PROPERTY_ID));

        return property;
    }

    public static List<Property> mockEventBuiltInProperties(String spgTypeName, Long ontologyId) {
        List<Property> propertys = new ArrayList<>();

        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(spgTypeName, ontologyId);
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("eventTime"), "发生时间", "desc mobile");
        SPGTypeRef objectTypeRef = new SPGTypeRef(new BasicInfo<>(
            SPGTypeIdentifier.parse("STD.Timestamp"), "时间戳", "desc"),
            SPGTypeEnum.STANDARD_TYPE);
        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
        advancedConfig.setPropertyGroup(PropertyGroupEnum.TIME);
        Property eventTime = new Property(
            basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);
        eventTime.setAlterOperation(AlterOperationEnum.CREATE);
        propertys.add(eventTime);

        BasicInfo<PredicateIdentifier> subjectBasicInfo = new BasicInfo<>(
            new PredicateIdentifier("subject"), "主体", "desc mobile");
        SPGTypeRef subObjectTypeRef = new SPGTypeRef(new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.ENTITY1_TYPE_NAME), "主体", "desc"),
            SPGTypeEnum.ENTITY_TYPE);
        PropertyAdvancedConfig subAdvancedConfig = new PropertyAdvancedConfig();
        advancedConfig.setPropertyGroup(PropertyGroupEnum.SUBJECT);
        Property subject = new Property(
            subjectBasicInfo, subjectTypeRef, subObjectTypeRef, false, subAdvancedConfig);
        subject.setAlterOperation(AlterOperationEnum.CREATE);
        propertys.add(subject);

        BasicInfo<PredicateIdentifier> objBasicInfo = new BasicInfo<>(
            new PredicateIdentifier("objectWho"), "客体", "desc mobile");
        SPGTypeRef objObjectTypeRef = new SPGTypeRef(new BasicInfo<>(
            SPGTypeIdentifier.parse(MockConstants.CONCEPT_TYPE_NAME), "客体", "desc"),
            SPGTypeEnum.CONCEPT_TYPE);
        PropertyAdvancedConfig objAdvancedConfig = new PropertyAdvancedConfig();
        advancedConfig.setPropertyGroup(PropertyGroupEnum.OBJECT);
        Property obj = new Property(
            objBasicInfo, subjectTypeRef, objObjectTypeRef, false, objAdvancedConfig);
        obj.setAlterOperation(AlterOperationEnum.CREATE);
        propertys.add(obj);
        return propertys;
    }
}
