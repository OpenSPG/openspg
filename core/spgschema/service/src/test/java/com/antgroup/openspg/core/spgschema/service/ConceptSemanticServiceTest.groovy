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

import com.antgroup.openspg.common.model.UserInfo
import com.antgroup.openspg.core.spgschema.model.identifier.ConceptIdentifier
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.spgschema.model.semantic.*
import com.antgroup.openspg.core.spgschema.service.concept.impl.ConceptSemanticServiceImpl
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository
import com.antgroup.openspg.core.spgschema.service.semantic.LogicalRuleService
import com.antgroup.openspg.core.spgschema.service.semantic.model.LogicalCausationQuery
import com.antgroup.openspg.core.spgschema.service.semantic.model.SimpleSemantic
import com.antgroup.openspg.core.spgschema.service.semantic.repository.SemanticRepository
import org.assertj.core.util.Lists
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertEquals

class ConceptSemanticServiceTest extends Specification {
    def semanticRepository = Mock(SemanticRepository.class)
    def logicalRuleService = Mock(LogicalRuleService.class)
    def propertyRepository = Mock(PropertyRepository.class)

    def conceptSemanticService = new ConceptSemanticServiceImpl(
            semanticRepository: semanticRepository,
            logicalRuleService: logicalRuleService,
            propertyRepository: propertyRepository
    )

    def "test queryDynamicTaxonomySemantic"() {
        given:
        SPGTypeIdentifier conceptTypeIdentifier = SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson")
        ConceptIdentifier conceptIdentifier = new ConceptIdentifier("中产阶级")

        when:
        propertyRepository.queryUniqueIdByPO(_, _) >> 100L
        semanticRepository.queryConceptSemanticByCond(_) >> [new SimpleSemantic(
                SPGOntologyEnum.CONCEPT, "100", "中产阶级",
                new PredicateIdentifier("belongTo"),
                null, conceptTypeIdentifier,
                new RuleCode("RULE_XXX"))]
        logicalRuleService.queryByRuleCode(_) >> [new LogicalRule(
                new RuleCode("RULE_XXX"), 1,
                null, true, RuleStatusEnum.PROD,
                mockDynamicTaxonomyDSL(),
                new UserInfo("138192", null))]
        def semantics = conceptSemanticService.queryDynamicTaxonomySemantic(
                conceptTypeIdentifier, conceptIdentifier)

        then:
        assertEquals(1, semantics.size())
        assertEquals(conceptTypeIdentifier, semantics.get(0).getConceptTypeIdentifier())
        assertEquals(conceptIdentifier, semantics.get(0).getConceptIdentifier())
        assertEquals(new PredicateIdentifier("belongTo"), semantics.get(0).getPredicateIdentifier())
        assertEquals(mockDynamicTaxonomyDSL(), semantics.get(0).getLogicalRule().getContent())
    }

    def "test deleteDynamicTaxonomySemantic"() {
        given:
        SPGTypeIdentifier conceptTypeIdentifier = SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson")
        ConceptIdentifier conceptIdentifier = new ConceptIdentifier("中产阶级")

        when:
        propertyRepository.queryUniqueIdByPO(_, _) >> 100L
        semanticRepository.queryConceptSemanticByCond(_) >> [new SimpleSemantic(
                SPGOntologyEnum.CONCEPT, "100", "男",
                new PredicateIdentifier("belongTo"),
                null, conceptTypeIdentifier,
                new RuleCode("RULE_XXX"))]
        logicalRuleService.queryByRuleCode(_) >> [new LogicalRule(
                new RuleCode("RULE_XXX"), 1,
                null, true, RuleStatusEnum.PROD,
                mockDynamicTaxonomyDSL(),
                new UserInfo("138192", null))]
        logicalRuleService.deleteByRuleId(_) >> 1
        semanticRepository.deleteByObject(_, _, _, _) >> 1
        def cnt = conceptSemanticService.deleteDynamicTaxonomySemantic(conceptTypeIdentifier, conceptIdentifier)

        then:
        assertEquals(1, cnt)
    }

    def "test upsertDynamicTaxonomySemantic"() {
        given:
        SPGTypeIdentifier conceptTypeIdentifier = SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson")
        ConceptIdentifier conceptIdentifier = new ConceptIdentifier("中产阶级")

        LogicalRule logicalRule = new LogicalRule(null, null, mockDynamicTaxonomyDSL())
        DynamicTaxonomySemantic semantic = new DynamicTaxonomySemantic(
                conceptTypeIdentifier, conceptIdentifier, logicalRule)

        when:
        propertyRepository.queryUniqueIdByPO(_, _) >> 100L
        semanticRepository.queryConceptSemanticByCond(_) >> []
        logicalRuleService.create(_) >> 1
        semanticRepository.saveOrUpdate(_) >> 1
        def cnt = conceptSemanticService.upsertDynamicTaxonomySemantic(semantic)

        then:
        assertEquals(1, cnt)
    }

