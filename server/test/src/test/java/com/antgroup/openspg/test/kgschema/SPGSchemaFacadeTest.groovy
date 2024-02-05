/*
 * Copyright 2023 OpenSPG Authors
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

import com.antgroup.openspg.server.api.facade.ApiResponse
import com.antgroup.openspg.server.api.http.client.HttpConceptFacade
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphStoreClient
import com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchEngineClient
import com.antgroup.openspg.server.common.service.datasource.DataSourceService
import com.antgroup.openspg.core.schema.model.alter.SchemaDraft
import com.antgroup.openspg.core.schema.model.predicate.Property
import com.antgroup.openspg.core.schema.model.predicate.Relation
import com.antgroup.openspg.core.schema.model.semantic.request.DefineDynamicTaxonomyRequest
import com.antgroup.openspg.core.schema.model.semantic.request.DefineLogicalCausationRequest
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveDynamicTaxonomyRequest
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveLogicalCausationRequest
import com.antgroup.openspg.core.schema.model.type.BaseSPGType
import com.antgroup.openspg.core.schema.model.type.ConceptList
import com.antgroup.openspg.core.schema.model.type.ProjectSchema
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum
import com.antgroup.openspg.test.sofaboot.SofaBootTestApplication
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Shared
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

@SpringBootTest(classes = SofaBootTestApplication, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
class SPGSchemaFacadeTest extends Specification {
    @Shared
    Long projectId = 1L
    @Shared
            spgSchemaFacade = new HttpSchemaFacade()
    @Shared
            conceptFacade = new HttpConceptFacade()

    @MockBean
    private DataSourceService dataSourceService

    def setupSpec() {
        HttpClientBootstrap.init(new ConnectionInfo("http://127.0.0.1:8887")
                .setConnectTimeout(60000).setReadTimeout(60000)
        )
    }

    /**
     * step 1: query project schema, check system built-in BasicType and Standard is inited;
     * step 2: create new StandardType、EntityType、ConceptType、EventType;
     * step 3: define taxonomy semantic and logic causation semantic on concept;
     * step 4: update or delete some StandardType、EntityType、ConceptType、EventType;
     * step 5: query a single spg type, query a single relation;
     * step 6: delete all customized StandardType、EntityType、ConceptType、EventType;
     */
    def "test"() {
        given:
        Mockito.doReturn(Mock(TuGraphStoreClient.class))
                .when(dataSourceService)
                .buildSharedKgStoreClient()
        Mockito.doReturn(Mock(ElasticSearchEngineClient.class))
                .when(dataSourceService)
                .buildSharedSearchEngineClient()

        when:
        // step 1
        ProjectSchema projectSchema = this.getProjectSchema()
        MockSchemaResultValidator.checkInitResult(projectSchema.getSpgTypes())

        BuiltInPropertyRequest builtInPropertyRequest = new BuiltInPropertyRequest(
                spgTypeEnum: SPGTypeEnum.CONCEPT_TYPE.name()
        )
        ApiResponse<List<Property>> apiResponse = spgSchemaFacade.queryBuiltInProperty(builtInPropertyRequest)
        assertEquals(2, apiResponse.getData().size())

        // step 2
        SchemaDraft createDraft = MockSchemaDraftFactory.buildCreateDraft()
        SchemaAlterRequest schemaAlterRequest = new SchemaAlterRequest(
                projectId: projectId, schemaDraft: createDraft)
        spgSchemaFacade.alterSchema(schemaAlterRequest)

        projectSchema = this.getProjectSchema()
        MockSchemaResultValidator.checkCreateResult(projectSchema.getSpgTypes())

        //step 3
        DefineDynamicTaxonomyRequest defineDynamicTaxonomyRequest1 = new DefineDynamicTaxonomyRequest(
                conceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                conceptName: "中产阶级",
                dsl: "Define (s:DEFAULT.Person)-[p:belongTo]->(o:`DEFAULT.TaxonomyOfPerson`/`中产阶级`) " +
                        "{GraphStructure{} Rule{ R1: s.age >= 40 and s.age < 50}}")
        conceptFacade.defineDynamicTaxonomy(defineDynamicTaxonomyRequest1)

        DefineDynamicTaxonomyRequest defineDynamicTaxonomyRequest2 = new DefineDynamicTaxonomyRequest(
                conceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                conceptName: "资产阶级",
                dsl: "Define (s:DEFAULT.Person)-[p:belongTo]->" +
                        "(o:`DEFAULT.TaxonomyOfPerson`/`资产阶级`) " +
                        "{GraphStructure{} Rule{ R1: s.age >= 50}}")
        conceptFacade.defineDynamicTaxonomy(defineDynamicTaxonomyRequest2)

        DefineLogicalCausationRequest defineLogicalCausationRequest = new DefineLogicalCausationRequest(
                subjectConceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                subjectConceptName: "中产阶级",
                objectConceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                objectConceptName: "资产阶级",
                predicateName: "leadTo",
                dsl: "Define (s:`DEFAULT.TaxonomyOfPerson`/`中产阶级`)-[p:leadTo]->" +
                        "(o:`DEFAULT.TaxonomyOfPerson`/`资产阶级`) " +
                        "{GraphStructure{} Rule{ R1: s.age=50} \n Action {}}")
        conceptFacade.defineLogicalCausation(defineLogicalCausationRequest)

        ConceptRequest conceptRequest = new ConceptRequest(
                conceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                conceptName: "中产阶级"
        )
        ApiResponse<ConceptList> conceptResponse = conceptFacade.queryConcept(conceptRequest)
        assertEquals(1, conceptResponse.getData().getConcepts().size())
        assertEquals(2, conceptResponse.getData().getConcepts().get(0).getSemantics().size())

        RemoveDynamicTaxonomyRequest removeDynamicTaxonomyRequest = new RemoveDynamicTaxonomyRequest(
                objectConceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName())
        conceptFacade.removeDynamicTaxonomy(removeDynamicTaxonomyRequest)

        RemoveLogicalCausationRequest removeLogicalCausationRequest = new RemoveLogicalCausationRequest(
                subjectConceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                subjectConceptName: "中产阶级",
                objectConceptTypeName: MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.getName(),
                objectConceptName: "资产阶级",
                predicateName: "leadTo")
        conceptFacade.removeLogicalCausation(removeLogicalCausationRequest)

        conceptResponse = conceptFacade.queryConcept(conceptRequest)
        assertEquals(0, conceptResponse.getData().getConcepts().size())

        // step 4
        SchemaDraft updateDraft = MockSchemaDraftFactory.buildUpdateDraft(projectSchema)
        schemaAlterRequest = new SchemaAlterRequest(
                projectId: projectId, schemaDraft: updateDraft)
        spgSchemaFacade.alterSchema(schemaAlterRequest)

        projectSchema = this.getProjectSchema()
        MockSchemaResultValidator.checkUpdateResult(projectSchema.getSpgTypes())

        // step 5
        SPGTypeRequest request = new SPGTypeRequest(name: MockSpgTypeNameEnum.DEFAULT_ALIPAY_USER.getName())
        ApiResponse<BaseSPGType> response = spgSchemaFacade.querySPGType(request)
        assertNotNull(response.getData())
        assertEquals(MockSpgTypeNameEnum.DEFAULT_ALIPAY_USER.getName(), response.getData().getName())

        RelationRequest relationRequest = new RelationRequest(
                sName: MockSpgTypeNameEnum.DEFAULT_ALIPAY_USER.getName(),
                relation: "regAddress",
                oName: MockSpgTypeNameEnum.DEFAULT_ADMINISTRATION.getName())
        ApiResponse<Relation> relationResponse = spgSchemaFacade.queryRelation(relationRequest)
        assertNotNull(relationResponse.getData())

        // step 6
        projectSchema = this.getProjectSchema()
        SchemaDraft deleteDraft = MockSchemaDraftFactory.buildDeleteDraft(projectSchema)
        schemaAlterRequest = new SchemaAlterRequest(
                projectId: projectId, schemaDraft: deleteDraft)
        spgSchemaFacade.alterSchema(schemaAlterRequest)

        projectSchema = this.getProjectSchema()
        MockSchemaResultValidator.checkInitResult(projectSchema.getSpgTypes())

        then:
        assertNotNull(this.getProjectSchema())
    }

    ProjectSchema getProjectSchema() {
        ProjectSchemaRequest projectSchemaRequest = new ProjectSchemaRequest(projectId: projectId)
        ApiResponse<ProjectSchema> projectSchemaResponse =
                spgSchemaFacade.queryProjectSchema(projectSchemaRequest)

        assertNotNull(projectSchemaResponse)
        return projectSchemaResponse.getData()
    }
}
