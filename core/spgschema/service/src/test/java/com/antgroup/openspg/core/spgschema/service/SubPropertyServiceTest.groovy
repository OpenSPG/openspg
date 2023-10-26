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
import com.antgroup.openspg.core.spgschema.model.constraint.Constraint
import com.antgroup.openspg.core.spgschema.model.predicate.Property
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyAdvancedConfig
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum
import com.antgroup.openspg.core.spgschema.service.model.SimpleSpgTypeMockFactory
import com.antgroup.openspg.core.spgschema.service.predicate.impl.SubPropertyServiceImpl
import com.antgroup.openspg.core.spgschema.service.predicate.repository.ConstraintRepository
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository
import com.antgroup.openspg.core.spgschema.service.predicate.repository.SubPropertyRepository
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType
import com.antgroup.openspg.core.spgschema.service.type.repository.SPGTypeRepository
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertNotNull

class SubPropertyServiceTest extends Specification {
    def constraintRepository = Mock(ConstraintRepository.class)
    def spgTypeRepository = Mock(SPGTypeRepository.class)
    def propertyRepository = Mock(PropertyRepository.class)
    def subPropertyRepository = Mock(SubPropertyRepository.class)

    def subPropertyTypeService = new SubPropertyServiceImpl(
            spgTypeRepository: spgTypeRepository,
            propertyRepository: propertyRepository,
            constraintRepository: constraintRepository,
            subPropertyRepository: subPropertyRepository)

    def "testCreate"() {
        when:
        subPropertyRepository.save(_) >> 1
        subPropertyTypeService.create(subPropertyType) >> ret

        then:
        assertNotNull(ret)

        where:
        subPropertyType  || ret
        newSubProperty() || 1
    }

    def "testUpdate"() {
        when:
        subPropertyRepository.update(_) >> 1
        subPropertyTypeService.update(subPropertyType) >> ret

        then:
        assertNotNull(ret)

        where:
        subPropertyType  || ret
        newSubProperty() || 1
    }

    def "testDelete"() {
        when:
        subPropertyRepository.delete(_) >> 1
        subPropertyTypeService.delete(subPropertyType) >> ret

        then:
        assertNotNull(ret)

        where:
        subPropertyType  || ret
        newSubProperty() || 1
    }

    def "testQueryBySubjectId"() {
        when:
        subPropertyRepository.queryBySubjectId(_, _) >> simpleSubProperties
        spgTypeRepository.queryByUniqueId(_) >> spgTypes
        constraintRepository.queryById(_) >> constraints
        propertyRepository.queryRefByUniqueId(_, _) >> propertyRef

        def ret = subPropertyTypeService.queryBySubjectId(subjectIds, SPGOntologyEnum.PROPERTY)

        then:
        assertNotNull(ret)
        ret.size() == size

        where:
        subjectIds | simpleSubProperties                                                                     | spgTypes      | constraints | propertyRef           || size
        []         | []                                                                                      | []            | []          | []                    || 0
        [111]      | []                                                                                      | []            | []          | []                    || 0
        [111]      | [com.antgroup.openspg.core.spgschema.service.model.SimpleSubPropertyMockFactory.mock()] | mockSpgType() | []          | mockPropertyTypeRef() || 1
    }

    private SubProperty newSubProperty() {
        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig()
        advancedConfig.setConstraint(new Constraint())
        return new SubProperty(null, null, null, advancedConfig)
    }

    private List<SimpleSPGType> mockSpgType() {
        List<SimpleSPGType> spgTypes = new ArrayList<>();
        spgTypes.add(SimpleSpgTypeMockFactory.mockSimpleEntity(1L))
        spgTypes.addAll(SimpleSpgTypeMockFactory.mockBasicTypes())
        return spgTypes
    }

    private List<PropertyRef> mockPropertyTypeRef() {
        Property property = com.antgroup.openspg.core.spgschema.service.model.PropertyMockFactory.mockEntityProperty(
                "xxx", 6L, "aaa", 1L)
        property.setOntologyId(new OntologyId(100L))
        return [property.toRef()]
    }
}
