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


package com.antgroup.openspg.test.kgschema

import com.antgroup.openspg.common.util.StringUtils
import com.antgroup.openspg.schema.model.constraint.RegularConstraint
import com.antgroup.openspg.schema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.schema.model.identifier.SPGTripleIdentifier
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.schema.model.predicate.Property
import com.antgroup.openspg.schema.model.predicate.Relation

import java.util.function.Function
import java.util.stream.Collectors

class MockSchemaResultValidator {

    static void checkInitResult(List<BaseSPGType> spgTypes) {
        Map<String, BaseSPGType> spgTypeMap = spgTypes.stream()
                .collect(Collectors.toMap((e -> e.getName()), Function.identity()))
        List<MockSpgTypeNameEnum> basicTypes = MockSpgTypeNameEnum.getBasicType()
        for (MockSpgTypeNameEnum basicType : basicTypes) {
            assertTrue(spgTypeMap.containsKey(basicType.getName()))
        }

        List<MockSpgTypeNameEnum> standardTypes = MockSpgTypeNameEnum.getInitStandardType()
        for (MockSpgTypeNameEnum standardType : standardTypes) {
            assertTrue(spgTypeMap.containsKey(standardType.getName()))
        }
    }

    static void checkCreateResult(List<BaseSPGType> spgTypes) {
        Map<String, BaseSPGType> spgTypeMap = spgTypes.stream()
                .collect(Collectors.toMap((e -> e.getName()), Function.identity()))
        List<MockSpgTypeNameEnum> types = MockSpgTypeNameEnum.getCustomizedType()
        for (MockSpgTypeNameEnum type : types) {
            assertTrue(spgTypeMap.containsKey(type.getName()))
        }

        //check standard type
        BaseSPGType alipayIdType = getSpgType(spgTypes, MockSpgTypeNameEnum.STD_ALIPAY_ID)
        assertTrue(alipayIdType instanceof StandardType)
        StandardType alipayId = (StandardType) alipayIdType
        assertTrue(alipayId.getSpreadable())
        assertTrue(alipayId.getConstraintItems().size() == 1)
        assertTrue(alipayId.getConstraintItems().get(0) instanceof RegularConstraint)
        assertTrue(StringUtils.isNotBlank(
                ((RegularConstraint) (alipayId.getConstraintItems().get(0))).getRegularPattern()))

        //check entity type
        BaseSPGType alipayMemberType = getSpgType(spgTypes, MockSpgTypeNameEnum.DEFAULT_ALIPAY_MEMBER)
        assertTrue(alipayMemberType instanceof EntityType)
        EntityType alipayMember = (EntityType) alipayMemberType
        assertEquals(14, alipayMember.getProperties().size())
        assertEquals(12, alipayMember.getProperties().stream().filter(e -> e.inherited).count())
        assertEquals(8L, alipayMember.getRelations().size())
        assertEquals(8L, alipayMember.getRelations().stream().filter(e -> e.semanticRelation).count())
        assertEquals(7L, alipayMember.getRelations().stream().filter(e -> e.inherited).count())
        assertNotNull(alipayMember.getLinkOperator())

        //check concept type
        BaseSPGType memberDegreeType = getSpgType(spgTypes, MockSpgTypeNameEnum.DEFAULT_MEMBER_DEGREE)
        assertTrue(memberDegreeType instanceof ConceptType)
        ConceptType memberDegree = (ConceptType) memberDegreeType
        assertNotNull(memberDegree.getConceptMultiVersionConfig())
        assertEquals("yyyyMMdd", memberDegree.getConceptMultiVersionConfig().getPattern())
        assertEquals(1, memberDegree.getConceptMultiVersionConfig().getMaxVersion())
        assertEquals(3, memberDegree.getConceptMultiVersionConfig().getTtl())
        assertNotNull(memberDegree.getConceptLayerConfig())
        assertEquals("isA", memberDegree.getConceptLayerConfig().getHypernymPredicate())
        assertNotNull(memberDegree.getNormalizedOperator())
        assertEquals(5L, memberDegree.getProperties().size())
        assertEquals(1L, memberDegree.getRelations().size())

        //check event type
        BaseSPGType exchangeType = getSpgType(spgTypes, MockSpgTypeNameEnum.DEFAULT_EXCHANGE_GOODS)
        assertTrue(exchangeType instanceof EventType)
        EventType exchange = (EventType) exchangeType
        assertEquals(3, exchange.getSubjectProperties().size())
        assertEquals(1, exchange.getObjectProperties().size())
        assertEquals(1, exchange.getTimeProperties().size())
        assertEquals(3, exchange.getRelations().size())
        assertNotNull(exchange.getLinkOperator())
        assertNotNull(exchange.getExtractOperator())
    }

    static void checkUpdateResult(List<BaseSPGType> spgTypes) {
        Map<String, BaseSPGType> spgTypeMap = spgTypes.stream()
                .collect(Collectors.toMap((e -> e.getName()), Function.identity()))
        List<MockSpgTypeNameEnum> types = MockSpgTypeNameEnum.getCustomizedType()
        for (MockSpgTypeNameEnum type : types) {
            if (MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON == type) {
                assertTrue(!spgTypeMap.containsKey(type.getName()))
            } else {
                assertTrue(spgTypeMap.containsKey(type.getName()))
            }
        }
        assertTrue(spgTypeMap.containsKey("DEFAULT.TaobaoMember"))

        BaseSPGType alipayUser = getSpgType(spgTypes, MockSpgTypeNameEnum.DEFAULT_ALIPAY_USER)
        assertTrue(alipayUser instanceof EntityType)
        assertEquals("支付宝新用户", alipayUser.getBasicInfo().getNameZh())

        SPGTripleIdentifier lastVisitTripleName = new SPGTripleIdentifier(alipayUser.getBaseSpgIdentifier(),
                new PredicateIdentifier("lastVisit"),
                SPGTypeIdentifier.parse(MockSpgTypeNameEnum.STD_TIME_STAMP.getName()))
        Property Property = alipayUser.getPropertyByName(lastVisitTripleName)
        assertNotNull(Property)

        SPGTripleIdentifier mobileTripleName = new SPGTripleIdentifier(alipayUser.getBaseSpgIdentifier(),
                new PredicateIdentifier("regMobile"),
                SPGTypeIdentifier.parse(MockSpgTypeNameEnum.STD_MOBILE.name))
        Property mobileProp = alipayUser.getPropertyByName(mobileTripleName)
        assertEquals(1, mobileProp.getAdvancedConfig().getSubProperties().size())

        SPGTripleIdentifier collectAppTripleName = new SPGTripleIdentifier(alipayUser.getBaseSpgIdentifier(),
                new PredicateIdentifier("collectApp"),
                SPGTypeIdentifier.parse(MockSpgTypeNameEnum.DEFAULT_APP.getName()))
        Relation collectApp = alipayUser.getRelationByName(collectAppTripleName)
        assertNull(collectApp)

        BaseSPGType person = getSpgType(spgTypes, MockSpgTypeNameEnum.DEFAULT_PERSON)
        SPGTripleIdentifier ageTripleName = new SPGTripleIdentifier(person.getBaseSpgIdentifier(),
                new PredicateIdentifier("age"),
                SPGTypeIdentifier.parse("Integer"))
        Property age = person.getPropertyByName(ageTripleName)
        assertNull(age.getConstraint())
    }

    static BaseSPGType getSpgType(List<BaseSPGType> spgTypes, MockSpgTypeNameEnum name) {
        for (BaseSPGType spgType : spgTypes) {
            if (name.getName() == spgType.getName()) {
                return spgType
            }
        }
        return null
    }
}
