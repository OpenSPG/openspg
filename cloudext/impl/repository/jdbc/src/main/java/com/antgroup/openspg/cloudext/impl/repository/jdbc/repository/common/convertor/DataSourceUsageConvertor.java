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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.common.convertor;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.DataSourceUsageDO;
import com.antgroup.openspg.common.model.datasource.DataSourceMountObjectTypeEnum;
import com.antgroup.openspg.common.model.datasource.DataSourceUsage;
import com.antgroup.openspg.common.model.datasource.DataSourceUsageTypeEnum;

public class DataSourceUsageConvertor {

  public static DataSourceUsage toModel(DataSourceUsageDO dataSourceUsageDO) {
    if (dataSourceUsageDO == null) {
      return null;
    }

    return new DataSourceUsage(
        dataSourceUsageDO.getDataSourceName(),
        dataSourceUsageDO.getMountObjectId(),
        DataSourceMountObjectTypeEnum.valueOf(dataSourceUsageDO.getMountObjectType()),
        DataSourceUsageTypeEnum.valueOf(dataSourceUsageDO.getUsageType()),
        Byte.valueOf((byte) 1).equals(dataSourceUsageDO.getIsDefault()));
  }

  public static DataSourceUsageDO toDO(DataSourceUsage dataSourceUsage) {
    DataSourceUsageDO dataSourceUsageDO = new DataSourceUsageDO();

    dataSourceUsageDO.setDataSourceName(dataSourceUsage.getDataSourceName());
    dataSourceUsageDO.setMountObjectId(dataSourceUsage.getMountObjectId());
    dataSourceUsageDO.setMountObjectType(dataSourceUsage.getMountObjectType().name());
    dataSourceUsageDO.setIsDefault((byte) (dataSourceUsage.isDefault() ? 1 : 0));
    dataSourceUsageDO.setUsageType(dataSourceUsage.getUsageType().name());
    return dataSourceUsageDO;
  }
}
