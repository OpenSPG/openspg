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

package com.antgroup.openspg.cloudext.impl.computing.local;

import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClient;
import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClientDriver;
import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClientDriverManager;
import com.antgroup.openspg.common.util.cloudext.CachedCloudExtClientDriver;
import com.antgroup.openspg.common.model.datasource.connection.ComputingConnectionInfo;

public class LocalComputingClientDriver
    extends CachedCloudExtClientDriver<ComputingClient, ComputingConnectionInfo>
    implements ComputingClientDriver {

  static {
    ComputingClientDriverManager.registerDriver(new LocalComputingClientDriver());
  }

  @Override
  public String driverScheme() {
    return "local";
  }

  @Override
  protected ComputingClient innerConnect(ComputingConnectionInfo connInfo) {
    return new LocalComputingClient(connInfo);
  }
}
