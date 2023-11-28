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


package com.antgroup.openspg.cloudext.impl.graphstore.tugraph

import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.ScriptLPGRecordQuery
import com.antgroup.openspg.cloudext.interfaces.graphstore.impl.DefaultLPGTypeNameConvertor
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation
import com.antgroup.openspg.server.common.model.api.ApiConstants
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo
import com.antgroup.openspg.common.util.CollectionsUtils
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import spock.lang.Specification

import java.util.stream.Collectors

/**
 * @see https://tugraph-db.readthedocs.io/zh_CN/latest/3.quick-start/1.preparation.html#id5
 */
class TuGraphStoreClientSpec extends Specification {

    private final static TuGraphStoreClient client = genTuGraphStoreClient();

    static def APP_VERTEX_TYPE = new CreateVertexTypeOperation("App");

    static def PERSON_VERTEX_TYPE = new CreateVertexTypeOperation("Person");

    static def OWNER_EDGE_TYPE_NAME = new EdgeTypeName("Person", "owner", "App")

    static def OWNER_EDGE_TYPE = new CreateEdgeTypeOperation(OWNER_EDGE_TYPE_NAME);

    def "createVertexTypeAndEdgeType"() {
        when:
        def PROPERTY_ID = new LPGProperty("id", BasicTypeEnum.TEXT);
        PROPERTY_ID.setOptional(false)
        PROPERTY_ID.setPrimaryKey(true)
        APP_VERTEX_TYPE.addProperty(new LPGProperty("cert", BasicTypeEnum.LONG));
        APP_VERTEX_TYPE.addProperty(new LPGProperty("amt", BasicTypeEnum.DOUBLE))
        APP_VERTEX_TYPE.addProperty(new LPGProperty("cnt", BasicTypeEnum.LONG));
        APP_VERTEX_TYPE.addProperty(PROPERTY_ID);
        APP_VERTEX_TYPE.createIndex("cert", false);
        PERSON_VERTEX_TYPE.addProperty(new LPGProperty("name", BasicTypeEnum.TEXT)); ;
        PERSON_VERTEX_TYPE.addProperty(PROPERTY_ID);
        OWNER_EDGE_TYPE.addProperty(new LPGProperty("timestamp", BasicTypeEnum.LONG));
        OWNER_EDGE_TYPE.addProperty(new LPGProperty("srcId", BasicTypeEnum.TEXT));
        OWNER_EDGE_TYPE.addProperty(new LPGProperty("dstId", BasicTypeEnum.TEXT));
        OWNER_EDGE_TYPE.addProperty(new LPGProperty("version", BasicTypeEnum.LONG));

        def lpgSchema = client.querySchema()
        def vertexLabels = lpgSchema.getVertexTypes()
                .stream()
                .map(VertexType::getTypeName)
                .collect(Collectors.toSet());
        def edgeLabels = lpgSchema.getEdgeTypes()
                .stream()
                .map(EdgeType::getTypeName)
                .collect(Collectors.toSet());
        if (!vertexLabels.contains(APP_VERTEX_TYPE.getVertexTypeName())) {
            client.createVertexType(APP_VERTEX_TYPE)
        }
        if (!vertexLabels.contains(PERSON_VERTEX_TYPE.getVertexTypeName())) {
            client.createVertexType(PERSON_VERTEX_TYPE)
        }
        if (!edgeLabels.contains(OWNER_EDGE_TYPE.getEdgeTypeName().toString())) {
            client.createEdgeType(OWNER_EDGE_TYPE)
        }

        then:
        def schema = client.querySchema()
        schema.getVertexTypes().size() == 2
        schema.getEdgeTypes().size() == 1

        CollectionsUtils.setMap(schema.getVertexTypes(), x -> x.getTypeName()) == Sets.newHashSet("App", "Person")
        CollectionsUtils.setMap(schema.getEdgeTypes(), x -> x.getTypeName()) == Sets.newHashSet("Person_owner_App")
    }

