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

package com.antgroup.openspg.biz.spgschema

import com.antgroup.openspg.biz.spgschema.impl.ConceptManagerImpl
import com.antgroup.openspg.core.spgschema.model.identifier.ConceptIdentifier
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.spgschema.model.semantic.DynamicTaxonomySemantic
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalCausationSemantic
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineDynamicTaxonomyRequest
import com.antgroup.openspg.core.spgschema.model.semantic.request.DefineLogicalCausationRequest
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveDynamicTaxonomyRequest
import com.antgroup.openspg.core.spgschema.model.semantic.request.RemoveLogicalCausationRequest
import com.antgroup.openspg.core.spgschema.service.concept.ConceptSemanticService
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertNull

class ConceptManagerTest extends Specification {
    def conceptService = Mock(ConceptSemanticService.class)
    def conceptManager = new ConceptManagerImpl(
            conceptService: conceptService
    )

    def "test defineDynamicTaxonomy"() {
        given:
        DefineDynamicTaxonomyRequest request = new DefineDynamicTaxonomyRequest();
        request.setConceptTypeName("DEFAULT.TaxonomyOfPerson")
        request.setConceptName("中产阶级")
        request.setDsl("Define (s:DEFAULT.Person)-[p:belongTo]->(o:`DEFAULT.TaxonomyOfPerson`/`中产阶级`) \" +\n" +
                "                        \"{GraphStructure{} Rule{ R1: s.age >= 40 and s.age < 50}}")
        when:
        conceptService.upsertDynamicTaxonomySemantic(_) >> 1
        def v = conceptManager.defineDynamicTaxonomy(request)

        then:
        assertNull(v)
    }

    def "test removeDynamicTaxonomy"() {
        given:
        RemoveDynamicTaxonomyRequest request = new RemoveDynamicTaxonomyRequest();
        request.setObjectConceptTypeName("DEFAULT.TaxonomyOfPerson")
        request.setObjectConceptName("中产阶级")

        when:
        conceptService.deleteDynamicTaxonomySemantic(_) >> 1
        def v = conceptManager.removeDynamicTaxonomy(request)

        then:
        assertNull(v)
    }

    def "test defineLogicalCausation"() {
        given:
        DefineLogicalCausationRequest defineLogicalCausationRequest = new DefineLogicalCausationRequest(
                subjectConceptTypeName: "DEFAULT.TaxonomyOfPerson",
                subjectConceptName: "中产阶级",
                objectConceptTypeName: "DEFAULT.TaxonomyOfPerson",
                objectConceptName: "资产阶级",
                predicateName: "leadTo",
                dsl: "Define (s:`DEFAULT.TaxonomyOfPerson`/`中产阶级`)-[p:leadTo]->" +
                        "(o:`DEFAULT.TaxonomyOfPerson`/`资产阶级`) " +
                        "{GraphStructure{} Rule{ R1: s.age=50} \n Action {}}")

        when:
        conceptService.upsertLogicalCausationSemantic(_) >> 1
        def v = conceptManager.defineLogicalCausation(defineLogicalCausationRequest)

        then:
        assertNull(v)
    }

    def "test removeLogicalCausation"() {
        given:
        RemoveLogicalCausationRequest defineLogicalCausationRequest = new RemoveLogicalCausationRequest(
                subjectConceptTypeName: "DEFAULT.TaxonomyOfPerson",
                subjectConceptName: "中产阶级",
                objectConceptTypeName: "DEFAULT.TaxonomyOfPerson",
                objectConceptName: "资产阶级",
                predicateName: "leadTo")

        when:
        conceptService.deleteLogicalCausationSemantic(_) >> 1
        def v = conceptManager.removeLogicalCausation(defineLogicalCausationRequest)

        then:
        assertNull(v)
    }

    def "test getConceptDetail"() {
        given:
        when:
        conceptService.queryLogicalCausationSemantic(_) >> [new LogicalCausationSemantic(
                SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson"),
                new ConceptIdentifier("中产阶级"),
                new PredicateIdentifier("leadTo"),
                SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson"),
                new ConceptIdentifier("资产阶级"),
                new LogicalRule(null, null,
                        "Define (s:`DEFAULT.TaxonomyOfPerson`/`中产阶级`)-[p:leadTo]->" +
                                "(o:`DEFAULT.TaxonomyOfPerson`/`资产阶级`) " +
                                "{GraphStructure{} Rule{ R1: s.age=50} \n Action {}}")
        )]

        conceptService.queryDynamicTaxonomySemantic(_, _) >> [new DynamicTaxonomySemantic(
                SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson"),
                new ConceptIdentifier("中产阶级"),
                new LogicalRule(null, null,
                        "Define (s:DEFAULT.Person)-[p:belongTo]->(o:`DEFAULT.TaxonomyOfPerson`/`中产阶级`) \" +\n" +
                                "                        \"{GraphStructure{} Rule{ R1: s.age >= 40 and s.age < 50}}")
        )]
        def conceptList = conceptManager.getConceptDetail("DEFAULT.TaxonomyOfPerson", null)

        then:
        assertEquals(1, conceptList.getConcepts().size())
        assertEquals(1, conceptList.getDynamicTaxonomyList().size())
        assertEquals(1, conceptList.getLogicalCausation().size())
    }
}
