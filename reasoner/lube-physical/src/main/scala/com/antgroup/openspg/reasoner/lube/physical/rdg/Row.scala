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

package com.antgroup.openspg.reasoner.lube.physical.rdg

import com.antgroup.openspg.reasoner.lube.logical.Var

abstract class Row[T <: RDG[T]](val orderedFields: List[Var], val rdg: T) extends Result {

  /**
   * Print the result, usually used for debug.
   *
   * @param rows number of rows to print
   */
  def show(rows: Int = 20): Unit
}
