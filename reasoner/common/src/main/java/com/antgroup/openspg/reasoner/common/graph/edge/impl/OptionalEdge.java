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
package com.antgroup.openspg.reasoner.common.graph.edge.impl;

/**
 * @author chengqiang.cq
 * @version $Id: Edge.java, v 0.1 2023-02-01 11:37 chengqiang.cq Exp $$
 */
public class OptionalEdge<K, EV> extends Edge<K, EV> {

  /** optional edge */
  public OptionalEdge(K srcId, K targetId) {
    super(srcId, targetId);
  }

  @Override
  public OptionalEdge<K, EV> clone() {
    return new OptionalEdge<>(this.sourceId, this.targetId);
  }
}
