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

package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.logical.LogicalOperatorOps.RichLogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.planning.{LogicalPlanner, LogicalPlannerContext}
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class LogicalOperatorTests extends AnyFunSpec {

  private def logicalTree: LogicalOperator = {
    val dsl =
      """
        |GraphStructure {
        |	(A:Film)-[E1:directFilm]->(B:FilmDirector)
        |	(A:Film)-[E2:writerOfFilm]->(C:FilmWriter)
        |	(B:FilmDirector)-[E3:workmates]->(C:FilmWriter)
        |}
        |Rule {
        |	R1("80后导演"): B.birthDate > '1980'
        |	R2("导演编剧同性别"): B.gender == C.gender
        |}
        |Action {
        |	get(B.name, C.name)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Film" -> Set.empty,
      "FilmDirector" -> Set.apply("birthDate", "gender", "name"),
      "FilmWriter" -> Set.apply("gender", "name"),
      "Film_directFilm_FilmDirector" -> Set.empty,
      "Film_writerOfFilm_FilmWriter" -> Set.empty,
      "FilmDirector_workmates_FilmWriter" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext = LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block)
    logicalPlan.head
  }

  it("testStart") {
    val op = logicalTree
    op.findExactlyOne { case Start(_, _, _, solved) =>
      solved.fields.keySet should equal(Set.apply("A", "B", "C", "E1", "E2", "E3"))
      solved.fields("B").asInstanceOf[NodeVar].fields.map(_.name) should equal(
        Set.apply("birthDate", "gender", "name"))
      solved.fields("C").asInstanceOf[NodeVar].fields.map(_.name) should equal(
        Set.apply("gender", "name"))
    }
  }

  it("testPatternScan") {
    val op = logicalTree
    op.findExactlyOne { case scan @ PatternScan(_, _) =>
      scan.fields.map(_.name).toSet should equal(Set.apply("A", "E1", "E2", "B", "C"))
    }
  }

  it("testExpandInto") {
    val op = logicalTree
    op.findExactlyOne { case expandInto @ ExpandInto(_: ExpandInto, _, _) =>
      expandInto.fields.map(_.name).toSet should equal(Set.apply("A", "B", "C", "E1", "E2", "E3"))
    }
  }

  it("testFilter") {
    val op = logicalTree
    op.findExactlyOne { case filter @ Filter(_: ExpandInto, _) =>
      filter.refFields.toSet should equal(
        Set.apply(NodeVar("B", Set.apply(new Field("birthDate", KTString, true)))))
    }

    op.findExactlyOne { case filter @ Filter(_: Filter, _) =>
      filter.refFields.toSet should equal(
        Set.apply(
          NodeVar("B", Set.apply(new Field("gender", KTString, true))),
          NodeVar("C", Set.apply(new Field("gender", KTString, true)))))
    }

  }

}
