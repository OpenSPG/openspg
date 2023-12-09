package com.antgroup.openspg.builder.model.pipeline

import com.antgroup.openspg.builder.model.BuilderJsonUtils
import com.antgroup.openspg.builder.model.pipeline.config.CsvSourceNodeConfig
import com.antgroup.openspg.builder.model.pipeline.config.GraphStoreSinkNodeConfig
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo
import com.google.common.collect.Lists
import spock.lang.Specification

class PipelineTest extends Specification {

    def "testPipelineSer"() {
        given:
        Node node1 =
                new Node(
                        "1",
                        "csv",
                        new CsvSourceNodeConfig("./data/TaxOfRiskApp.csv", 1, Lists.newArrayList("id")));
        Node node2 =
                new Node(
                        "2",
                        "mapping",
                        new SPGTypeMappingNodeConfig(
                                "RiskMining.TaxOfRiskUser", new ArrayList<>(), new ArrayList<>()));

        Map<String, Object> params = new HashMap<>();
        params.put("graphName", "default");
        params.put("timeout", "5000");
        params.put("host", "127.0.0.1");
        params.put("accessId", "admin");
        params.put("accessKey", "73@TuGraph");
        Node node3 =
                new Node("3", "sink", new GraphStoreSinkNodeConfig(new GraphStoreConnectionInfo().setScheme("tugraph")
                        .setParams(params)));

        Edge edge1 = new Edge("1", "2");
        Edge edge2 = new Edge("2", "3");

        Pipeline pipeline = new Pipeline(Lists.newArrayList(node1, node2, node3), Lists.newArrayList(edge1, edge2));

        expect:
        BuilderJsonUtils.serialize(pipeline).contains(BuilderJsonUtils.DEFAULT_TYPE_FIELD_NAME)
    }
}
