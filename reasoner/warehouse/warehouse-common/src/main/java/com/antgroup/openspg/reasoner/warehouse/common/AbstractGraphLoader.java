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
package com.antgroup.openspg.reasoner.warehouse.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

@Slf4j
public abstract class AbstractGraphLoader implements GraphLoader {
  /** graph loader config */
  @Getter
  protected final GraphLoaderConfig graphLoaderConfig;

  /** loader */
  public AbstractGraphLoader(GraphLoaderConfig graphLoaderConfig) {
    this.graphLoaderConfig = graphLoaderConfig;
    log.info("graphLoaderConfig," + this.graphLoaderConfig);
  }

  /**
   * recall one hot graph from data source
   *
   * @param vertexId
   * @return
   */
  public VertexSubGraph queryOneHotGraphState(IVertexId vertexId) {
    throw new NotImplementedException("not support queryOneHotGraphState");
  }
}
