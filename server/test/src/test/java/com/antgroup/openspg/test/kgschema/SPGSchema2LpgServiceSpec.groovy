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

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphConstants
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphStoreClient
import com.antgroup.openspg.cloudext.interfaces.graphstore.impl.DefaultLPGTypeNameConvertor
import com.antgroup.openspg.core.schema.model.predicate.Property
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig
import com.antgroup.openspg.core.schema.model.predicate.PropertyRef
import com.antgroup.openspg.core.schema.model.predicate.Relation
import com.antgroup.openspg.core.schema.model.predicate.SubProperty
import com.antgroup.openspg.core.schema.model.type.BaseSPGType
import com.antgroup.openspg.core.schema.model.type.ConceptLayerConfig
import com.antgroup.openspg.core.schema.model.type.ConceptTaxonomicConfig
import com.antgroup.openspg.core.schema.model.type.ConceptType
import com.antgroup.openspg.core.schema.model.type.EntityType
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef
import com.antgroup.openspg.server.api.facade.ApiConstants
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo
import com.antgroup.openspg.core.schema.model.BasicInfo
import com.antgroup.openspg.core.schema.model.SPGSchema
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import spock.lang.Specification

class SPGSchema2LpgServiceSpec extends Specification {

    private final static TuGraphStoreClient client = genTuGraphStoreClient();

    static def CERT = new EntityType(
            new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.Cert"), "证书", "desc"),
            new ParentTypeInfo(null, null,
                    SPGTypeIdentifier.parse("Thing"), null),
            new ArrayList<Property>() {
                {
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("id"), "id", "desc id"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.Cert"), "证书", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("Text"), "文本", "描述"),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()));
                }
            },
            Lists.newArrayList(),
            new SPGTypeAdvancedConfig()
    )

