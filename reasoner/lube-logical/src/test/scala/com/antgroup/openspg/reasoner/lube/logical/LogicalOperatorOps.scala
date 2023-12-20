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

package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.lube.logical.operators.LogicalOperator
import org.scalatest.Assertions.withClue
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

object LogicalOperatorOps {

  implicit class RichLogicalOperator(op: LogicalOperator) {

    def findExactlyOne(f: PartialFunction[LogicalOperator, Unit]): LogicalOperator = {
      val results = op.collect {
        case block if f.isDefinedAt(block) =>
          f(block)
          block
      }
      withClue(s"Failed to extract single matching block from $op") {
        results.size should equal(1)
      }
      results.head
    }

    def ensureThat(f: (LogicalOperator) => Unit): Unit = f(op)
  }
}
