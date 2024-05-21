/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.reasoner.thinker

import scala.collection.JavaConverters._
import scala.collection.mutable

import com.antgroup.openspg.reasoner.thinker.logic.graph.{Entity, Predicate, Value}
import com.antgroup.openspg.reasoner.thinker.logic.graph
import com.antgroup.openspg.reasoner.thinker.logic.rule._
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact._
import com.antgroup.openspg.reasoner.thinker.util.ThinkerConditionUtil
import org.scalatest.funspec.AnyFunSpec

class SimplifyThinkerParserTest extends AnyFunSpec {
  val parser: SimplifyThinkerParser = new SimplifyThinkerParser()

  // scalastyle:off
  it("test1 define rule on concept") {
    val thinkerDsl =
      """
        |Define (危险水平分层/`很高危`) {
        |  R1:高血压分层/`临床并发症` and ("有并发症的糖尿病" in 症状) and 伸缩压>=140
        |  R2:Patient == "yes"
        |}
        |
        |""".stripMargin
    val ruleList: List[Rule] = parser.parseSimplifyDsl(thinkerDsl)
    assert(ruleList.size == 1)
    val rule: Rule = ruleList.head
    assert(rule.getHead.isInstanceOf[EntityPattern])
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
      conditionToElementMap(new QlExpressCondition("get_value(\"高血压分层/临床并发症\")")).head
        .equals(new EntityPattern(new Entity("临床并发症", "高血压分层"))))
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
      case _: EntityPattern => clauseCount.entityPattern += 1
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
        new QlExpressCondition("hits(get_value(\"高血压分层/心血管危险因素\")) >= 3")))
    assert(firstLineChildrenList(1).equals(new QlExpressCondition("get_value(\"高血压分层/靶器官损害\")")))
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
        |Define (:Med.drug)-[:基本用药方案]->(:药品/`ACEI+噻嗪类利尿剂`) {
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
    assert(subject.isInstanceOf[logic.graph.Node])
    assert(subject.asInstanceOf[logic.graph.Node].getType.equals("Med.drug"))
    assert(predicate.asInstanceOf[Predicate].getName.equals("基本用药方案"))
    assert(
      object_.asInstanceOf[Entity].getType.equals("药品")
        && object_.asInstanceOf[Entity].getId.equals("ACEI+噻嗪类利尿剂"))
    assert(rule.getBody.size() == 2)
    assert(rule.getRoot.isInstanceOf[And])
    assert(rule.getRoot.asInstanceOf[And].getChildren.size() == 2)
    assert(rule.getDesc.equals("\"本品与其他解热、镇痛、抗炎药物同用时可增加胃肠道不良反应，并可能导致溃疡。\""))
  }

  it("test parse concept from condition") {
    val condition: String = "get_value(\"高血压分层/靶器官损害\")"
    val conceptList = ThinkerConditionUtil.parseAllConceptInCondition(condition)
    assert(conceptList.size == 1)
    assert(conceptList.head.equals(new Entity("靶器官损害", "高血压分层")))

    val condition2 = "hits(get_value(\"高血压分层/心血管危险因素\")) >= 3"
    val conceptList2 = ThinkerConditionUtil.parseAllConceptInCondition(condition2)
    assert(conceptList2.size == 1)
    assert(conceptList2.head.equals(new Entity("心血管危险因素", "高血压分层")))

    val condition3 =
      "hits(get_value(\"高血压分层/心血管危险因素\"), get_value(\"高血压分层/2心血管危险因素\"), get_value(\"高血压分层/心血管危险因素\")) >= 10"
    val conceptList3 = ThinkerConditionUtil.parseAllConceptInCondition(condition3)
    assert(conceptList3.size == 2)
  }

  it("define_rule_on_relation_to_concept2") {
    val thinkerDsl =
      """
        |Define(a:InsDisease)-[:disclaim]->(d:InsComProd) {
        |    R0: 疾病/`高血压` and 疾病/`低血压`
        |    R1: (a)-[p: disclaimClause]->(b: InsDiseaseDisclaim) AND (b)-[:clauseVersion]->(c:InsClause) 
        |    R2: (p.disclaimType == '既往') and 疾病/`高血压`
        |    R3: hits((a)-[p]->(b), (c:InsClause)-[:insClauseVersion]->(d:InsComProd)) > 2
        |}
        |""".stripMargin
    val rule: Rule = parser.parseSimplifyDsl(thinkerDsl).head
    assert(rule.getHead.isInstanceOf[TriplePattern])
    val pattern = rule.getHead.asInstanceOf[TriplePattern].getTriple
    val subject = pattern.getSubject
    val predicate = pattern.getPredicate
    val object_ = pattern.getObject
    assert(subject.isInstanceOf[logic.graph.Node])
    val subjectNode = subject.asInstanceOf[logic.graph.Node]
    assert(subjectNode.getType.equals("InsDisease"))
    assert(subjectNode.getAlias.equals("a"))

    assert(predicate.asInstanceOf[Predicate].getName.equals("disclaim"))
    assert(predicate.asInstanceOf[Predicate].getAlias.startsWith("anonymous"))
    val objectNode = object_.asInstanceOf[logic.graph.Node]
    assert(objectNode.getType.equals("InsComProd"))
    assert(objectNode.getAlias.equals("d"))

    assert(rule.getBody.size() == 6)
    val (entityCount, tripleCount) = countClauseCount(rule.getBody.asScala.toList)
    assert(entityCount == 2)
    assert(tripleCount == 4)
    val expectTriplePatternSet = Set.apply(
      new TriplePattern(
        new graph.Triple(
          new graph.Node("InsDisease", "a"),
          new Predicate("disclaimClause", "p"),
          new graph.Node("InsDiseaseDisclaim", "b"))),
      new TriplePattern(
        new graph.Triple(
          new graph.Node("InsDiseaseDisclaim", "b"),
          new Predicate("clauseVersion", "anonymous_2"),
          new graph.Node("InsClause", "c"))),
      new TriplePattern(
        new graph.Triple(
          new Predicate("disclaimClause", "p"),
          new Predicate("disclaimType"),
          new Value(null, "anonymous_3"))),
      new TriplePattern(
        new graph.Triple(
          new graph.Node("InsClause", "c"),
          new Predicate("insClauseVersion", "anonymous_4"),
          new graph.Node("InsComProd", "d"))))
    assert(rule.getBody.containsAll(expectTriplePatternSet.asJava))
    assert(rule.getRoot.isInstanceOf[Or])
    assert(rule.getRoot.asInstanceOf[Or].getChildren.size() == 4)
    rule.getRoot
      .asInstanceOf[Or]
      .getChildren
      .asScala
      .foreach(child => {
        if (child.isInstanceOf[QlExpressCondition]) {
          assert(
            child
              .asInstanceOf[QlExpressCondition]
              .getQlExpress
              .equals("hits(get_spo(a, p, b),get_spo(c, anonymous_4, d)) > 2"))
        }
      })
  }

  def countClauseCount(body: List[ClauseEntry]): (Int, Int) = {
    var entityCount = 0
    var tripleCount = 0
    for (clause <- body) {
      clause match {
        case _: EntityPattern => entityCount += 1
        case _: TriplePattern => tripleCount += 1
        case _ =>
      }
    }
    (entityCount, tripleCount)
  }

  it("define_rule_on_relation_to_concept3") {
    val thinkerDsl =
      """
        |Define(a:InsDisease)-[:abnormalRule]->(o:String) {
        |    R1: (a)-[p: disclaimClause]->(b: InsDiseaseDisclaim) AND (b)-[:interpretation]->(o)
        |}
        |""".stripMargin
    val rule: Rule = parser.parseSimplifyDsl(thinkerDsl).head
    val head = rule.getHead.asInstanceOf[TriplePattern].getTriple
    assert(head.getObject.isInstanceOf[Value])
    val conditionList = rule.getRoot.asInstanceOf[And].getChildren
    assert(conditionList.size == 2)
    val expectedConditionList = List("get_spo(a,p,b)", "get_spo(b,anonymous_2,o)")
    conditionList.containsAll(expectedConditionList.asJava)
    assert(rule.getBody.size() == 2)
    rule.getBody.asScala.foreach(clause => {
      val triple = clause.asInstanceOf[TriplePattern].getTriple
      if (triple.getSubject.alias().equals("b")) {
        assert(triple.getPredicate.alias().equals("anonymous_2"))
        assert(triple.getObject.alias().equals("o"))
      }
    })

  }
}
