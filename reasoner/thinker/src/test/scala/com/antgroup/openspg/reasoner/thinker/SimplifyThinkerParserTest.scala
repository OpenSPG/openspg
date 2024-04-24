package com.antgroup.openspg.reasoner.thinker

import scala.collection.JavaConverters._
import scala.collection.mutable

import com.antgroup.openspg.reasoner.thinker.logic.graph.{Entity, Predicate}
import com.antgroup.openspg.reasoner.thinker.logic.rule.{
  ClauseEntry,
  EntityPattern,
  Node,
  Rule,
  TriplePattern
}
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.{
  And,
  Condition,
  Not,
  Or,
  QlExpressCondition
}
import org.scalatest.funspec.AnyFunSpec

class SimplifyThinkerParserTest extends AnyFunSpec {
  val parser: SimplifyThinkerParser = new SimplifyThinkerParser()

  // scalastyle:off
  it("test1 define rule on concept") {
    val thinkerDsl =
      """
        |Define (危险水平分层/`很高危`) {
        |  R1:高血压分层/`临床并发症` and ("有并发症的糖尿病" in 症状) and 伸缩压>=140
        |  R2:Patient.conscious == "yes"
        |}
        |
        |""".stripMargin
    val ruleList: List[Rule] = parser.parseSimplifyDsl(thinkerDsl)
    assert(ruleList.size == 1)
    val rule: Rule = ruleList.head
    assert(rule.getHead.isInstanceOf[EntityPattern[_]])
    val body = rule.getBody.asScala
    assert(body.size == 1)
    // body
    val clauseCount = calculateClauseCount(body.toList)
    assert(clauseCount.entityPattern == 1)

    val root = rule.getRoot
    assert(root.isInstanceOf[Or])
    val rootChildrenList = root.asInstanceOf[Or].getChildren.asScala
    assert(rootChildrenList.size == 2)
    val firstLine = rootChildrenList.head
    assert(firstLine.isInstanceOf[And])
    assert(firstLine.asInstanceOf[And].getChildren.size() == 3)
    val secondLine = rootChildrenList(1)
    assert(secondLine.isInstanceOf[QlExpressCondition])

    // conditionToElementMap
    val conditionToElementMap: Map[Condition, Set[ClauseEntry]] =
      parser.getConditionToElementMap()
    assert(
      conditionToElementMap(new QlExpressCondition("get_value(\"高血压分层/`临床并发症`\")")).head
        .equals(new EntityPattern[String](new Entity("`临床并发症`", "高血压分层"))))
  }

  def getAllConditionInNode(node: Node): List[Condition] = {
    val logicalOpList: mutable.ListBuffer[Node] = new mutable.ListBuffer[Node]()
    logicalOpList += node
    val conditionList: mutable.ListBuffer[Condition] = new mutable.ListBuffer[Condition]()
    while (logicalOpList.nonEmpty) {
      val node = logicalOpList.remove(0)
      node match {
        case and: And => logicalOpList ++= and.getChildren.asScala.toList
        case or: Or => logicalOpList ++= or.getChildren.asScala.toList
        case not: Not => logicalOpList += not.getChild
        case c: QlExpressCondition => conditionList += c
      }
    }
    conditionList.toList
  }

  class ClauseCount {
    var entityPattern = 0
    var triplePattern = 0
  }

  def calculateClauseCount(body: List[ClauseEntry]): ClauseCount = {
    val clauseCount = new ClauseCount()
    body.foreach {
      case _: EntityPattern[_] => clauseCount.entityPattern += 1
      case _: TriplePattern => clauseCount.triplePattern += 1
      case _ =>
    }
    clauseCount
  }

  it("test2 define rule on concept") {
    val thinkerDsl =
      """
        |Define (危险水平分层/`中危`) {
        |  R1: hits(高血压分层/`心血管危险因素`)>=3 or 高血压分层/`靶器官损害` or ("无并发症的糖尿病" in 症状)
        |  R2: hits(高血压分层/`心血管危险因素`)>=1 and hits(高血压分层/`心血管危险因素`)<3
        |  R3: 血压水平分级/`2级高血压`
        |}
        |""".stripMargin
    val rule: Rule = parser.parseSimplifyDsl(thinkerDsl).head
    assert(rule.getBody.size() == 3)
    val root = rule.getRoot
    assert(root.isInstanceOf[Or])
    val outermostOrChildrenList = root.asInstanceOf[Or].getChildren.asScala
    assert(outermostOrChildrenList.size == 3)

    // first line
    val firstLine = outermostOrChildrenList.head
    assert(firstLine.isInstanceOf[Or])
    val firstLineChildrenList = firstLine.asInstanceOf[Or].getChildren.asScala
    assert(firstLineChildrenList.size == 3)
    assert(
      firstLineChildrenList.head.equals(
        new QlExpressCondition("hits(get_value(\"高血压分层/`心血管危险因素`\")) >= 3")))
    assert(
      firstLineChildrenList(1).equals(new QlExpressCondition("get_value(\"高血压分层/`靶器官损害`\")")))
    assert(firstLineChildrenList(2).equals(new QlExpressCondition("\"无并发症的糖尿病\" in 症状")))

    // second line
    val secondLine = outermostOrChildrenList(1)
    assert(secondLine.isInstanceOf[And])
    assert(secondLine.asInstanceOf[And].getChildren.size() == 2)

    // third line
    val thirdLine = outermostOrChildrenList(2)
    assert(thirdLine.isInstanceOf[QlExpressCondition])
  }

  it("test define_rule_on_relation_to_concept") {
    val thinkerDsl =
      """
        |Define (Med.drug)-[基本用药方案]->(药品/`ACEI+噻嗪类利尿剂`) {
        |  R1: 疾病/`高血压` and 药品/`多药方案`
        |}
        |Description: "本品与其他解热、镇痛、抗炎药物同用时可增加胃肠道不良反应，并可能导致溃疡。"
        |""".stripMargin
    val rule: Rule = parser.parseSimplifyDsl(thinkerDsl).head
    assert(rule.getHead.isInstanceOf[TriplePattern])
    val pattern = rule.getHead.asInstanceOf[TriplePattern].getTriple
    val subject = pattern.getSubject
    val predicate = pattern.getPredicate
    val object_ = pattern.getObject
    assert(subject.isInstanceOf[Entity[_]])
    assert(subject.asInstanceOf[Entity[_]].getType.equals("Med.drug"))
    assert(predicate.asInstanceOf[Predicate].getName.equals("基本用药方案"))
    assert(
      object_.asInstanceOf[Entity[String]].getType.equals("药品")
        && object_.asInstanceOf[Entity[String]].getId.equals("`ACEI+噻嗪类利尿剂`"))
    assert(rule.getBody.size() == 2)
    assert(rule.getRoot.isInstanceOf[And])
    assert(rule.getRoot.asInstanceOf[And].getChildren.size() == 2)
    assert(rule.getDesc.equals("\"本品与其他解热、镇痛、抗炎药物同用时可增加胃肠道不良反应，并可能导致溃疡。\""))
  }
}
