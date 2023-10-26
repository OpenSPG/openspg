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

import com.antgroup.openspg.core.spgschema.model.predicate.Relation
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum
import com.antgroup.openspg.core.spgschema.service.model.SimpleSpgTypeMockFactory
import com.antgroup.openspg.core.spgschema.service.predicate.SubPropertyService
import com.antgroup.openspg.core.spgschema.service.predicate.impl.PropertyAdvancedConfigHandler
import com.antgroup.openspg.core.spgschema.service.predicate.impl.RelationServiceImpl
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository
import com.antgroup.openspg.core.spgschema.service.semantic.LogicalRuleService
import com.antgroup.openspg.core.spgschema.service.semantic.SemanticService
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType
import com.antgroup.openspg.core.spgschema.service.type.repository.SPGTypeRepository
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertNotNull

class RelationServiceTest extends Specification {
    def simplePredicateRepository = Mock(PropertyRepository.class)
    def propertyAdvancedConfigService = Mock(PropertyAdvancedConfigHandler.class)
    def subPropertyService = Mock(SubPropertyService.class)
    def semanticService = Mock(SemanticService.class)
    def logicalRuleService = Mock(LogicalRuleService.class)
    def simpleSpgTypeRepository = Mock(SPGTypeRepository.class)

    def relationService = new RelationServiceImpl(
            propertyRepository: simplePredicateRepository,
            propertyAdvancedConfigService: propertyAdvancedConfigService,
            subPropertyService: subPropertyService,
            semanticService: semanticService,
            logicalRuleService: logicalRuleService,
            simpleSpgTypeRepository: simpleSpgTypeRepository)

    def "testCreate"() {
        when:
        simplePredicateRepository.save(_) >> 1
        relationService.create(relation) >> ret

        then:
        assertNotNull(ret)

        where:
        relation                  || ret
        newRelation(true, false)  || 0
        newRelation(false, true)  || 0
        newRelation(false, false) || 1
    }

    def "testUpdate"() {
        when:
        simplePredicateRepository.update(_) >> 1
        relationService.update(relation) >> ret

        then:
        assertNotNull(ret)

        where:
        relation                  || ret
        newRelation(true, false)  || 0
        newRelation(false, true)  || 0
        newRelation(false, false) || 1
    }

    def "testDelete"() {
        when:
        simplePredicateRepository.delete(_) >> 1
        relationService.delete(relation) >> ret

        then:
        assertNotNull(ret)

        where:
        relation                  || ret
        newRelation(true, false)  || 0
        newRelation(false, true)  || 0
        newRelation(false, false) || 1
    }

    def "testQueryBySubjectId"() {
        when:
        simplePredicateRepository.queryBySubjectId(_, SPGOntologyEnum.RELATION) >> simpleProperties
        simpleSpgTypeRepository.queryByUniqueId(_) >> spgTypes
        subPropertyService.queryBySubjectId(_, SPGOntologyEnum.RELATION) >> subProperties
        semanticService.queryBySubjectIds(_, SPGOntologyEnum.RELATION) >> semantics
        logicalRuleService.queryByRuleCode(_) >> logicalRules

        def ret = relationService.queryBySubjectId(subjectIds)

        then:
        assertNotNull(ret)
        ret.size() == size

        where:
        subjectIds | simpleProperties                                                                     | spgTypes      | subProperties | semantics | logicalRules || size
        []         | []                                                                                   | []            | []            | []        | []           || 0
        [111]      | []                                                                                   | []            | []            | []        | []           || 0
        [111]      | [com.antgroup.openspg.core.spgschema.service.model.SimplePropertyMockFactory.mock()] | mockSpgType() | []            | []        | []           || 1
    }

    private Relation newRelation(boolean inherited, isSemantic) {
        return new Relation(null, null,
                null, inherited, null, isSemantic)
    }

    private List<SimpleSPGType> mockSpgType() {
        List<SimpleSPGType> spgTypes = new ArrayList<>();
        spgTypes.add(SimpleSpgTypeMockFactory.mockSimpleEntity(1L))
        spgTypes.addAll(SimpleSpgTypeMockFactory.mockBasicTypes())
        return spgTypes
    }
}
