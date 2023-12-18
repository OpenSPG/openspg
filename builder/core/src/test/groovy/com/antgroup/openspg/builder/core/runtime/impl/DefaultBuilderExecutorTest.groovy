package com.antgroup.openspg.builder.core.runtime.impl

import com.antgroup.openspg.builder.core.logical.LogicalPlan
import com.antgroup.openspg.builder.core.physical.PhysicalPlan
import com.antgroup.openspg.builder.core.runtime.BuilderExecutor
import com.antgroup.openspg.builder.model.pipeline.Node
import com.antgroup.openspg.builder.model.pipeline.Pipeline
import spock.lang.Specification

class DefaultBuilderExecutorTest extends Specification {

    def eval1() {
        given:
        Pipeline pipeline1 = genPipeline1()
        BuilderExecutor builderExecutor = new DefaultBuilderExecutor()

        when:
        def logicalPlan = LogicalPlan.parse(pipeline1)
        def physicalPlan = PhysicalPlan.plan(logicalPlan)

        then:
        builderExecutor.eval()

    }
    private def genPipeline1() {
        def node1 = new Node()
        def node2 = new Node()
        def node3 = new Node()
    }
}
