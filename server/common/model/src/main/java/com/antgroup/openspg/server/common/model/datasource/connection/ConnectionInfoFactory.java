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

package com.antgroup.openspg.server.common.model.datasource.connection;

import com.antgroup.openspg.server.common.model.datasource.DataSourceTypeEnum;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

public class ConnectionInfoFactory {

  public static BaseConnectionInfo from(String connInfo, DataSourceTypeEnum dataSourceType) {
    BaseConnectionInfo connectionInfo = null;
    Gson gson = new Gson();
    switch (dataSourceType) {
      case GRAPH_STORE:
        connectionInfo = gson.fromJson(connInfo, GraphStoreConnectionInfo.class);
        break;
      case SEARCH_ENGINE:
        connectionInfo = gson.fromJson(connInfo, SearchEngineConnectionInfo.class);
        break;
      case OBJECT_STORE:
        connectionInfo = gson.fromJson(connInfo, ObjectStoreConnectionInfo.class);
        break;
      case JOB_SCHEDULER:
        connectionInfo = gson.fromJson(connInfo, JobSchedulerConnectionInfo.class);
        break;
      case COMPUTING:
        connectionInfo = gson.fromJson(connInfo, ComputingConnectionInfo.class);
        break;
      case TABLE_STORE:
        connectionInfo = gson.fromJson(connInfo, TableStoreConnectionInfo.class);
        break;
      default:
        throw new IllegalArgumentException("illegal dataSourceType=" + dataSourceType);
    }

    if (StringUtils.isBlank(connectionInfo.getScheme())) {
      throw new IllegalArgumentException("illegal uri param for connInfo=" + connInfo);
    }
    return connectionInfo;
  }
}
