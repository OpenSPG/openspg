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
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.spgschema.model.predicate.Property
import com.antgroup.openspg.core.spgschema.model.predicate.Relation
import com.antgroup.openspg.core.spgschema.model.semantic.SystemPredicateEnum
import com.antgroup.openspg.core.spgschema.model.type.*
import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext
import com.antgroup.openspg.core.spgschema.service.alter.stage.handler.BuiltInPropertyHandler
import com.antgroup.openspg.core.spgschema.service.type.SPGTypeService
import com.antgroup.openspg.core.spgschema.service.type.model.BuiltInPropertyEnum
import org.apache.commons.collections4.CollectionUtils
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertTrue

class BuiltInPropertyHandlerTest extends Specification {
    def spgTypeService = Mock(SPGTypeService.class)
    def defaultPropertyHandler = new BuiltInPropertyHandler(spgTypeService: spgTypeService)

    def "test handle"() {
        given:
        Project project = new Project(1L, null, null, "test", 1L)
        SchemaAlterContext context = new SchemaAlterContext().setProject(project).setAlterSchema(mockAlterSchema())

        when:
        defaultPropertyHandler.handle(context)

        then:
        assertTrue(containsBuiltInProperty(context.getAlterSchema().get(1).getProperties(), BuiltInPropertyEnum.STD_ID.getName()))
        assertTrue(containsBuiltInProperty(context.getAlterSchema().get(1).getProperties(), BuiltInPropertyEnum.ALIAS.getName()))
        assertTrue(containsBuiltInProperty(context.getAlterSchema().get(2).getProperties(), BuiltInPropertyEnum.EVENT_TIME.getName()))
        assertTrue(containsBuiltInProperty(context.getAlterSchema().get(1).getRelations(), SystemPredicateEnum.IS_A.getName()))
    }

    boolean containsBuiltInProperty(List<Property> propertys, String propertyName) {
        if (CollectionUtils.isEmpty(propertys)) {
            return false
        }
        for (Property property : propertys) {
            if (property.getName() == propertyName) {
                return true
            }
        }
        return false
    }

    boolean containsDefaultRelation(List<Relation> relations, String relationName) {
        if (CollectionUtils.isEmpty(relations)) {
            return false
        }
        for (Relation relation : relations) {
            if (relation.getName() == relationName) {
                return true
            }
        }
        return false
    }

    List<BaseAdvancedType> mockAlterSchema() {
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
}
