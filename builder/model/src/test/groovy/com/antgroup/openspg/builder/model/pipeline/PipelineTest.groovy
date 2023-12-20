package com.antgroup.openspg.builder.model.pipeline

import com.antgroup.openspg.builder.model.BuilderJsonUtils
import com.antgroup.openspg.builder.model.pipeline.config.*
import spock.lang.Specification

class PipelineTest extends Specification {

    def testTaxOfRiskAppSer() {
        given:
        Node node1 = new Node("1", "csv", new CsvSourceNodeConfig(
                "./data/TaxOfRiskApp.csv", 2, ["id"]));
        Node node2 = new Node("2", "mapping",
                new SPGTypeMappingNodeConfig("RiskMining.TaxOfRiskUser", [], []));
        Node node3 = new Node("3", "sink", new GraphStoreSinkNodeConfig());


        Edge edge1 = new Edge("1", "2");
        Edge edge2 = new Edge("2", "3");

        Pipeline pipeline = new Pipeline([node1, node2, node3], [edge1, edge2]);

        expect:
        BuilderJsonUtils.serialize(pipeline) == '''{"nodes":[{"id":"1","name":"csv","nodeConfig":{"@type":"CSV_SOURCE","startRow":2,"url":"./data/TaxOfRiskApp.csv","columns":["id"],"type":"CSV_SOURCE"}},{"id":"2","name":"mapping","nodeConfig":{"@type":"SPG_TYPE_MAPPING","spgType":"RiskMining.TaxOfRiskUser","mappingFilters":[],"mappingConfigs":[],"type":"SPG_TYPE_MAPPING"}},{"id":"3","name":"sink","nodeConfig":{"@type":"GRAPH_SINK","type":"GRAPH_SINK"}}],"edges":[{"from":"1","to":"2"},{"from":"2","to":"3"}]}'''
    }

    def testAppSer() {
        given:
        Node node1 = new Node("1", "csv", new CsvSourceNodeConfig(
                "./data/App.csv", 2, ["id", "riskMark", "useCert"]));
        Node node2 = new Node("2", "mapping",
                new SPGTypeMappingNodeConfig("RiskMining.App",
                        [

                        ],
                        [
                                new BaseMappingNodeConfig.MappingConfig("id", "id", null),
                                new BaseMappingNodeConfig.MappingConfig("id", "name", null),
                                new BaseMappingNodeConfig.MappingConfig("riskMark", "riskMark", null),
                                new BaseMappingNodeConfig.MappingConfig("useCert", "userCert",
                                        new OperatorPropertyNormalizerConfig(
                                                new OperatorConfig(
                                                        "examples/riskmining/builder/operator/cert_link_operator.py",
                                                        "cert_link_operator",
                                                        "CertLinkerOperator",
                                                        "handle",
                                                        null
                                                )
                                        )
                                )

                        ]));
        Node node3 = new Node("3", "sink", new GraphStoreSinkNodeConfig());


        Edge edge1 = new Edge("1", "2");
        Edge edge2 = new Edge("2", "3");

        Pipeline pipeline = new Pipeline([node1, node2, node3], [edge1, edge2]);

        expect:
        BuilderJsonUtils.serialize(pipeline) == '''{"nodes":[{"id":"1","name":"csv","nodeConfig":{"@type":"CSV_SOURCE","startRow":2,"url":"./data/App.csv","columns":["id","riskMark","useCert"],"type":"CSV_SOURCE"}},{"id":"2","name":"mapping","nodeConfig":{"@type":"SPG_TYPE_MAPPING","spgType":"RiskMining.App","mappingFilters":[],"mappingConfigs":[{"source":"id","target":"id"},{"source":"id","target":"name"},{"source":"riskMark","target":"riskMark"},{"source":"useCert","target":"userCert","normalizerConfig":{"@type":"OPERATOR","operatorConfig":{"filePath":"examples/riskmining/builder/operator/cert_link_operator.py","modulePath":"cert_link_operator","className":"CertLinkerOperator","method":"handle"},"normalizerType":"OPERATOR"}}],"type":"SPG_TYPE_MAPPING"}},{"id":"3","name":"sink","nodeConfig":{"@type":"GRAPH_SINK","type":"GRAPH_SINK"}}],"edges":[{"from":"1","to":"2"},{"from":"2","to":"3"}]}'''
    }
}
