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