    static def APP = new EntityType(
            new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.App"), "App应用", "desc"),
            new ParentTypeInfo(null, null,
                    SPGTypeIdentifier.parse("Thing"), null),
            new ArrayList<Property>() {
                {
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("id"), "id", "desc id"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.App"), "App应用", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("Text"), "文本", "描述"),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()));
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("hasCert"), "所用证书", "desc"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.App"), "App应用", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.Cert"), "证书", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            false,
                            new PropertyAdvancedConfig()))
                }
            },
            Lists.newArrayList(),
            new SPGTypeAdvancedConfig()
    )

    static def RELEASE_APP = new Relation(
            new BasicInfo<>(new PredicateIdentifier("releaseApp"), "发布应用", "releaseApp desc"),
            new SPGTypeRef(
                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
                    SPGTypeEnum.ENTITY_TYPE),
            new SPGTypeRef(
                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.App"), "App应用", "desc"),
                    SPGTypeEnum.ENTITY_TYPE),
            false,
            new PropertyAdvancedConfig()
    )

    static def LEGAL_PERSON = new EntityType(
            new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
            new ParentTypeInfo(null, null,
                    SPGTypeIdentifier.parse("Thing"), null),
            new ArrayList<Property>() {
                {
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("id"), "id", "desc id"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("Text"), "文本", "描述"),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()));
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("age"), "age", "desc id"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("Integer"), "整数", "描述"),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()));
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("hasCert"), "持有证书", "desc"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.Cert"), "证书", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            false,
                            new PropertyAdvancedConfig()))
                }
            },
            new ArrayList<Relation>() {
                {
                    add(RELEASE_APP);
                }
            },
            new SPGTypeAdvancedConfig()
    );

    static def UPDATE_LEGAL_PERSON = new EntityType(
            new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
            new ParentTypeInfo(null, null,
                    SPGTypeIdentifier.parse("Thing"), null),
            new ArrayList<Property>() {
                {
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("age"), "age", "desc id"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("Integer"), "整数", "描述"),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()));
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("hasCert"), "持有证书", "desc"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.LegalPerson"), "公司法人", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.Cert"), "证书", "desc"),
                                    SPGTypeEnum.ENTITY_TYPE),
                            false,
                            new PropertyAdvancedConfig()))
                }
            },
            new ArrayList<Relation>() {
                {
                    add(RELEASE_APP);
                }
            },
            new SPGTypeAdvancedConfig()
    );

    static def TAXONOMY_OF_APP = new ConceptType(
            new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfApp"), "应用程序分类", "desc"),
            new ParentTypeInfo(null, null,
                    SPGTypeIdentifier.parse("Thing"), null),
            new ArrayList<Property>() {
                {
                    add(new Property(
                            new BasicInfo<>(new PredicateIdentifier("id"), "id", "desc id"),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("DEFAULT.TaxonomyOfApp"), "应用程序分类", "desc"),
                                    SPGTypeEnum.CONCEPT_TYPE),
                            new SPGTypeRef(
                                    new BasicInfo<>(SPGTypeIdentifier.parse("Text"), "文本", "描述"),
                                    SPGTypeEnum.BASIC_TYPE),
                            false,
                            new PropertyAdvancedConfig()));
                }
            },
            Lists.newArrayList(),
            new SPGTypeAdvancedConfig(),
            new ConceptLayerConfig("isA", new ArrayList<String>() {
                {
                    add("TaxonomyOfApp")
                }
            }),
            new ConceptTaxonomicConfig(true, APP.getBaseSpgIdentifier()),
            null
    );

    def "initGraphSchema"() {
        when:
        CERT.setAlterOperation(AlterOperationEnum.CREATE);
        APP.setAlterOperation(AlterOperationEnum.CREATE);
        LEGAL_PERSON.setAlterOperation(AlterOperationEnum.CREATE);
        TAXONOMY_OF_APP.setAlterOperation(AlterOperationEnum.CREATE);
        RELEASE_APP.setAlterOperation(AlterOperationEnum.CREATE)
        RELEASE_APP.getSubProperties().addAll(mock(RELEASE_APP.toRef()))
        def spgSchema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(CERT);
                        add(APP);
                        add(LEGAL_PERSON);
                        add(TAXONOMY_OF_APP);
                    }
                },
                Sets.newHashSet()
        )
        def cmd1 = new SPGSchemaAlterCmd(spgSchema);

        then:
        def ret = client.alterSchema(cmd1)
        def lpgSchema = client.querySchema()

        expect:
        ret == 0;
        lpgSchema.edgeTypes.size() == 1;
        lpgSchema.vertexTypes.size() == 4;
    }

    def "deleteSchema"() {
        when:
        CERT.setAlterOperation(AlterOperationEnum.DELETE);
        APP.setAlterOperation(AlterOperationEnum.DELETE);
        LEGAL_PERSON.setAlterOperation(AlterOperationEnum.DELETE);
        TAXONOMY_OF_APP.setAlterOperation(AlterOperationEnum.DELETE);
        RELEASE_APP.setAlterOperation(AlterOperationEnum.DELETE)
        def spgSchema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(CERT);
                        add(APP);
                        add(LEGAL_PERSON);
                        add(TAXONOMY_OF_APP);
                    }
                },
                Sets.newHashSet()
        )
        def cmd1 = new SPGSchemaAlterCmd(spgSchema);

        then:
        def ret = client.alterSchema(cmd1)
        def lpgSchema = client.querySchema()

        expect:
        ret == 0;
        lpgSchema.edgeTypes.size() == 0;
        lpgSchema.vertexTypes.size() == 0;
    }

    def "repeatedlyDropVertexType"() {
        when:
        APP.setAlterOperation(AlterOperationEnum.CREATE);
        LEGAL_PERSON.setAlterOperation(AlterOperationEnum.CREATE);
        def schemaToCreate = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(APP);
                        add(LEGAL_PERSON);
                    }
                },
                Sets.newHashSet()
        )
        def cmd1 = new SPGSchemaAlterCmd(schemaToCreate);
        def ret1 = client.alterSchema(cmd1)

        then:
        APP.setAlterOperation(AlterOperationEnum.DELETE);
        LEGAL_PERSON.setAlterOperation(AlterOperationEnum.DELETE);
        def schemaToDelete = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(APP);
                        add(APP);
                        add(LEGAL_PERSON);
                        add(LEGAL_PERSON);
                    }
                },
                Sets.newHashSet()
        )
        def cmd2 = new SPGSchemaAlterCmd(schemaToDelete);
        notThrown(TuGraphDbRpcException)
        def ret2 = client.alterSchema(cmd2)

        expect:
        ret1 == 0
        ret2 == 0
    }

    def "dropProperty"() {
        when:
        UPDATE_LEGAL_PERSON.setAlterOperation(AlterOperationEnum.UPDATE)
        for (Property propertyType : UPDATE_LEGAL_PERSON.getProperties()) {
            propertyType.setAlterOperation(AlterOperationEnum.DELETE);
        }
        RELEASE_APP.getSubProperties().addAll(mock(RELEASE_APP.toRef()))
        for (Relation relationType : UPDATE_LEGAL_PERSON.getRelations()) {
            relationType.setAlterOperation(AlterOperationEnum.UPDATE)
            for (SubProperty subPropertyType : relationType.getSubProperties()) {
                subPropertyType.setAlterOperation(AlterOperationEnum.DELETE);
            }
        }
        def spgSchema = new SPGSchema(
                new ArrayList<BaseSPGType>() {
                    {
                        add(UPDATE_LEGAL_PERSON);
                    }
                },
                Sets.newHashSet()
        )
        def cmd1 = new SPGSchemaAlterCmd(spgSchema);

        then:
        def ret = client.alterSchema(cmd1)
        def lpgSchema = client.querySchema()

        expect:
        1 == 1
    }

    private static TuGraphStoreClient genTuGraphStoreClient() {
        GraphStoreConnectionInfo connInfo = new GraphStoreConnectionInfo();
        connInfo.setScheme("tugraph");

        Map<String, Object> params = new HashMap<>(5);
        params.put(TuGraphConstants.GRAPH_NAME, "Spg2LpgTest")
        params.put(ApiConstants.TIMEOUT, 5000d)
        params.put(ApiConstants.HOST, "127.0.0.1:9090")
        params.put(ApiConstants.ACCESS_ID, "admin")
        params.put(ApiConstants.ACCESS_KEY, "73@TuGraph")

        connInfo.setParams(params)
        return new TuGraphStoreClient(connInfo, new DefaultLPGTypeNameConvertor())
    }

    private static SubProperty mockSingle(String objectType, PropertyRef propertyTypeRef) {
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
                new PredicateIdentifier("sub_proper" + objectType), "子属性", "desc");
        SPGTypeRef objectTypeRef = new SPGTypeRef(new BasicInfo<>(
                SPGTypeIdentifier.parse(objectType), "name", "desc"), SPGTypeEnum.BASIC_TYPE);
        return new SubProperty(basicInfo, propertyTypeRef, objectTypeRef, null);
    }

    private static List<SubProperty> mock(PropertyRef propertyTypeRef) {
        return Lists.newArrayList(
                mockText(propertyTypeRef),
                mockInteger(propertyTypeRef),
                mockFloat(propertyTypeRef)
        );
    }

    private static SubProperty mockText(PropertyRef propertyTypeRef) {
        return mockSingle("Text", propertyTypeRef);
    }

    private static SubProperty mockInteger(PropertyRef propertyTypeRef) {
        return mockSingle("Integer", propertyTypeRef);
    }

    private static SubProperty mockFloat(PropertyRef propertyTypeRef) {
        return mockSingle("Float", propertyTypeRef);
    }
}
