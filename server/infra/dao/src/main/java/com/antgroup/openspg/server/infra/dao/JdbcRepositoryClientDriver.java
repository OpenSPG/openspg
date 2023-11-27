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

package com.antgroup.openspg.server.infra.dao;

import com.antgroup.openspg.cloudext.interfaces.repository.RepositoryClient;
import com.antgroup.openspg.cloudext.interfaces.repository.RepositoryClientDriver;
import com.antgroup.openspg.cloudext.interfaces.repository.RepositoryClientDriverManager;
import com.antgroup.openspg.server.common.model.datasource.connection.RepositoryConnectionInfo;

public class JdbcRepositoryClientDriver implements RepositoryClientDriver {

  static {
    RepositoryClientDriverManager.registerDriver(new JdbcRepositoryClientDriver());
  }

  // todo 动态生成repo的bean

  @Override
  public String driverScheme() {
    return "jdbc";
  }

  @Override
  public RepositoryClient connect(RepositoryConnectionInfo connInfo) {
    throw new IllegalArgumentException();
  }
}
