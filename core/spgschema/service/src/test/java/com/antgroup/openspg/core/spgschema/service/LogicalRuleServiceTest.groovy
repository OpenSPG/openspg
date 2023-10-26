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

import com.antgroup.openspg.common.model.exception.OpenSPGException
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule
import com.antgroup.openspg.core.spgschema.model.semantic.RuleCode
import com.antgroup.openspg.core.spgschema.service.semantic.impl.LogicalRuleServiceImpl
import com.antgroup.openspg.core.spgschema.service.semantic.repository.LogicalRuleRepository
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertEquals

class LogicalRuleServiceTest extends Specification {
    def logicalRuleRepository = Mock(LogicalRuleRepository.class)
    def logicalRuleService = new LogicalRuleServiceImpl(logicalRuleRepository: logicalRuleRepository)

    def "test create valid rule"() {
        given:
        LogicalRule logicalRule = new LogicalRule(null, "test", getValidDsl())
        logicalRuleRepository.save(_) >> 1

        when:
        def cnt = logicalRuleService.create(logicalRule)

        then:
        assertEquals(1, cnt)
    }

    def "test create invalid rule"() {
        given:
        LogicalRule logicalRule = new LogicalRule(null, "test", dsl)
        logicalRuleRepository.save(_) >> 1

        when:
        logicalRuleService.create(logicalRule)

        then:
        thrown(expectedException)

        where:
        dsl             || expectedException
        getInValidDsl() || OpenSPGException
    }

    def "test update"() {
        given:
        LogicalRule logicalRule = new LogicalRule(new RuleCode(RuleCode.genRuleCode()), "test", getValidDsl())
        logicalRuleRepository.update(_) >> 1

        when:
        def cnt = logicalRuleService.update(logicalRule)

        then:
        assertEquals(1, cnt)
    }

    def "test delete"() {
        given:
        LogicalRule logicalRule = new LogicalRule(new RuleCode(RuleCode.genRuleCode()), "test", null)
        logicalRuleRepository.delete(_) >> 1

        when:
        def cnt = logicalRuleService.delete(logicalRule)

        then:
        assertEquals(1, cnt)
    }

    def "test deleteByRuleCode"() {
        given:
        logicalRuleRepository.delete(_, _) >> size

        when:
        def cnt = logicalRuleService.deleteByRuleId(ruleCodes)

        then:
        cnt == t

        where:
        size | ruleCodes                                                                    || t
        0    | []                                                                           || 0
        2    | [new RuleCode(RuleCode.genRuleCode()), new RuleCode(RuleCode.genRuleCode())] || 2
    }

    String getValidDsl() {
        return "Define (s:DEFAULT.Person)-[p:belongTo]->(o:`DEFAULT.TaxonomyOfPerson`/`中产阶级`) \" +\n" +
                "                        \"{GraphStructure{} Rule{ R1: s.age >= 40 and s.age < 50}}"
    }

    String getInValidDsl() {
        return "IDefine (s:DEFAULT.Person)-[p:belongTo]->(o:`DEFAULT.TaxonomyOfPerson`/`中产阶级`) \" +\n" +
                "                        \"{GraphStructure{} Rule{ R1: s.age >= 40 and s.age < 50}}"
    }
}
