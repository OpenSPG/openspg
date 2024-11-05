/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.cloudext.impl.graphstore.neo4j;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriver;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.common.util.cloudext.CachedCloudExtClientDriver;

public class Neo4jStoreClientDriver extends CachedCloudExtClientDriver<GraphStoreClient>
    implements GraphStoreClientDriver {

  static {
    GraphStoreClientDriverManager.registerDriver(new Neo4jStoreClientDriver());
  }

  @Override
  public String driverScheme() {
    return "neo4j";
  }

  @Override
  protected GraphStoreClient innerConnect(String connInfo) {
    return new Neo4jStoreClient(connInfo);
  }
}
