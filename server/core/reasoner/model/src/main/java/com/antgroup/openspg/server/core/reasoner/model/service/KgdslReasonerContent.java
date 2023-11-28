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

import com.antgroup.openspg.server.core.reasoner.model.struct.StartingVertex;
import java.util.Collections;
import java.util.List;

/**
 * Reasoning based on KGDSL. This class contains a KGDSL script and possible starting vertices. When
 * there is a starting vertex, the task starts from that starting point for reasoning. If there is
 * no starting vertex, the task will trigger reasoning from a batch of vertices of the same type.
 */
public class KgdslReasonerContent extends BaseReasonerContent {

  /** A short reasoning script */
  private final String kgdsl;

  /**
   * starting vertices for reasoning, may be empty.
   *
   * <p>If it is empty, it usually consumes more resources, it is recommended to run in cluster
   * mode.
   */
  private final List<StartingVertex> startingVertices;

  public KgdslReasonerContent(String kgdsl, List<StartingVertex> startingVertices) {
    super(ReasonerContentTypeEnum.KGDSL);
    this.kgdsl = kgdsl;
    this.startingVertices = startingVertices;
  }

  public String getKgdsl() {
    return kgdsl;
  }

  public List<StartingVertex> getStartingVertices() {
    if (startingVertices == null) {
      return Collections.emptyList();
    }
    return startingVertices;
  }
}
