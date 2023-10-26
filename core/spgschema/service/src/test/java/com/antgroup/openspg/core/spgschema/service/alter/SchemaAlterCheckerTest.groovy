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


package com.antgroup.openspg.core.spgschema.service.alter

import com.antgroup.openspg.common.model.project.Project
import com.antgroup.openspg.core.spgschema.model.BasicInfo
import com.antgroup.openspg.core.spgschema.model.SchemaConstants
import com.antgroup.openspg.core.spgschema.model.SchemaException
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.spgschema.model.predicate.Property
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyGroupEnum
import com.antgroup.openspg.core.spgschema.model.type.*
import com.antgroup.openspg.core.spgschema.service.alter.check.SchemaAlterChecker
import com.antgroup.openspg.core.spgschema.service.alter.check.SchemaCheckContext
import com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory
import spock.lang.Shared
import spock.lang.Specification

class SchemaAlterCheckerTest extends Specification {
    @Shared
    SchemaAlterChecker checker = new SchemaAlterChecker()

    def "test check"() {
        given:
        Project project = new Project(1L, null, null, "test", 1L)
        List<Property> defaultProps = PropertyMockFactory.mockThingProperty()

        when:
        SchemaCheckContext context = SchemaCheckContext.build(project, onlineSchema, alterSchema, defaultProps)
        checker.check(context)

        then:
        def exception = thrown(expectedException)
        exception.message == expectedMessage

        where:
        onlineSchema         | alterSchema         || expectedException        | expectedMessage
        []                   | []                  || IllegalArgumentException | "schema draft is empty"
        []                   | mockAlterSchema1()  || IllegalArgumentException | "exist blank name type"
        []                   | mockAlterSchema2()  || IllegalArgumentException | "type name: t not match project namespace: test"
        []                   | mockAlterSchema3()  || IllegalArgumentException | String.format(
                "length of type name: test.%s can not be longer than 60", getNameTooLong())
        []                   | mockAlterSchema4()  || IllegalArgumentException | String.format(
                "type name:%s not match: %s", "test.11t", "^[A-Z][a-zA-Z0-9]*")
        []                   | mockAlterSchema5()  || IllegalArgumentException | String.format(
                "nameZh of type: %s can not be blank", "test.Tt")
        []                   | mockAlterSchema5()  || IllegalArgumentException | String.format(
                "nameZh of type: %s can not be blank", "test.Tt")
        []                   | mockAlterSchema6()  || IllegalArgumentException | String.format(
                "nameZh of type: %s can not be longer than %s",
                "test.Tt", SchemaConstants.SCHEMA_SPG_TYPE_MAX_NAME_ZH)
        []                   | mockAlterSchema7()  || IllegalArgumentException | String.format(
                "desc of type: %s can not be longer than %s",
                "test.Tt", SchemaConstants.SCHEMA_SPG_TYPE_MAX_DESCRIPTION)
        []                   | mockAlterSchema8()  || IllegalArgumentException | String.format(
                "parent type of: %s can not be null", "test.Tt")
        []                   | mockAlterSchema9()  || IllegalArgumentException | String.format(
                "parent type: %s not exist", "test.Bb")
        []                   | mockAlterSchema10() || IllegalArgumentException | String.format(
                "alter operation can not be null")
        []                   | mockAlterSchema10() || IllegalArgumentException | String.format(
                "alter operation can not be null")
        mockOnlineSchema11() | mockAlterSchema11() || SchemaException          | String.format(
                "exist spg type with name=%s", "test.Tt")
        []                   | mockAlterSchema12() || SchemaException          | String.format(
                "there is no spg type with name=%s", "test.Tt")
        []                   | mockAlterSchema13() || IllegalArgumentException | String.format(
                "operator name of type: %s is null", "test.Tt")
        []                   | mockAlterSchema14() || IllegalArgumentException | String.format(
                "operator version of type: %s is null", "test.Tt")
        []                   | mockAlterSchema15() || IllegalArgumentException | String.format(
                "conceptLayerConfig of %s is null", "test.TestConcept")
        []                   | mockAlterSchema16() || IllegalArgumentException | String.format(
                "hypernymPredicate of %s is blank", "test.TestConcept")
        []                   | mockAlterSchema17() || IllegalArgumentException | String.format(
                "taxonomic type: %s not exists", "test.TestEntity")
        []                   | mockAlterSchema18() || IllegalArgumentException | String.format(
                "event type must contain %s property", PropertyGroupEnum.SUBJECT.name().toLowerCase())
    }

