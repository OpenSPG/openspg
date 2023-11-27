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

package com.antgroup.openspg.server.core.reasoner.model.service;

import com.antgroup.openspg.core.spgreasoner.model.struct.StartingVertex;
import java.util.List;

/**
 * Query or reason about the vertices passed in.
 *
 * <p>When the attribute of a vertex is defined by non-logical rules, we call this attribute fact
 * data, when the attribute of a vertex is defined by logical rules, we call this attribute data
 * defined by derived rules.
 *
 * <p>When a vertex is passed in, we will perform reasoning calculations on the attributes defined
 * by its logical rules, and query the attributes defined by its non-logical rules, and finally
 * return them together
 */
public class VertexReasonerContent extends BaseReasonerContent {

  /** the vertices to reason about */
  private final List<StartingVertex> startingVertices;

  public VertexReasonerContent(List<StartingVertex> startingVertices) {
    super(ReasonerContentTypeEnum.VERTEX);
    this.startingVertices = startingVertices;
  }

  public List<StartingVertex> getStartingVertices() {
    return startingVertices;
  }
}
