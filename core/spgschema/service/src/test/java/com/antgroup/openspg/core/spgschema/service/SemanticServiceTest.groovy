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

import com.antgroup.openspg.core.spgschema.model.OntologyId
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository
import com.antgroup.openspg.core.spgschema.service.semantic.impl.SemanticServiceImpl
import com.antgroup.openspg.core.spgschema.service.semantic.model.SimpleSemantic
import com.antgroup.openspg.core.spgschema.service.semantic.repository.SemanticRepository
import com.google.common.collect.Lists
import spock.lang.Specification

class SemanticServiceTest extends Specification {
    def semanticRepository = Mock(SemanticRepository.class)
    def propertyRepository = Mock(PropertyRepository.class)

    def semanticService = new SemanticServiceImpl(
            semanticRepository: semanticRepository,
            propertyRepository: propertyRepository)

    def "test queryBySubjectIds"() {
        when:
        semanticRepository.queryBySubjectId(_ as List<String>, _ as SPGOntologyEnum) >> simpleSemantics
        propertyRepository.queryRefByUniqueId(_, _) >> propertyRefs
        def semantics = semanticService.queryBySubjectIds(subjectIds, ontologyEnum)

        then:
        semantics.size() == result

        where:
        simpleSemantics      | propertyRefs      | subjectIds                                                                           | ontologyEnum             || result
        []                   | []                | [1L]                                                                                 | SPGOntologyEnum.RELATION || 0
        mockSimpleSemantic() | mockPropertyRef() | [com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY_PROPERTY_ID] | SPGOntologyEnum.RELATION || 1
    }

    private List<SimpleSemantic> mockSimpleSemantic() {
        return Lists.newArrayList(com.antgroup.openspg.core.spgschema.service.model.SimpleSemanticMockFactory
                .mockPredicateSemantic(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY_PROPERTY_ID,
                        com.antgroup.openspg.core.spgschema.service.model.MockConstants.INVERSE_PROPERTY_ID))
    }

    private List<PropertyRef> mockPropertyRef() {
        PropertyRef subjectRef = com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory
                .mockEntityProperty(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_NAME,
                        com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_ID, com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_NAME,
                        com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_ID).toRef()
        subjectRef.setOntologyId(new OntologyId(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY_PROPERTY_ID))

        PropertyRef objectRef = com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory
                .mockEntityProperty(com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_NAME,
                        com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY2_TYPE_ID, com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_NAME,
                        com.antgroup.openspg.core.spgschema.service.model.MockConstants.ENTITY1_TYPE_ID).toRef()
        objectRef.setOntologyId(new OntologyId(com.antgroup.openspg.core.spgschema.service.model.MockConstants.INVERSE_PROPERTY_ID))

        return Lists.newArrayList(subjectRef, objectRef)
    }
}
