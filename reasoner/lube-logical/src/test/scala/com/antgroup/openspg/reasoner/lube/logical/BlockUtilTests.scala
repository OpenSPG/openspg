package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.lube.utils.BlockUtils
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class BlockUtilTests extends AnyFunSpec{
  it("group start test") {
    val dsl =
      """
        |GraphStructure {
        | (s: test)-[p: abc]->(o: test)
        |}
        |Rule {
        |  	amt = group(s).sum(p.amt)
        |}
        |Action {
        |  get(s.id)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    BlockUtils.getStarts(block) should equal (Set.apply("s"))
  }

  it("group filter with id test") {
    val dsl =
      """
        |GraphStructure {
        | (s: test)-[p: abc]->(o: test)
        |}
        |Rule {
        |	R1: o.id == '1111111'
        |}
        |Action {
        |  get(s.id)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    BlockUtils.getStarts(block) should equal (Set.apply("o"))
  }

}
