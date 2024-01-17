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

package com.antgroup.openspg.reasoner.parser.utils

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.table.FieldType
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class ParserUtilsTest extends AnyFunSpec {

  it("ddl block result table is null") {
    val dsl =
      """Define (s:DomainFamily)-[p:totalText]->(o:Text) {
        |    Structure {
        |        (s)<-[:belong]-(d:Domain)
        |    }
        |    Constraint {
        |        o = "abc"
        |    }
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val columnList = ParserUtils.getResultTableColumns(block, null)
    columnList should equal(null)
  }

  it("dsl with out as") {
    val dsl =
      """GraphStructure {
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
        |  get(A.name,B.name,F1.type,F4.source)
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val columnList = ParserUtils.getResultTableColumns(block, null)
    columnList.length should equal(4)
    columnList.map(f => f.getName) should equal(
      List.apply("a_name", "b_name", "f1_type", "f4_source"))
  }

  it("dsl with as, check column") {
    val dsl =
      """GraphStructure {
        |  A, B [FilmDirector]
        |  C, D [Film]
        |  E [FilmStar]
        |  C->A [directFilm] as F1
        |  D->B [directFilm] as F2
        |  C->E [starOfFilm] as F3
        |  D->E [starOfFilm] as F4
        |}
        |Rule {
        |}
        |Action {
        |  get(A.name as an,B.name as bn,C.id as cid,D.id,E.name as en)
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val columnList = ParserUtils.getResultTableColumns(
      block,
      Map.apply((Constants.KG_REASONER_OUTPUT_COLUMN_FORCE_STRING, "true")))
    columnList.length should equal(5)
    columnList.map(f => f.getName) should equal(List.apply("an", "bn", "cid", "d_id", "en"))
    columnList.map(f => f.getType) should equal(
      List.apply(
        FieldType.STRING,
        FieldType.STRING,
        FieldType.STRING,
        FieldType.STRING,
        FieldType.STRING))
  }
}
