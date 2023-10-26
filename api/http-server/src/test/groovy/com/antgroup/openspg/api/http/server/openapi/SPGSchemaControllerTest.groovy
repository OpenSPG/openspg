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


package com.antgroup.openspg.api.http.server.openapi

import com.antgroup.openspg.api.facade.JSON
import com.antgroup.openspg.api.facade.dto.schema.request.SchemaAlterRequest
import spock.lang.Specification

/**
 *  */
class SPGSchemaControllerTest extends Specification {

    def "testSerDeSchemaAlterRequest"() {
        expect:
        def json = JSON.deserialize("{\n" +
                "  \"projectId\": 1000017,\n" +
                "  \"token\": \"8F665Ef936Ce221a\",\n" +
                "  \"schemaDraft\": {\n" +
                "    \"alterSpgTypes\": [\n" +
                "      {\n" +
                "        \"@type\": \"CONCEPT_TYPE\",\n" +
                "        \"alterOperation\": \"CREATE\",\n" +
                "        \"basicInfo\": {\n" +
                "          \"name\": {\n" +
                "            \"nameType\": \"SPG_TYPE\",\n" +
                "            \"namespace\": \"FraudTest5\",\n" +
                "            \"nameEn\": \"TaxonomyOfApp\"\n" +
                "          },\n" +
                "          \"nameZh\": \"应用程序的分类\",\n" +
                "          \"desc\": \"应用程序的分类\"\n" +
                "        },\n" +
                "        \"spgTypeEnum\": \"CONCEPT_TYPE\",\n" +
                "        \"properties\": [\n" +
                "          {\n" +
                "            \"basicInfo\": {\n" +
                "              \"name\": {\n" +
                "                \"nameType\": \"Predicate\",\n" +
                "                \"name\": \"id\"\n" +
                "              },\n" +
                "              \"nameZh\": \"实体主键\"\n" +
                "            },\n" +
                "            \"objectTypeRef\": {\n" +
                "              \"basicInfo\": {\n" +
                "                \"name\": {\n" +
                "                  \"nameType\": \"SPG_TYPE\",\n" +
                "                  \"nameEn\": \"Text\"\n" +
                "                },\n" +
                "                \"nameZh\": \"Text\"\n" +
                "              },\n" +
                "              \"spgTypeEnum\": \"BASIC_TYPE\"\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"basicInfo\": {\n" +
                "              \"name\": {\n" +
                "                \"nameType\": \"Predicate\",\n" +
                "                \"name\": \"name\"\n" +
                "              },\n" +
                "              \"nameZh\": \"名称\"\n" +
                "            },\n" +
                "            \"objectTypeRef\": {\n" +
                "              \"basicInfo\": {\n" +
                "                \"name\": {\n" +
                "                  \"nameType\": \"SPG_TYPE\",\n" +
                "                  \"nameEn\": \"Text\"\n" +
                "                },\n" +
                "                \"nameZh\": \"Text\"\n" +
                "              },\n" +
                "              \"spgTypeEnum\": \"BASIC_TYPE\"\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"conceptLayerConfig\": {\n" +
                "            \"hypernymPredicate\": \"isA\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n", SchemaAlterRequest.class)

        json.getProjectId() == 1000017L
        json.getSchemaDraft().getAlterSpgTypes().size() == 1
    }
}
