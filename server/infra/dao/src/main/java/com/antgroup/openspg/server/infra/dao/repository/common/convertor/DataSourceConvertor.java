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

package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.DataSourceDOWithBLOBs;
import com.antgroup.openspg.common.model.datasource.DataSource;
import com.antgroup.openspg.common.model.datasource.DataSourceTypeEnum;
import com.antgroup.openspg.common.model.datasource.connection.BaseConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.ConnectionInfoFactory;

public class DataSourceConvertor {

  public static DataSource toModel(DataSourceDOWithBLOBs dataSourceDO) {
    if (dataSourceDO == null) {
      return null;
    }

    DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.valueOf(dataSourceDO.getType());
    BaseConnectionInfo connectionInfo =
        ConnectionInfoFactory.from(dataSourceDO.getConnInfo(), dataSourceType);
    return new DataSource(
        dataSourceDO.getUniqueName(),
        dataSourceType,
        dataSourceDO.getPhysicalInfo(),
        connectionInfo);
  }

  public static DataSourceDOWithBLOBs toDO(DataSource dataSource) {
    DataSourceDOWithBLOBs dataSourceDO = new DataSourceDOWithBLOBs();

    dataSourceDO.setUniqueName(dataSource.getUniqueName());
    dataSourceDO.setType(dataSource.getDataSourceType().name());
    dataSourceDO.setPhysicalInfo(dataSource.getPhysicalInfo());
    dataSourceDO.setConnInfo(dataSource.getConnectionInfo().toString());
    return dataSourceDO;
  }
}
