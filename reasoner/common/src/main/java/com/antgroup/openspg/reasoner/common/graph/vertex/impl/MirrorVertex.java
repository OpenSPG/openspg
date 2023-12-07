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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;

/**
 * @author chengqiang.cq
 * @version $Id: Vertex.java, v 0.1 2023-02-01 11:30 chengqiang.cq Exp $$
 */
public class MirrorVertex<K, VV> extends NoneVertex<K, VV> {

  /** mirror vertex for optional */
  public MirrorVertex(IVertex<K, VV> vertex) {
    super(vertex);
  }

  @Override
  public MirrorVertex<K, VV> clone() {
    return new MirrorVertex<>(this);
  }
}
