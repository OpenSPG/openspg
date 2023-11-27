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

package com.antgroup.openspg.server.biz.common.convertor;

import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceUsageCreateRequest;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceMountObjectTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.DataSourceTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsage;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsageTypeEnum;
import com.antgroup.openspg.server.common.model.datasource.connection.ConnectionInfoFactory;
import org.apache.commons.lang3.BooleanUtils;

public class DataSourceConvertor {

  public static DataSource convert(DataSourceCreateRequest request) {
    DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.valueOf(request.getType());
    return new DataSource(
        request.getName(),
        dataSourceType,
        request.getPhysicalInfo(),
        ConnectionInfoFactory.from(request.getConnInfo(), dataSourceType));
  }

  public static DataSourceUsage convert(DataSourceUsageCreateRequest request) {
    return new DataSourceUsage(
        request.getDataSourceName(),
        request.getMountObjectId(),
        DataSourceMountObjectTypeEnum.valueOf(request.getMountObjectType()),
        DataSourceUsageTypeEnum.valueOf(request.getUsageType()),
        BooleanUtils.isTrue(request.getAsDefault()));
  }
}
