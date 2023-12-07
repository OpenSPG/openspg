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

package com.antgroup.openspg.reasoner.common.trees

import scala.reflect.ClassTag

import com.antgroup.openspg.reasoner.common.exception.SystemError

case class TreeFinder[T <: AbstractTreeNode[T]: ClassTag](f: PartialFunction[T, Unit]) {

  def findExactlyOne(tree: T): T = {
    val results = tree.collect {
      case op if f.isDefinedAt(op) =>
        f(op)
        op
    }
    if (results.size != 1) {
      throw SystemError(s"Failed to extract single matching tree node from $tree")
    } else {
      results.head
    }
  }

}
