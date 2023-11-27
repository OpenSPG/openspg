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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * {@link EdgeTypeName EdgeTypeName} is the unique identifier of {@link EdgeType EdgeType}, and it
 * consists of a triplet of the start vertex's type <tt>(startVertexType)</tt>, edge type's label
 * <tt>(edgeLabel)</tt> and the end vertex's type <tt>(endVertexType)</tt>.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EdgeTypeName extends BaseValObj {

  private final String startVertexType;

  /** The label of edge type */
  private final String edgeLabel;

  private final String endVertexType;

  public static EdgeTypeName parse(String edgeTypeName) {
    String[] splits = edgeTypeName.split("_");
    if (splits.length != 3) {
      throw new IllegalArgumentException("illegal edgeTypeName=" + edgeTypeName);
    }
    return new EdgeTypeName(splits[0], splits[1], splits[2]);
  }

  @Override
  public String toString() {
    return String.format("%s_%s_%s", startVertexType, edgeLabel, endVertexType);
  }
}
