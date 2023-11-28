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

package com.antgroup.openspg.server.api.http.client.util;

import com.antgroup.openspg.server.api.http.client.forest.GsonConvertor;
import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;

public class HttpClientBootstrap {

  public static void init(ConnectionInfo connInfo) {
    ForestConfiguration config = Forest.config();
    config.setVariableValue(HttpClientConstants.SCHEME, connInfo.getScheme());
    config.setVariableValue(HttpClientConstants.HOST, connInfo.getHost());
    config.setVariableValue(HttpClientConstants.PORT, connInfo.getPort());
    config.setConnectTimeout(connInfo.getConnectTimeout());
    config.setReadTimeout(connInfo.getReadTimeout());
    config.setJsonConverter(new GsonConvertor());
    config.setLogEnabled(false);
    config.setBackendName("httpclient");
  }
}