    def "test queryLogicalCausationSemantic"() {
        given:
        SPGTypeIdentifier conceptTypeIdentifier = SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson")

        LogicalCausationQuery query = new LogicalCausationQuery()
                .setSubjectTypeNames(Lists.newArrayList("DEFAULT.TaxonomyOfPerson"))
                .setSubjectName("中产阶级")
                .setObjectTypeNames(Lists.newArrayList("DEFAULT.TaxonomyOfPerson"))
                .setObjectName("资产阶级")
                .setPredicateName("leadTo")

        when:
        semanticRepository.queryConceptSemanticByCond(_) >> [new SimpleSemantic(
                SPGOntologyEnum.CONCEPT, "中产阶级", "资产阶级",
                new PredicateIdentifier("leadTo"),
                conceptTypeIdentifier,
                conceptTypeIdentifier,
                new RuleCode("RULE_XXX"))]
        logicalRuleService.queryByRuleCode(_) >> [new LogicalRule(
                new RuleCode("RULE_XXX"), 1,
                null, true, RuleStatusEnum.PROD,
                mockLogicalCausationDSL(),
                new UserInfo("138192", null))]
        def semantics = conceptSemanticService.queryLogicalCausationSemantic(query)

        then:
        assertEquals(1, semantics.size())
        assertEquals(conceptTypeIdentifier, semantics.get(0).getSubjectTypeIdentifier())
        assertEquals(new ConceptIdentifier("中产阶级"), semantics.get(0).getSubjectIdentifier())
        assertEquals(new PredicateIdentifier("leadTo"), semantics.get(0).getPredicateIdentifier())
        assertEquals(conceptTypeIdentifier, semantics.get(0).getObjectTypeIdentifier())
        assertEquals(new ConceptIdentifier("资产阶级"), semantics.get(0).getObjectIdentifier())
        assertEquals(mockLogicalCausationDSL(), semantics.get(0).getLogicalRule().getContent())
    }

    def "test deleteLogicalCausationSemantic"() {
        given:
        SPGTypeIdentifier conceptTypeIdentifier = SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson")
        LogicalRule logicalRule = new LogicalRule(
                null, null,
                mockLogicalCausationDSL())
        LogicalCausationSemantic logicalCausationSemantic = new LogicalCausationSemantic(
                conceptTypeIdentifier,
                new ConceptIdentifier("中产阶级"),
                new PredicateIdentifier("leadTo"),
                conceptTypeIdentifier,
                new ConceptIdentifier("资产阶级"),
                logicalRule
        )

        when:
        logicalRuleService.deleteByRuleId(_) >> 1
        semanticRepository.queryConceptSemanticByCond(_) >> [new SimpleSemantic(
                SPGOntologyEnum.CONCEPT, "中产阶级", "资产阶级",
                new PredicateIdentifier("leadTo"),
                conceptTypeIdentifier,
                conceptTypeIdentifier,
                new RuleCode("RULE_XXX"))]
        logicalRuleService.queryByRuleCode(_) >> [new LogicalRule(
                new RuleCode("RULE_XXX"), 1,
                null, true, RuleStatusEnum.PROD,
                mockLogicalCausationDSL(),
                new UserInfo("138192", null))]
        semanticRepository.deleteConceptSemantic(_) >> 1
        def cnt = conceptSemanticService.deleteLogicalCausationSemantic(logicalCausationSemantic)

        then:
        assertEquals(1, cnt)
    }

    def "test upsertLogicalCausationSemantic"() {
        given:
        SPGTypeIdentifier conceptTypeIdentifier = SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfPerson")
        LogicalRule logicalRule = new LogicalRule(
                null, null,
                mockLogicalCausationDSL())
        LogicalCausationSemantic logicalCausationSemantic = new LogicalCausationSemantic(
                conceptTypeIdentifier,
                new ConceptIdentifier("中产阶级"),
                new PredicateIdentifier("leadTo"),
                conceptTypeIdentifier,
                new ConceptIdentifier("资产阶级"),
                logicalRule
        )

        when:
        semanticRepository.queryConceptSemanticByCond(_) >> []
        logicalRuleService.create(_) >> 1
        semanticRepository.saveOrUpdate(_) >> 1
        def cnt = conceptSemanticService.upsertLogicalCausationSemantic(logicalCausationSemantic)

        then:
        assertEquals(1, cnt)
    }

    String mockDynamicTaxonomyDSL() {
        return "Define (s:DEFAULT.Person)-[p:belongTo]->(o:`DEFAULT.TaxonomyOfPerson`/`中产阶级`) \" +\n" +
                "                        \"{GraphStructure{} Rule{ R1: s.age >= 40 and s.age < 50}}"
    }

    String mockLogicalCausationDSL() {
        "Define (s:`DEFAULT.TaxonomyOfPerson`/`中产阶级`)-[p:leadTo]->" +
                "(o:`DEFAULT.TaxonomyOfPerson`/`资产阶级`) " +
                "{GraphStructure{} Rule{ R1: s.age=50} \n Action {}}"
    }
}
