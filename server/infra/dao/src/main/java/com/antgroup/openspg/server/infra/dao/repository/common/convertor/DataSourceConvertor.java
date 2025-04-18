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

package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DozerBeanMapperUtil;
import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDO;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DataSourceConvertor {

  public static DataSourceDO toDO(DataSource dataSource) {
    if (null == dataSource) {
      return null;
    }
    DataSourceDO dataSourceDO = DozerBeanMapperUtil.map(dataSource, DataSourceDO.class);
    if (dataSource.getConnectionInfo() != null) {
      dataSourceDO.setConnectionInfo(JSONObject.toJSONString(dataSource.getConnectionInfo()));
    }
    if (CommonConstant.DEFAULT_PASSWORD.equals(dataSource.getDbPassword())) {
      dataSourceDO.setDbPassword(null);
    }
    return dataSourceDO;
  }

  public static DataSource toModel(DataSourceDO dataSourceDO) {
    if (null == dataSourceDO) {
      return null;
    }

    DataSource dataSource = DozerBeanMapperUtil.map(dataSourceDO, DataSource.class);
    if (StringUtils.isNotBlank(dataSourceDO.getConnectionInfo())) {
      dataSource.setConnectionInfo(JSONObject.parseObject(dataSourceDO.getConnectionInfo()));
    } else {
      dataSource.setConnectionInfo(new JSONObject());
    }
    if (StringUtils.isNotBlank(dataSourceDO.getDbPassword())) {
      dataSource.setDbPassword(CommonConstant.DEFAULT_PASSWORD);
    }
    return dataSource;
  }

  public static List<DataSourceDO> toDoList(List<DataSource> dataSources) {
    if (dataSources == null) {
      return null;
    }
    List<DataSourceDO> dos = Lists.newArrayList();
    for (DataSource dataSource : dataSources) {
      dos.add(toDO(dataSource));
    }
    return dos;
  }

  public static List<DataSource> toModelList(List<DataSourceDO> schedulerJobDOs) {
    if (schedulerJobDOs == null) {
      return null;
    }
    List<DataSource> dataSources = Lists.newArrayList();
    for (DataSourceDO schedulerJobDO : schedulerJobDOs) {
      dataSources.add(toModel(schedulerJobDO));
    }
    return dataSources;
  }
}
