package com.antgroup.openspg.builder.model.pipeline

import com.antgroup.openspg.builder.model.BuilderJsonUtils
import com.antgroup.openspg.builder.model.pipeline.config.CsvSourceNodeConfig
import com.antgroup.openspg.builder.model.pipeline.config.GraphStoreSinkNodeConfig
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig
import spock.lang.Specification

class PipelineTest extends Specification {

    def "testPipelineSer"() {
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
        BuilderJsonUtils.serialize(pipeline).contains(BuilderJsonUtils.DEFAULT_TYPE_FIELD_NAME)
    }
}
