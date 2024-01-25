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

package com.antgroup.openspg.reasoner.lube.logical.optimizer

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.logical.LogicalOperatorOps.RichLogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.rules._
import com.antgroup.openspg.reasoner.lube.logical.planning.{LogicalPlanner, LogicalPlannerContext}
import com.antgroup.openspg.reasoner.lube.logical.validate.Validator
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class NodeIdToEdgePropertyTests extends AnyFunSpec {
  it("test select nodeId should not to edge property") {
    val dsl =
      """
        |GraphStructure {
        |  (A:User)-[e1:lk]->(B:User)-[e2:lk]->(C:User)
        |}
        |Rule {
        |  R1(""): e1.weight < e2.weight
        |  R2(""): C.height > 170
        |}
        |Action {
        |  get(A.id, B.name, C.name)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] =
      Map.apply(
        "User" -> Set.apply("id", "name", "age", "height", "weight"),
        "User_lk_User" -> Set.apply("weight"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(
        catalog,
        parser,
        Map
          .apply(
            Constants.SPG_REASONER_MULTI_VERSION_ENABLE -> true,
            Constants.START_ALIAS -> "A")
          .asInstanceOf[Map[String, Object]])
    val dag = Validator.validate(List.apply(block))
    val logicalPlan = LogicalPlanner.plan(dag).popRoot()
    val rule = Seq(NodeIdToEdgeProperty, FilterPushDown, ExpandIntoPure, SolvedModelPure)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan, rule)
    optimizedLogicalPlan.findExactlyOne { case select: Select =>
      select.fields.map(f => s"${f.name}.${f.asInstanceOf[PropertyVar].field.name}") should equal(
        List.apply("A.id", "B.name", "C.name"))
    }
    val cnt = logicalPlan.transform[Int] {
      case (scan: PatternScan, cnt) => cnt.sum + 1
      case (scan: ExpandInto, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(3)
  }

  it("test nodeId to edge property for select") {
    val dsl =
      """
        |GraphStructure {
        |    (s:Pkg)-[p:target]->(o:User)
        |}
        |Rule {
        |
        |}
        |Action {
        |    get(s.id,o.id)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] =
      Map.apply(
        "Pkg" -> Set.apply("id"),
        "User" -> Set.apply("id"),
        "Pkg_target_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(
        catalog,
        parser,
        Map
          .apply(Constants.SPG_REASONER_MULTI_VERSION_ENABLE -> true, Constants.START_ALIAS -> "s")
          .asInstanceOf[Map[String, Object]])
    val dag = Validator.validate(List.apply(block))
    val logicalPlan = LogicalPlanner.plan(dag).popRoot()
    val rule = Seq(NodeIdToEdgeProperty, FilterPushDown, ExpandIntoPure, SolvedModelPure)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan, rule)
    optimizedLogicalPlan.findExactlyOne { case select: Select =>
      select.fields.map(f => s"${f.name}.${f.asInstanceOf[PropertyVar].field.name}") should equal(
        List.apply("s.id", s"p.${Constants.EDGE_TO_ID_KEY}"))
    }
    val cnt = optimizedLogicalPlan.transform[Int] {
      case (scan: PatternScan, cnt) => cnt.sum + 1
      case (scan: ExpandInto, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }

  it("test nodeId to edge property for select with filter") {
    val dsl =
      """
        |GraphStructure {
        |  (A:User)-[e1:lk]->(B:User)-[e2:lk]->(C:User)
        |}
        |Rule {
        |  R1(""): e1.weight < e2.weight
        |  R2(""): C.id in ['123456789']
        |}
        |Action {
        |  get(A.id, B.id, C.id)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] =
      Map.apply(
        "User" -> Set.apply("id", "name", "age", "height", "weight"),
        "User_lk_User" -> Set.apply("weight"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(
        catalog,
        parser,
        Map
          .apply(Constants.SPG_REASONER_MULTI_VERSION_ENABLE -> true)
          .asInstanceOf[Map[String, Object]])
    val dag = Validator.validate(List.apply(block))
    val logicalPlan = LogicalPlanner.plan(dag).popRoot()
    val rule = Seq(NodeIdToEdgeProperty, FilterPushDown, ExpandIntoPure, SolvedModelPure)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan, rule)
    optimizedLogicalPlan.findExactlyOne { case select: Select =>
      select.fields.map(f => s"${f.name}.${f.asInstanceOf[PropertyVar].field.name}") should equal(
        List.apply(s"e1.${Constants.EDGE_TO_ID_KEY}", "B.id", s"e2.${Constants.EDGE_TO_ID_KEY}"))
    }
    val cnt = optimizedLogicalPlan.transform[Int] {
      case (scan: PatternScan, cnt) =>
        val rule = scan.pattern.topology.values.flatten.filter(_.rule != null).head.rule
        val qlTransformer = new Expr2QlexpressTransformer()
        qlTransformer.transform(rule).head should equal(
          s"""e2.${Constants.EDGE_TO_ID_KEY} in ["123456789"]""")
        cnt.sum + 1
      case (scan: ExpandInto, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }

}
