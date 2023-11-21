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

package com.antgroup.openspg.cloudext.impl.tablestore.local;

import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClient;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClientDriver;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClientDriverManager;
import com.antgroup.openspg.common.model.datasource.connection.TableStoreConnectionInfo;

public class LocalTableStoreClientDriver implements TableStoreClientDriver {

  static {
    TableStoreClientDriverManager.registerDriver(new LocalTableStoreClientDriver());
  }

  @Override
  public String driverScheme() {
    return "local";
  }

  @Override
  public TableStoreClient connect(TableStoreConnectionInfo connInfo) {
    return new LocalTableStoreClient(connInfo);
  }
}
