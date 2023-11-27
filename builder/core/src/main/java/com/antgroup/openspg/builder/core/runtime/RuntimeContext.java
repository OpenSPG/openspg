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

package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo;
import com.antgroup.openspg.core.spgbuilder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInfo;
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RuntimeContext implements Serializable {

  public static final String GRAPH_STORE_CONN_INFO = "graphStoreConnInfo";
  public static final String SEARCH_ENGINE_CONN_INFO = "searchEngineConnInfo";
  public static final String TABLE_STORE_CONN_INFO = "tableStoreConnInfo";
  public static final String GRAPH_STORE_CLIENT = "graphStoreClient";
  public static final String SEARCH_ENGINE_CLIENT = "searchEngineClient";

  private final long projectId;
  private final String schemaUrl;
  private final String builderJobName;
  private final long builderJobInstId;

  private final RecordAlterOperationEnum operation;
  private final int splitId;
  private final int parallelism;
  private final int batchSize;
  private final ProjectSchema projectSchema;

  private final Map<String, Object> params;

  private final BuilderMetric metrics;
  private final RecordCollector recordCollector;

  public GraphStoreClient getGraphStoreClient() {
    return (GraphStoreClient) params.get(GRAPH_STORE_CLIENT);
  }

  public SearchEngineClient getSearchEngineClient() {
    return (SearchEngineClient) params.get(SEARCH_ENGINE_CLIENT);
  }

  public GraphStoreConnectionInfo getGraphStoreConnInfo() {
    return (GraphStoreConnectionInfo) params.get(GRAPH_STORE_CONN_INFO);
  }

  public SearchEngineConnectionInfo getSearchEngineConnectionInfo() {
    return (SearchEngineConnectionInfo) params.get(SEARCH_ENGINE_CONN_INFO);
  }

  public boolean isEnableLeadTo() {
    return (boolean) params.getOrDefault(BuilderJobInfo.LEAD_TO, Boolean.FALSE);
  }

  public boolean isEnableSearchEngine() {
    return (boolean) params.getOrDefault(BuilderJobInfo.SEARCH_ENGINE, Boolean.FALSE);
  }
}
