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


package com.antgroup.openspg.cloudext.interfaces.searchengine.impl

import com.antgroup.openspg.cloudext.interfaces.searchengine.BaseIdxSearchEngineClient
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.schema.SPGSchema2IdxService
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.schema.impl.SPGSchema2IdxServiceImpl
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxMapping
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchema
import com.antgroup.openspg.common.util.StringUtils
import com.antgroup.openspg.core.schema.model.BasicInfo
import com.antgroup.openspg.core.schema.model.SPGSchema
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.schema.model.predicate.Property
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig
import com.antgroup.openspg.core.schema.model.type.BaseSPGType
import com.antgroup.openspg.core.schema.model.type.EntityType
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef
import com.antgroup.openspg.schema.model.type.*
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import spock.lang.Specification

class SPGSchema2IdxServiceTest extends Specification {

    private static final String SPG_TYPE_UNIQUE_NAME = "DEFAULT.Cert"
    private static final String BASIC_TYPE_TEXT_NAME = "Text"
    private static final String SPG_ROOT_TYPE_NAME = "Thing"
    private static final String PROPERTY_ID = "id"

    /**
     * Spg Type Demo
     */
    static def CERT = new EntityType(
            new BasicInfo<>(SPGTypeIdentifier.parse(SPG_TYPE_UNIQUE_NAME), StringUtils.EMPTY, StringUtils.EMPTY),
            new ParentTypeInfo(null, null,
                    SPGTypeIdentifier.parse(SPG_ROOT_TYPE_NAME), null),
            new ArrayList<Property>() {
                {
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier(PROPERTY_ID), StringUtils.EMPTY, StringUtils.EMPTY),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse(SPG_TYPE_UNIQUE_NAME), StringUtils.EMPTY, StringUtils.EMPTY),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse(BASIC_TYPE_TEXT_NAME), StringUtils.EMPTY, StringUtils.EMPTY),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()))
                }
            },
            Lists.newArrayList(),
            new SPGTypeAdvancedConfig()
    )

    /**
     * Translate alteration to create or update spg type into idx schema alterations, when the idx schema is
     *  not existed in idx search engine.
     */
    def "testCreateAndUpdateEntityWhenIdxSchemaIsNotExisted"() {
        given:
        BaseIdxSearchEngineClient idxSearchEngineClient = Mock()
        SPGSchema2IdxService schema2IdxService = new SPGSchema2IdxServiceImpl(idxSearchEngineClient)

        and:
        idxSearchEngineClient.querySchema() >> Lists.newArrayList()

        when:
        CERT.setAlterOperation(operator)
        def schema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(CERT)
                    }
                },
                Sets.newHashSet()
        )

        then:
        def alterItems = schema2IdxService.generate(schema)

        expect:
        size == alterItems.size()
        def a = alterItems.get(0)
        alterOp == a.getAlterOp()
        idxName == a.getIdxSchema().getIdxName()

        where:
        operator                  | size | alterOp                   | idxName
        AlterOperationEnum.CREATE | 1    | AlterOperationEnum.CREATE | SPG_TYPE_UNIQUE_NAME
        AlterOperationEnum.UPDATE | 1    | AlterOperationEnum.CREATE | SPG_TYPE_UNIQUE_NAME
    }

    /**
     * Translate alteration to delete spg type into idx schema alterations, when the idx schema is
     *  not existed in idx search engine.
     */
    def "testDeleteEntityWhenIdxSchemaIsNotExisted"() {
        given:
        BaseIdxSearchEngineClient idxSearchEngineClient = Mock()
        SPGSchema2IdxService schema2IdxService = new SPGSchema2IdxServiceImpl(idxSearchEngineClient)

        and:
        idxSearchEngineClient.querySchema() >> Lists.newArrayList()

        when:
        CERT.setAlterOperation(operator)
        def schema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(CERT)
                    }
                },
                Sets.newHashSet()
        )

        then:
        def alterItems = schema2IdxService.generate(schema)

        expect:
        size == alterItems.size()

        where:
        operator                  | size
        AlterOperationEnum.DELETE | 0
    }

    /**
     * Translate alteration to create or update spg type into idx schema alterations, when the idx schema is
     *  already existed in idx search engine.
     */
    def "testCreateAndUpdateEntityWhenIdxSchemaIsExisted"() {
        given:
        BaseIdxSearchEngineClient idxSearchEngineClient = Mock()
        SPGSchema2IdxService schema2IdxService = new SPGSchema2IdxServiceImpl(idxSearchEngineClient)

        and:
        idxSearchEngineClient.querySchema() >> new ArrayList<IdxSchema>() {
            {
                add(new IdxSchema(
                        SPG_TYPE_UNIQUE_NAME,
                        new IdxMapping(Lists.newArrayList())
                ))
            }
        }

        when:
        CERT.setAlterOperation(operator)
        def schema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(CERT)
                    }
                },
                Sets.newHashSet()
        )

        then:
        def alterItems = schema2IdxService.generate(schema)

        expect:
        size == alterItems.size()

        where:
        operator                  | size
        AlterOperationEnum.CREATE | 0
        AlterOperationEnum.UPDATE | 0
    }

    /**
     * Translate alteration to delete spg type into idx schema alterations, when the idx schema is
     *  already existed in idx search engine.
     */
    def "testDeleteEntityWhenIdxSchemaIsExisted"() {
        given:
        BaseIdxSearchEngineClient idxSearchEngineClient = Mock()
        SPGSchema2IdxService schema2IdxService = new SPGSchema2IdxServiceImpl(idxSearchEngineClient)

        and:
        idxSearchEngineClient.querySchema() >> new ArrayList<IdxSchema>() {
            {
                add(new IdxSchema(
                        SPG_TYPE_UNIQUE_NAME,
                        new IdxMapping(Lists.newArrayList())
                ))
            }
        }
        when:
        CERT.setAlterOperation(operator)
        def schema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(CERT)
                    }
                },
                Sets.newHashSet()
        )

        then:
        def alterItems = schema2IdxService.generate(schema)

        expect:
        size == alterItems.size()
        def a = alterItems.get(0)
        alterOp == a.getAlterOp()
        idxName == a.getIdxSchema().getIdxName()

        where:
        operator                  | size | alterOp                   | idxName
        AlterOperationEnum.DELETE | 1    | AlterOperationEnum.DELETE | SPG_TYPE_UNIQUE_NAME
    }
}
