package com.antgroup.openspg.reasoner.thinker

import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule
import org.scalatest.funspec.AnyFunSpec

class SimplifyThinkerParserTest extends AnyFunSpec {
  val parser: SimplifyThinkerParser = new SimplifyThinkerParser()

  // scalastyle:off
  it("test define rule on concept") {
    val thinkerDsl1 =
      """
        |Define (危险水平分层/`很高危`) {
        |  高血压分层/`临床并发症` and ("有并发症的糖尿病" in 症状) and 伸缩压>=140;
        |  Patient.conscious == "yes";
        |}
        |
        |""".stripMargin
    val ruleList: List[Rule] = parser.parseSimplifyDsl(thinkerDsl1)
    assert(ruleList.size == 1)
  }

}
