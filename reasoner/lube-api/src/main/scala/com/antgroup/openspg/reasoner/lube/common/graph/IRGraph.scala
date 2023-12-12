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

package com.antgroup.openspg.reasoner.lube.common.graph

import java.util.concurrent.atomic.AtomicInteger

/**
 * A graph defined in query, which has a graph name only. Usually the name is KG
 */
trait IRGraph {
  def graphName: String
}

final case class KG() extends IRGraph {
  override def graphName: String = IRGraph.defaultGraphName
}

final case class View(graphName: String) extends IRGraph

object IRGraph {
  val defaultGraphName = "KG"
  private val graphId: AtomicInteger = new AtomicInteger(0)

  def generate: IRGraph = {
    if (graphId.get() == 0) {
      graphId.incrementAndGet()
      KG()
    } else {
      View(defaultGraphName + "_" + graphId.getAndAdd(1))
    }
  }
}
