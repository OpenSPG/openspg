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

package com.antgroup.openspg.builder.core.pipeline.config;

import com.antgroup.openspg.builder.core.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.server.common.model.datasource.connection.SearchEngineConnectionInfo;
import com.antgroup.openspg.server.common.model.datasource.connection.TableStoreConnectionInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphStoreSinkNodeConfig extends BaseNodeConfig {

  /** The configuration information for graph storage. */
  private GraphStoreConnectionInfo graphStoreConnectionInfo;

  /** The configuration information for the search engine. */
  private SearchEngineConnectionInfo searchEngineConnectionInfo;

  /** The configuration information for the table store. */
  private TableStoreConnectionInfo tableStoreConnectionInfo;

  public GraphStoreSinkNodeConfig() {
    super(NodeTypeEnum.GRAPH_SINK);
  }
}
