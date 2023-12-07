package com.antgroup.openspg.builder.model.pipeline.config

import spock.lang.Specification

class SubgraphPatternTest extends Specification {

    def "testElementOrdered"() {
        expect:
        SubgraphPattern subgraph = SubgraphPattern.from(elements)
        elementOrdered == subgraph.elementOrdered()

        where:
        elements                                                    || elementOrdered
        "(RiskMining.App)"                                          || ["RiskMining.App"]
        "(RiskMining.App|RiskMining.Cert)"                          || ["RiskMining.Cert", "RiskMining.App"]
        "(RiskMining.App)-[hasCert|installCert]->(RiskMining.Cert)" || ["RiskMining.Cert", "RiskMining.App", "RiskMining.App_hasCert_RiskMining.Cert", "RiskMining.App_installCert_RiskMining.Cert"]
    }
}
