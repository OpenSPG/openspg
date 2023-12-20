/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.runner.local

import com.antgroup.openspg.reasoner.runner.local.impl.LocalReasonerSession
import com.antgroup.openspg.reasoner.runner.local.rdg.{LocalRow, TypeTags}
import com.antgroup.openspg.reasoner.lube.block.{Block, DDLBlock, MatchBlock, TableResultBlock}
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  GraphPattern,
  LinkedPatternConnection,
  PatternConnection
}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.logical.validate.{Dag, Validator}
import com.antgroup.openspg.reasoner.parser.KgDslParser
import com.google.common.collect.Lists
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class LocalRunner extends AnyFunSpec with BeforeAndAfter {

  it("MockRunner") {
    val dsl =
      """
        |GraphStructure {
        |	(Student:Student)-[STEdge:STEdge]->(Teacher:Teacher)
        |}
        |Rule {
        |
        |}
        |Action {
        |get(Student.name, Teacher.name, STEdge.name)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "Student" -> Set.apply("name"),
      "Teacher" -> Set.apply("name"),
      "Student_STEdge_Teacher" -> Set.apply("name"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new LocalReasonerSession(new KgDslParser(), catalog, TypeTags.rdgTypeTag)
    val plan = session.plan(dsl, Map.empty)
    val rst = session.getResult(plan.head)
    if (rst.isInstanceOf[LocalRow]) {
      rst.asInstanceOf[LocalRow].show(10)
    }
  }

  // scalastyle:off
  it("dependency_analysis") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s.boundary, o.center, 10.1)]->(o:Subway)
        |   }
        |   Rule{}
        |   Action {get(s)}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.apply("boundary"),
      "Subway" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new LocalReasonerSession(new KgDslParser(), catalog, TypeTags.rdgTypeTag)
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    session.plan2UnresolvedLogicalPlan(dsl, Map.empty)
    val blockDag: Dag[Block] = Validator.validate(parser, block)(context)
    val nodes: Map[String, Block] = blockDag.getNodes
    nodes.keySet.size should equal(2)
    for (block <- nodes.values) {
      println(block.pretty)
    }
    // define Block
    val defineNodeName: String = blockDag.getEdges.apply("result").head
    val defineBlock = blockDag.getNode(defineNodeName)
    defineBlock.isInstanceOf[DDLBlock] should equal(true)
    val defineMatchBlock: MatchBlock =
      defineBlock.dependencies.head.dependencies.head.asInstanceOf[MatchBlock]
    defineMatchBlock.patterns.size should equal(1)
    val partialGraphPattern: GraphPattern = defineMatchBlock.patterns.head._2.graphPattern
    partialGraphPattern.nodes.size should equal(3)
    val linkedPatternConnection = partialGraphPattern.edges("s").head
    linkedPatternConnection.isInstanceOf[LinkedPatternConnection] should equal(true)
    linkedPatternConnection.asInstanceOf[LinkedPatternConnection].funcName should equal(
      "geo_buffer_and_convert_2_s2CellId")
    partialGraphPattern.edges("cell").head.isInstanceOf[PatternConnection] should equal(true)

    // rewrite Block
    val originBlock = blockDag.popRoot()
    originBlock.isInstanceOf[TableResultBlock] should equal(true)
    val originMatchBlock = originBlock.dependencies.head.asInstanceOf[MatchBlock]
    val originPartialGraphPattern = originMatchBlock.patterns.head._2.graphPattern
    !originPartialGraphPattern.edges("s").head.isInstanceOf[LinkedPatternConnection] should equal(
      true)
  }

  it("multi_dependency_analysis_error") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s.boundary, o.center, 10.1)]->(o:Subway)
        |       (s1:School)-[e2:nearby(s1.center, o2.center, 10)]->(o2:Shop)
        |   }
        |   Rule{}
        |   Action {get(s)}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.apply("boundary"),
      "Subway" -> Set.apply("center"),
      "School" -> Set.apply("center"),
      "Shop" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty,
      "Shop_centerS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new LocalReasonerSession(new KgDslParser(), catalog, TypeTags.rdgTypeTag)
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    session.plan2UnresolvedLogicalPlan(dsl, Map.empty)
    val blockDag: Dag[Block] = Validator.validate(parser, block)(context)
    var blockList: List[Block] =
      blockDag.getNodes.filter(entry => !entry._1.equals("result")).values.toList
    blockList = blockList :+ blockDag.getNode("result")
    blockList.foreach(block => println(block.pretty))
    blockList.size should equal(3)
    blockList.head.isInstanceOf[DDLBlock] should equal(true)
    blockList(1).isInstanceOf[DDLBlock] should equal(true)
    blockList.last.isInstanceOf[TableResultBlock] should equal(true)
    val partialGraphPattern = blockList.last
      .asInstanceOf[TableResultBlock]
      .dependencies
      .head
      .asInstanceOf[MatchBlock]
      .patterns
      .head
      ._2
      .graphPattern
    partialGraphPattern.edges("s1").head.isInstanceOf[LinkedPatternConnection] should equal(false)
    partialGraphPattern.edges("s").head.isInstanceOf[LinkedPatternConnection] should equal(false)
  }

  it("dependency_analysis_error") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s, o.center, 10.1)]->(o:Subway)
        |   }
        |   Rule{}
        |   Action {get(s)}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.apply("boundary"),
      "Subway" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new LocalReasonerSession(new KgDslParser(), catalog, TypeTags.rdgTypeTag)
    try {
      implicit val context: LogicalPlannerContext =
        LogicalPlannerContext(catalog, parser, Map.empty)
      session.plan2UnresolvedLogicalPlan(dsl, Map.empty)
      Validator.validate(parser, block)(context)
      false should equal(true)
    } catch {
      case e: Exception =>
        e.getMessage.equals("the first parameter in nearby should like A.property") should equal(
          true)
    }
  }

  it("dependency_analysis_error2") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s, o.center, -10)]->(o:Subway)
        |   }
        |   Rule{}
        |   Action {get(s)}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.apply("boundary"),
      "Subway" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new LocalReasonerSession(new KgDslParser(), catalog, TypeTags.rdgTypeTag)
    try {
      implicit val context: LogicalPlannerContext =
        LogicalPlannerContext(catalog, parser, Map.empty)
      session.plan2UnresolvedLogicalPlan(dsl, Map.empty)
      Validator.validate(parser, block)(context)
      false should equal(true)
    } catch {
      case e: Exception =>
        true should equal(true)
    }
  }

  it("test get graph load config") {
    val dsl =
      """
        |GraphStructure {
        |  A, B [FilmDirector]
        |  C, D [Film]
        |  E [FilmStar]
        |  C->A [directFilm] as F1
        |  D->B [directFilm] as F2
        |  C->E [starOfFilm] as F3
        |  D->E [starOfFilm] as F4
        |}
        |Rule {
        |  R1: A.id<B.id
        |}
        |Action {
        |  get(A.name,B.name)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "FilmDirector" -> Set.apply("id", "name"),
      "Film" -> Set.empty,
      "FilmStar" -> Set.empty,
      "Film_directFilm_FilmDirector" -> Set.empty,
      "Film_starOfFilm_FilmStar" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new LocalReasonerSession(new KgDslParser(), catalog, TypeTags.rdgTypeTag)
    val graphLoaderConfig = session.getGraphLoaderConfig(dsl, Map.empty)
    val vertexTypeSet = graphLoaderConfig.allVertexTypes()
    val edgeTypeSet = graphLoaderConfig.allEdgeTypes()
    vertexTypeSet.containsAll(Lists.newArrayList("FilmDirector", "Film", "FilmStar"))
    edgeTypeSet.containsAll(
      Lists.newArrayList("Film_starOfFilm_FilmStar", "Film_directFilm_FilmDirector"))
  }
  // scalastyle:on
}
