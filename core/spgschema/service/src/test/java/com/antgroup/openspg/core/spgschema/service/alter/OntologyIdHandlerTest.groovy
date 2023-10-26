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

import com.antgroup.openspg.cloudext.interfaces.repository.sequence.SequenceRepository
import com.antgroup.openspg.common.model.project.Project
import com.antgroup.openspg.core.spgschema.model.SchemaConstants
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.spgschema.model.predicate.Property
import com.antgroup.openspg.core.spgschema.model.predicate.Relation
import com.antgroup.openspg.core.spgschema.model.type.*
import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext
import com.antgroup.openspg.core.spgschema.service.alter.stage.handler.OntologyIdHandler
import com.antgroup.openspg.core.spgschema.service.type.SPGTypeService
import org.apache.commons.collections4.CollectionUtils
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertNotNull

class OntologyIdHandlerTest extends Specification {
    def sequenceRepository = Mock(SequenceRepository.class)
    def spgTypeService = Mock(SPGTypeService.class)
    def ontologyIdHandler = new OntologyIdHandler(sequenceRepository: sequenceRepository, spgTypeService: spgTypeService)

    def "test handle"() {
        given:
        Project project = new Project(1L, null, null, "test", 1L)
        SchemaAlterContext context = new SchemaAlterContext()
                .setProject(project)
                .setAlterSchema(mockAlterSchema())
                .setReleasedSchema(mockOnlineSchema())
        spgTypeService.querySPGTypeByIdentifier(SPGTypeIdentifier.parse(SchemaConstants.ROOT_TYPE_UNIQUE_NAME))
                >> com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockThingType()
        sequenceRepository.getSeqIdByTime() >> System.currentTimeMillis()

        when:
        ontologyIdHandler.handle(context)

        then:
        for (BaseAdvancedType advancedType : context.getAlterSchema()) {
            assertNotNull(advancedType.getOntologyId())
            assertNotNull(advancedType.getUniqueId())
            assertNotNull(advancedType.getAlterId())

            if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
                for (Property property : advancedType.getProperties()) {
                    if (Boolean.FALSE != property.inherited) {
                        continue
                    }
                    assertNotNull(property.getOntologyId())
                    assertNotNull(property.getUniqueId())
                    assertNotNull(property.getAlterId())
                }
            }

            if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
                for (Relation relation : advancedType.getRelations()) {
                    if (Boolean.FALSE != relation.inherited
                            || Boolean.FALSE != relation.semanticRelation) {
                        continue
                    }
                    assertNotNull(relation.getOntologyId())
                    assertNotNull(relation.getUniqueId())
                    assertNotNull(relation.getAlterId())
                }
            }
        }
    }

    List<BaseSPGType> mockOnlineSchema() {
        return [BasicType.from(BasicTypeEnum.TEXT.name()),
                BasicType.from(BasicTypeEnum.LONG.name()),
                BasicType.from(BasicTypeEnum.DOUBLE.name()),
                com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockStdType(com.antgroup.openspg.core.spgschema.service.model.MockConstants.PHONE_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.PHONE_TYPE_ID),
                com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockStdType(com.antgroup.openspg.core.spgschema.service.model.MockConstants.TIME_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.TIME_TYPE_ID),
                com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEntityType(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_NAME, com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_ID)
        ]
    }

    List<BaseAdvancedType> mockAlterSchema() {
        EntityType entityType = com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEntityType()
        entityType.setAlterOperation(AlterOperationEnum.CREATE)
        entityType.setOntologyId(null)
        entityType.getProperties().forEach(p -> {
            p.setOntologyId(null)
            p.setAlterOperation(AlterOperationEnum.CREATE)
        })
        entityType.getRelations().forEach(p -> {
            p.setOntologyId(null)
            p.setAlterOperation(AlterOperationEnum.CREATE)
        })

        ConceptType conceptType = com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockConceptType()
        conceptType.setAlterOperation(AlterOperationEnum.CREATE)
        conceptType.setOntologyId(null)
        conceptType.getProperties().forEach(p -> {
            p.setOntologyId(null)
            p.setAlterOperation(AlterOperationEnum.CREATE)
        })
        conceptType.getRelations().forEach(p -> {
            p.setOntologyId(null)
            p.setAlterOperation(AlterOperationEnum.CREATE)
        })

        EventType eventType = com.antgroup.openspg.core.spgschema.service.model.SPGTypeMockFactory.mockEventType()
        eventType.setAlterOperation(AlterOperationEnum.CREATE)
        eventType.setOntologyId(null)
        eventType.getProperties().forEach(p -> {
            p.setOntologyId(null)
            p.setAlterOperation(AlterOperationEnum.CREATE)
        })
        eventType.getRelations().forEach(p -> {
            p.setOntologyId(null)
            p.setAlterOperation(AlterOperationEnum.CREATE)
        })

        return [entityType, conceptType, eventType]
    }
}