    def "upsertVertexAndEdgeRecordSuccessfully"() {
        when:
        def vertexRecord1ToUpsert = new VertexRecord("app1", "App", Lists.newArrayList(
                new LPGPropertyRecord("id", "app1"),
                new LPGPropertyRecord("cert", 123L),
                new LPGPropertyRecord("amt", 100d),
                new LPGPropertyRecord("cnt", 99L),))


        def vertexRecord2ToUpsert = new VertexRecord("person1", "Person", Lists.newArrayList(
                new LPGPropertyRecord("id", "person1"),
                new LPGPropertyRecord("name", "name1"),
        ))

        def edgeRecord1ToUpsert = new EdgeRecord(
                "person1",
                "app1",
                OWNER_EDGE_TYPE_NAME,
                Lists.newArrayList(new LPGPropertyRecord("timestamp", 1020L))
        )

        def edgeRecord2ToUpsert = new EdgeRecord(
                "person1",
                "app1",
                OWNER_EDGE_TYPE_NAME,
                Lists.newArrayList(new LPGPropertyRecord("timestamp", 1000L)),
                2L)
        client.upsertVertex("App", Lists.newArrayList(vertexRecord1ToUpsert))
        client.upsertVertex("Person", Lists.newArrayList(vertexRecord2ToUpsert))
        client.upsertEdge(OWNER_EDGE_TYPE.getEdgeTypeName().getEdgeLabel(), Lists.newArrayList(edgeRecord1ToUpsert, edgeRecord2ToUpsert))

        then:
        def result = client.queryRecord(new ScriptLPGRecordQuery("MATCH (a:App)<-[o:owner]-(p:Person) RETURN a,o,p"))
        result.getClass()
    }

    def "upsertVertexRecordButSchemaNotExisted"() {
        when:
        def vertexRecordToUpsert = new VertexRecord("app1", "App2", Lists.newArrayList(
                new LPGPropertyRecord("id", "app1"),
                new LPGPropertyRecord("cert", 123L),
                new LPGPropertyRecord("amt", 100d),
                new LPGPropertyRecord("cnt", 99L),
        ))
        client.upsertVertex("App2", Lists.newArrayList(vertexRecordToUpsert))

        then:
        def e = thrown(RuntimeException)
        e != null
    }

    def "upsertEdgeRecordButSchemaNotExisted"() {
        when:
        def edgeRecordToUpsert = new EdgeRecord(
                "person1",
                "app1",
                new EdgeTypeName("Person", "notOwner", "App"),
                Lists.newArrayList(new LPGPropertyRecord("timestamp", 1000L)))

        client.upsertEdge(
                new EdgeTypeName("Person", "notOwner", "App").getEdgeLabel(),
                Lists.newArrayList(edgeRecordToUpsert))

        then:
        def e = thrown(RuntimeException)
        e != null
    }

    def "upsertVertexRecordButPropertyValueTypeNotMatch"() {
        when:
        def vertexRecordToUpsert = new VertexRecord("app1", "App", Lists.newArrayList(
                new LPGPropertyRecord("id", "app1"),
                new LPGPropertyRecord("cert", "notMatch"),
        ))

        client.upsertVertex("App", Lists.newArrayList(vertexRecordToUpsert))

        then:
        def e = thrown(RuntimeException)
        e != null
    }

    def "upsertEdgeRecordButPropertyValueTypeNotMatch"() {
        when:
        def edgeType = new EdgeTypeName("Person", "notOwner", "App")
        def edgeRecordToUpsert = new EdgeRecord(
                "person1",
                "app1",
                edgeType,
                Lists.newArrayList(new LPGPropertyRecord("timestamp", "notMatch"),
                ))
        client.upsertEdge(edgeType.getEdgeLabel(), Lists.newArrayList(edgeRecordToUpsert))

        then:
        def e = thrown(RuntimeException)
        e != null
    }


    def "queryRecord"() {
        expect:
        def tuGraphStoreClient = genTuGraphStoreClient()
        def queryRecord = tuGraphStoreClient.queryRecord(new ScriptLPGRecordQuery(
                "MATCH (n:App)-[e:hasCert]->(m) WHERE n.id='-6294032754554815824' RETURN e;"
        ))
        queryRecord.toString() == 'TableLPGRecordStruct[cells=[],columnNames=[],recordStruct=TABLE]'
    }

    private static TuGraphStoreClient genTuGraphStoreClient() {
        GraphStoreConnectionInfo connInfo = new GraphStoreConnectionInfo();
        connInfo.setScheme("tugraph");

        Map<String, Object> params = new HashMap<>(5);
        params.put(TuGraphConstants.GRAPH_NAME, "FraudTest5")
        params.put(ApiConstants.TIMEOUT, 5000d)
        params.put(ApiConstants.HOST, "127.0.0.1:9090")
        params.put(ApiConstants.ACCESS_ID, "admin")
        params.put(ApiConstants.ACCESS_KEY, "73@TuGraph")

        connInfo.setParams(params)
        return new TuGraphStoreClient(connInfo, new DefaultLPGTypeNameConvertor())
    }
}
