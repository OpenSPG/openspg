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
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum
import com.antgroup.openspg.core.spgschema.service.model.SimplePropertyMockFactory
import com.antgroup.openspg.core.spgschema.service.model.SimpleSpgTypeMockFactory
import com.antgroup.openspg.core.spgschema.service.predicate.SubPropertyService
import com.antgroup.openspg.core.spgschema.service.predicate.impl.PropertyAdvancedConfigHandler
import com.antgroup.openspg.core.spgschema.service.predicate.impl.PropertyServiceImpl
import com.antgroup.openspg.core.spgschema.service.predicate.repository.ConstraintRepository
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository
import com.antgroup.openspg.core.spgschema.service.semantic.LogicalRuleService
import com.antgroup.openspg.core.spgschema.service.semantic.SemanticService
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType
import com.antgroup.openspg.core.spgschema.service.type.repository.SPGTypeRepository
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertNotNull

class PropertyServiceTest extends Specification {
    def simplePredicateRepository = Mock(PropertyRepository.class)
    def propertyAdvancedConfigService = Mock(PropertyAdvancedConfigHandler.class)
    def constraintRepository = Mock(ConstraintRepository.class)
    def subPropertyService = Mock(SubPropertyService.class)
    def semanticService = Mock(SemanticService.class)
    def logicalRuleService = Mock(LogicalRuleService.class)
    def simpleSpgTypeRepository = Mock(SPGTypeRepository.class)

    def propertyService = new PropertyServiceImpl(
            simplePredicateRepository: simplePredicateRepository,
            propertyAdvancedConfigService: propertyAdvancedConfigService,
            constraintRepository: constraintRepository,
            subPropertyService: subPropertyService,
            semanticService: semanticService,
            logicalRuleService: logicalRuleService,
            simpleSpgTypeRepository: simpleSpgTypeRepository)

    def "testCreate"() {
        when:
        simplePredicateRepository.save(_) >> 1
        propertyService.create(property) >> ret

        then:
        assertNotNull(ret)

        where:
        property           || ret
        newProperty(true)  || 0
        newProperty(false) || 1
    }

    def "testUpdate"() {
        when:
        simplePredicateRepository.update(_) >> 1
        propertyService.update(property) >> ret

        then:
        assertNotNull(ret)

        where:
        property           || ret
        newProperty(true)  || 0
        newProperty(false) || 1
    }

    def "testDelete"() {
        when:
        simplePredicateRepository.delete(_) >> 1
        propertyService.delete(property) >> ret

        then:
        assertNotNull(ret)

        where:
        property           || ret
        newProperty(true)  || 0
        newProperty(false) || 1
    }

    def "testQueryBySubjectId"() {
        when:
        simplePredicateRepository.queryBySubjectId(_, SPGOntologyEnum.PROPERTY) >> simpleProperties
        simpleSpgTypeRepository.queryByUniqueId(_) >> spgTypes
        constraintRepository.queryById(_) >> constraints
        subPropertyService.queryBySubjectId(_, SPGOntologyEnum.PROPERTY) >> subProperties
        semanticService.queryBySubjectIds(_, SPGOntologyEnum.PROPERTY) >> semantics
        logicalRuleService.queryByRuleCode(_) >> logicalRules

        def ret = propertyService.queryBySubjectId(subjectIds)

        then:
        assertNotNull(ret)
        ret.size() == size

        where:
        subjectIds | simpleProperties                                                                     | spgTypes      | constraints | subProperties | semantics | logicalRules || size
        []         | []                                                                                   | []            | []          | []            | []        | []           || 0
        [111]      | []                                                                                   | []            | []          | []            | []        | []           || 0
        [111]      | [SimplePropertyMockFactory.mock()] | mockSpgType() | [] | [] | [] | [] || 1
    }

    private Property newProperty(boolean inherited) {
        return new Property(null, null, null, inherited, null)
    }

    private List<SimpleSPGType> mockSpgType() {
        List<SimpleSPGType> spgTypes = new ArrayList<>()
        spgTypes.add(SimpleSpgTypeMockFactory.mockSimpleEntity(1L))
        spgTypes.addAll(SimpleSpgTypeMockFactory.mockBasicTypes())
        return spgTypes
    }
}
