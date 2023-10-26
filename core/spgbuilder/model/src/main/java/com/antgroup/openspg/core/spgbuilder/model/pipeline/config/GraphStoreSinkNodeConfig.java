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

package com.antgroup.openspg.core.spgbuilder.model.pipeline.config;

import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.TableStoreConnectionInfo;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.NodeTypeEnum;


public class GraphStoreSinkNodeConfig extends BaseNodeConfig {

    /**
     * The configuration information for graph storage.
     */
    private GraphStoreConnectionInfo graphStoreConnectionInfo;

    /**
     * The configuration information for the search engine.
     */
    private SearchEngineConnectionInfo searchEngineConnectionInfo;

    /**
     * The configuration information for the table store.
     */
    private TableStoreConnectionInfo tableStoreConnectionInfo;

    public GraphStoreSinkNodeConfig() {
        super(NodeTypeEnum.GRAPH_SINK);
    }

    public GraphStoreConnectionInfo getGraphStoreConnectionInfo() {
        return graphStoreConnectionInfo;
    }

    public void setGraphStoreConnectionInfo(GraphStoreConnectionInfo graphStoreConnectionInfo) {
        this.graphStoreConnectionInfo = graphStoreConnectionInfo;
    }

    public SearchEngineConnectionInfo getSearchEngineConnectionInfo() {
        return searchEngineConnectionInfo;
    }

    public void setSearchEngineConnectionInfo(SearchEngineConnectionInfo searchEngineConnectionInfo) {
        this.searchEngineConnectionInfo = searchEngineConnectionInfo;
    }

    public TableStoreConnectionInfo getTableStoreConnectionInfo() {
        return tableStoreConnectionInfo;
    }

    public void setTableStoreConnectionInfo(TableStoreConnectionInfo tableStoreConnectionInfo) {
        this.tableStoreConnectionInfo = tableStoreConnectionInfo;
    }
}
