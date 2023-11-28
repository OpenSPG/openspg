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

package com.antgroup.openspg.cloudext.impl.objectstore.local;

import com.antgroup.openspg.cloudext.interfaces.objectstore.ObjectStoreClient;
import com.antgroup.openspg.cloudext.interfaces.objectstore.ObjectStoreClientDriver;
import com.antgroup.openspg.cloudext.interfaces.objectstore.ObjectStoreClientDriverManager;
import com.antgroup.openspg.server.common.model.datasource.connection.ObjectStoreConnectionInfo;

public class LocalObjectStoreClientDriver implements ObjectStoreClientDriver {

  static {
    ObjectStoreClientDriverManager.registerDriver(new LocalObjectStoreClientDriver());
  }

  @Override
  public String driverScheme() {
    return "local";
  }

  @Override
  public ObjectStoreClient connect(ObjectStoreConnectionInfo connInfo) {
    return new LocalObjectStoreClient(connInfo);
  }
}