    List<BaseAdvancedType> mockAlterSchema1() {
        return [new EntityType(null, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema2() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("t"))
        return [new EntityType(basicInfo, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema3() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test." + getNameTooLong()))
        return [new EntityType(basicInfo, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema4() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.11t"))
        return [new EntityType(basicInfo, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema5() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"))
        return [new EntityType(basicInfo, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema6() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), getNameZhTooLong(), null)
        return [new EntityType(basicInfo, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema7() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "dd", getDescTooLong())
        return [new EntityType(basicInfo, null, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema8() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        return [new EntityType(basicInfo, new ParentTypeInfo(null, null, null, null), null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema9() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = new ParentTypeInfo(null, null, SPGTypeIdentifier.parse("test.Bb"), null)
        return [new EntityType(basicInfo, parentTypeInfo, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema10() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        return [new EntityType(basicInfo, parentTypeInfo, null, null, null)]
    }

    List<BaseAdvancedType> mockAlterSchema11() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, null)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)
        return [entityType]
    }

    List<BaseAdvancedType> mockAlterSchema12() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, null)
        entityType.setAlterOperation(AlterOperationEnum.UPDATE)
        return [entityType]
    }

    List<BaseAdvancedType> mockAlterSchema13() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey(null, null))
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, advancedConfig)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)
        return [entityType]
    }

    List<BaseAdvancedType> mockAlterSchema14() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey("op", null))
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, advancedConfig)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)
        return [entityType]
    }

    List<BaseAdvancedType> mockAlterSchema15() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestEntity"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey("op", 1))
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, advancedConfig)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)

        BasicInfo<SPGTypeIdentifier> conceptBasicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestConcept"), " 测试概念类型", "描述")
        ConceptType conceptType = new ConceptType(conceptBasicInfo, ParentTypeInfo.THING, null, null, advancedConfig, null, null, null)
        conceptType.setAlterOperation(AlterOperationEnum.CREATE)
        return [entityType, conceptType]
    }

    List<BaseAdvancedType> mockAlterSchema16() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestEntity"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey("op", 1))
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, advancedConfig)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)

        BasicInfo<SPGTypeIdentifier> conceptBasicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestConcept"), " 测试概念类型", "描述")
        ConceptLayerConfig conceptLayerConfig = new ConceptLayerConfig(null, null)
        ConceptType conceptType = new ConceptType(conceptBasicInfo, ParentTypeInfo.THING, null, null, advancedConfig, conceptLayerConfig, null, null)
        conceptType.setAlterOperation(AlterOperationEnum.CREATE)
        return [entityType, conceptType]
    }

    List<BaseAdvancedType> mockAlterSchema17() {
        BasicInfo<SPGTypeIdentifier> conceptBasicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestConcept"), " 测试概念类型", "描述")
        ConceptLayerConfig conceptLayerConfig = new ConceptLayerConfig("isA", null)
        ConceptTaxonomicConfig taxonomicConfig = new ConceptTaxonomicConfig(SPGTypeIdentifier.parse("test.TestEntity"))
        ConceptType conceptType = new ConceptType(conceptBasicInfo, ParentTypeInfo.THING, null, null, new SPGTypeAdvancedConfig(), conceptLayerConfig, taxonomicConfig, null)
        conceptType.setAlterOperation(AlterOperationEnum.CREATE)
        return [conceptType]
    }

    List<BaseAdvancedType> mockAlterSchema18() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestEntity"), "测试实体类型", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey("op", 1))
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, advancedConfig)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)

        BasicInfo<SPGTypeIdentifier> conceptBasicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestConcept"), " 测试概念类型", "描述")
        ConceptLayerConfig conceptLayerConfig = new ConceptLayerConfig("isA", null)
        ConceptTaxonomicConfig taxonomicConfig = new ConceptTaxonomicConfig(SPGTypeIdentifier.parse("test.TestEntity"))
        ConceptType conceptType = new ConceptType(conceptBasicInfo, ParentTypeInfo.THING, null, null, new SPGTypeAdvancedConfig(), conceptLayerConfig, taxonomicConfig, null)
        conceptType.setAlterOperation(AlterOperationEnum.CREATE)

        BasicInfo<SPGTypeIdentifier> eventBasicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.TestEvent"), "测试事件类型", "desc")
        EventType eventType = new EventType(eventBasicInfo, ParentTypeInfo.THING, null, null, advancedConfig)
        eventType.setAlterOperation(AlterOperationEnum.CREATE)

        return [entityType, conceptType, eventType]
    }

    List<BaseAdvancedType> mockOnlineSchema11() {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(SPGTypeIdentifier.parse("test.Tt"), "dd", "desc")
        ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING
        EntityType entityType = new EntityType(basicInfo, parentTypeInfo, null, null, null)
        return [entityType]
    }

    String getNameTooLong() {
        String s = ""
        for (int i = 0; i < 61; i++) {
            s = s + String.valueOf(i)
        }
        return s
    }

    String getNameZhTooLong() {
        String s = ""
        for (int i = 0; i < 1000; i++) {
            s = s + String.valueOf(i)
        }
        return s
    }

    String getDescTooLong() {
        String s = ""
        for (int i = 0; i < 1000; i++) {
            s = s + String.valueOf(i)
        }
        return s
    }
}
