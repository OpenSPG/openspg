package com.antgroup.openspg.reasoner.lube.common.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class IRGraphTests extends AnyFunSpec{
  it ("test generate graph") {
    IRGraph.generate should equal(KG())
    IRGraph.generate.graphName should equal("KG_1")
  }
}
