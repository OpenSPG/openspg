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

package com.antgroup.openspg.reasoner.parser.pattern

import com.antgroup.openspg.reasoner.lube.block.{MatchBlock, SourceBlock}
import com.antgroup.openspg.reasoner.parser.{DemoGraphParser, LexerInit}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class PatternParserTest extends AnyFunSpec {
  it("old") {
    val s =
      """
        |GraphStructure {
        |  A [FilmPerson]
        |  C,D [FilmDirector]
        |  A->C [test] as e1
        |  C->D [t1] repeat(1,20) as e2
        |}
        |Rule {
        |}
        |Action {
        |  get(A.name,C.name)
        |}
        |""".stripMargin
    val parser = new LexerInit().initKGReasonerParser(s)
    val patternParser = new PatternParser()

    val block = patternParser.parseGraphStructureDefine(
      parser
        .kg_dsl()
        .base_job()
        .kgdsl_old_define()
        .the_graph_structure()
        .graph_structure_define())
    print(block.pretty)
    block.isInstanceOf[MatchBlock] should equal(true)
    block.asInstanceOf[MatchBlock].dependencies.head.isInstanceOf[SourceBlock] should equal(true)
    block.asInstanceOf[MatchBlock]
      .dependencies.head.asInstanceOf[SourceBlock].graph.nodes.size should equal(3)
    block.asInstanceOf[MatchBlock]
      .dependencies.head.asInstanceOf[SourceBlock].graph.edges.size should equal(2)
  }
  it("gql") {
    val s =
      """
        |GraphStructure {
        |  path1:(A:Film)-[p:starOfFilm|starOfDirector]->(B:FilmStar|FilmDirector)
        |  path2:(B)<-[p2:starOfFilm where year>10]-{1,4}(C:Film)
        |  path3:(B)<-[p3:starOfFilm where year>10]-{1,4}(D:Robot.Film)
        |
        |}
        |Rule {
        |}
        |Action {
        |  get(A.name,C.name)
        |}
        |""".stripMargin
    val parser = new LexerInit().initKGReasonerParser(s)
    val patternParser = new PatternParser()

    val block = patternParser.parseGraphStructureDefine(
      parser
        .kg_dsl()
        .base_job()
        .kgdsl_old_define()
        .the_graph_structure()
        .graph_structure_define())
    print(block.pretty)
    block.isInstanceOf[MatchBlock] should equal(true)
    block.asInstanceOf[MatchBlock].dependencies.head.isInstanceOf[SourceBlock] should equal(true)
    block.asInstanceOf[MatchBlock]
      .dependencies.head.asInstanceOf[SourceBlock].graph.nodes.size should equal(4)
    block.asInstanceOf[MatchBlock]
      .dependencies.head.asInstanceOf[SourceBlock].graph.edges.size should equal(3)
  }

  it("test demo graph 0") {
    val demoGraph = """Graph {
                      |  `测试患者1701324926.375477` [abc]
                      |}""".stripMargin
    val parser = new DemoGraphParser()
    val data = parser.parse(demoGraph)
    println(data)
    data._1.size should equal(1)
    data._2.size should equal(0)
    data._1.head.getId should equal("测试患者1701324926.375477")
  }

  it("test demo graph") {
    val demoGraph = """Graph {
                      |  A_156 [FilmDirector,name='349',id='601']
                      |  B_936 [FilmDirector,name='497',id='253']
                      |  C_858 [Film]
                      |  D_569 [Film]
                      |  E_746 [FilmStar]
                      |
                      |  C_858 -> A_156 [directFilm,p=1, __version__=2]
                      |  D_569 -> B_936 [directFilm]
                      |  C_858 -> E_746 [starOfFilm]
                      |  D_569 -> E_746 [starOfFilm]
                      |}""".stripMargin
    val parser = new DemoGraphParser()
    val data = parser.parse(demoGraph)
    println(data)
    data._1.size should equal(5)
    data._2.size should equal(4)
  }

  it("test demo graph 2") {
    val demoGraph = """Graph {
                      |  A_156 [FilmDirector,name='349',id='601']
                      |  B_936 [FilmDirector,name='497',id='253']
                      |  C_858,D_569 [Film]
                      |  E_746 [FilmStar]
                      |
                      |  C_858 -> A_156 [directFilm,p=1, __version__=2]
                      |  D_569 -> B_936 [directFilm]
                      |  C_858 -> E_746 [starOfFilm]
                      |  D_569 -> E_746 [starOfFilm]
                      |}""".stripMargin
    val parser = new DemoGraphParser()
    val data = parser.parse(demoGraph)
    println(data)
    data._1.size should equal(5)
    data._2.size should equal(4)
  }

  it("test demo graph 3") {
    val demoGraph = """Graph {
                      |  A_156 [FilmDirector,name='349',id='601']
                      |  B_936 [FilmDirector,name='497',id='253']
                      |  C_858,D_569 [Film]
                      |  E_746 [FilmStar]
                      |
                      |  C_858 -> A_156 [directFilm,p=1, __version__=2]
                      |  D_569 -> B_936 [directFilm]
                      |  C_858 -> E_746 [starOfFilm]
                      |  D_569 <-> E_746 [starOfFilm]
                      |}""".stripMargin
    val parser = new DemoGraphParser()
    val data = parser.parse(demoGraph)
    println(data)
    data._1.size should equal(5)
    data._2.size should equal(4)
  }
}
