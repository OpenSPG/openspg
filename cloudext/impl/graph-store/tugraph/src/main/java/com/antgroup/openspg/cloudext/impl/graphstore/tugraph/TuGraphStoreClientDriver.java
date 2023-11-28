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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriver;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.impl.DefaultLPGTypeNameConvertor;
import com.antgroup.openspg.common.util.cloudext.CachedCloudExtClientDriver;
import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;

public class TuGraphStoreClientDriver
    extends CachedCloudExtClientDriver<GraphStoreClient, GraphStoreConnectionInfo>
    implements GraphStoreClientDriver {

  static {
    GraphStoreClientDriverManager.registerDriver(new TuGraphStoreClientDriver());
  }

  @Override
  public String driverScheme() {
    return "tugraph";
  }

  @Override
  protected GraphStoreClient innerConnect(GraphStoreConnectionInfo connInfo) {
    return new TuGraphStoreClient(connInfo, new DefaultLPGTypeNameConvertor());
  }
}
