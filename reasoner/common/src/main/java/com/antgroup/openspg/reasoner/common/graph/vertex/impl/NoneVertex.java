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


package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;

/**
 * @author chengqiang.cq
 * @version $Id: Vertex.java, v 0.1 2023-02-01 11:30 chengqiang.cq Exp $$
 */
public class NoneVertex<K, VV> extends Vertex<K, VV> {

  /** mirror vertex for optional */
  public NoneVertex(IVertex<K, VV> vertex) {
    this.id = vertex.getId();
    this.value = vertex.getValue();
  }

  @Override
  public NoneVertex<K, VV> clone() {
    return new NoneVertex<>(this);
  }
}
