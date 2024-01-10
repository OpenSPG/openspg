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

package com.antgroup.openspg.reasoner.lube.physical

import com.antgroup.openspg.reasoner.lube.logical.{NodeVar, PathVar, PropertyVar, RepeatPathVar}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class VarTests extends AnyFunSpec {
  it("test rename PathVar") {
    val pathVar = PathVar("P", List.apply(NodeVar("A", Set.empty)))
    pathVar.rename("P1").name should equal("P1")
  }

  it("test rename ArrayVar") {
    val pathVar = PathVar("P", List.apply(NodeVar("A", Set.empty)))
    val arrayVar = RepeatPathVar(pathVar, 0, 0)
    arrayVar.rename("P1").name should equal("P1")
    arrayVar.isEmpty should equal(false)
  }

  it("test rename PropertyVar") {
    val propertyVar = PropertyVar("A", null)
    propertyVar.rename("P1").name should equal("P1")
  }
}
