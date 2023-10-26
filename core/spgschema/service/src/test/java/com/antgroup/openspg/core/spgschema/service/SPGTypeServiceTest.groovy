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


package com.antgroup.openspg.core.spgschema.service

import com.antgroup.openspg.core.spgschema.model.predicate.Property
import com.antgroup.openspg.core.spgschema.model.predicate.Relation
import com.antgroup.openspg.core.spgschema.service.model.SimpleSpgTypeMockFactory
import com.antgroup.openspg.core.spgschema.service.predicate.PropertyService
import com.antgroup.openspg.core.spgschema.service.predicate.RelationService
import com.antgroup.openspg.core.spgschema.service.type.impl.SPGTypeServiceImpl
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType
import com.antgroup.openspg.core.spgschema.service.type.repository.SPGTypeRepository
import spock.lang.Specification

class SPGTypeServiceTest extends Specification {
    def spgTypeRepository = Mock(SPGTypeRepository.class)
    def propertyService = Mock(PropertyService.class)
    def relationService = Mock(RelationService.class)

    def spgTypeService = new SPGTypeServiceImpl(
            spgTypeRepository: spgTypeRepository,
            propertyService: propertyService,
            relationService: relationService)

    def "test create"() {
        when:
        spgTypeRepository.save(_ as SimpleSPGType) >> 1
        def cnt = spgTypeService.create(spgType)

        then:
        cnt == recordCnt

        where:
        spgType                                                                                || recordCnt
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEntityType()  || 1
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockConceptType() || 1
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEventType()   || 1
    }

    def "test update"() {
        when:
        spgTypeRepository.update(_ as SimpleSPGType) >> 1
        propertyService.create(_ as Property) >> 1
        propertyService.update(_ as Property) >> 1
        propertyService.delete(_ as Property) >> 1

        relationService.create(_ as Relation) >> 1
        relationService.update(_ as Relation) >> 1
        relationService.delete(_ as Relation) >> 1

        def cnt = spgTypeService.update(spgType)

        then:
        cnt == recordCnt

        where:
        spgType                                                                                || recordCnt
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEntityType()  || 1
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockConceptType() || 1
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEventType()   || 1
    }

    def "test delete"() {
        when:
        spgTypeRepository.delete(_ as SimpleSPGType) >> 1
        propertyService.delete(_ as Property) >> 1
        relationService.delete(_ as Relation) >> 1

        def cnt = spgTypeService.delete(spgType)

        then:
        cnt == recordCnt

        where:
        spgType                                                                                || recordCnt
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEntityType()  || 1
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockConceptType() || 1
        com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEventType()   || 1
    }

    def "test queryProjectSchema"() {
        when:
        spgTypeRepository.queryAllBasicType() >> SimpleSpgTypeMockFactory.mockBasicTypes()
        spgTypeRepository.queryAllStandardType() >> SimpleSpgTypeMockFactory.mockStandardTypes()
        spgTypeRepository.queryByProject(_ as Long) >> projectTypes
        propertyService.queryBySubjectId(_ as List<Long>) >> properties
        relationService.queryBySubjectId(_ as List<Long>) >> relations

        def projectSchema = spgTypeService.queryProjectSchema(projectId)
        def spgTypes = projectSchema.getSpgTypes()

        then:
        with(spgTypes) {
            spgTypes.size() == size
            spgTypes.stream().mapToInt(e ->
                    e.getProperties().size()).reduce(0, Integer::sum) == propSize
            spgTypes.stream().mapToInt(e ->
                    e.getRelations().size()).reduce(0, Integer::sum) == relationSize
        }

        where:
        projectId | projectTypes                                  | properties       | relations       || size | propSize | relationSize
        1         | []                                            | []               | []              || 5    | 0        | 0
        1         | SimpleSpgTypeMockFactory.mockProjectTypes(1L) | []               | []              || 8    | 0        | 0
        1         | SimpleSpgTypeMockFactory.mockProjectTypes(1L) | mockProperties() | []              || 8    | 12       | 1
        1         | SimpleSpgTypeMockFactory.mockProjectTypes(1L) | mockProperties() | mockRelations() || 8    | 12       | 2
    }

    private List<Property> mockProperties() {
        List<Property> propertys = new ArrayList<>()

        propertys.addAll(com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory.mockThingProperty())

        Property entityProp = com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory
                .mockBasicProperty(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_ID)
        propertys.add(entityProp)

        Property conceptPros = com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory
                .mockStandardProperty(com.antgroup.openspg.core.spgschema.service.model.MockConstants.CONCEPT_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.CONCEPT_TYPE_ID)
        propertys.add(conceptPros)

        Property eventProp = com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory
                .mockEntityProperty(com.antgroup.openspg.core.spgschema.service.model.MockConstants.EVENT_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.EVENT_TYPE_ID,
                        com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_ID)
        propertys.add(eventProp)

        return propertys
    }

    private List<Relation> mockRelations() {
        return com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockRelations(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_NAME,
                com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_ID,
                com.antgroup.openspg.core.spgschema.service.model.MockConstants.EVENT_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.EVENT_TYPE_ID)
    }
}
