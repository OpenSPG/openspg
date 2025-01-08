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
package com.antgroup.openspg.server.common.service.datasource.meta.client;

import com.antgroup.openspg.server.common.model.CommonEnum;
import com.antgroup.openspg.server.common.service.datasource.meta.client.impl.DefaultMetaClientImpl;
import com.antgroup.openspg.server.common.service.datasource.meta.client.impl.JdbcMetaClientImpl;
import com.antgroup.openspg.server.common.service.datasource.meta.client.impl.OdpsMetaClientImpl;
import org.springframework.util.Assert;

public class DataSourceMetaFactory {

  public static DataSourceMetaClient getInstance(CommonEnum.DataSourceType type) {
    Assert.notNull(type, "datasource type");
    DataSourceMetaClient dataSourceMetaClient;
    switch (type) {
      case HIVE:
      case MYSQL:
      case ORACLE:
      case POSTGRESQL:
      case DB2:
      case MARIA_DB:
      case MS_SQL:
        dataSourceMetaClient = new JdbcMetaClientImpl(type.getDriver());
        break;
      case ODPS:
        dataSourceMetaClient = new OdpsMetaClientImpl();
        break;
      default:
        dataSourceMetaClient = new DefaultMetaClientImpl();
        break;
    }
    return dataSourceMetaClient;
  }
}
