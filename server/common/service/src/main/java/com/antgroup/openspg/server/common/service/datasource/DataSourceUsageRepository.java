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

package com.antgroup.openspg.server.common.service.datasource;

import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceUsageQueryRequest;
import com.antgroup.openspg.common.model.datasource.DataSourceMountObjectTypeEnum;
import com.antgroup.openspg.common.model.datasource.DataSourceUsage;
import com.antgroup.openspg.common.model.datasource.DataSourceUsageTypeEnum;
import java.util.List;

public interface DataSourceUsageRepository {

  /**
   * 创建数据源使用方式
   *
   * @param dataSourceUsage 数据源使用方式
   * @return 创建条数
   */
  int save(DataSourceUsage dataSourceUsage);

  /**
   * 获取资源使用方式
   *
   * @param mountObjectId 挂载对象Id
   * @param mountObjectType 挂载对象类型
   * @param usageType 使用方式
   * @return DataSourceUsage
   */
  List<DataSourceUsage> getByMountObject(
      String mountObjectId,
      DataSourceMountObjectTypeEnum mountObjectType,
      DataSourceUsageTypeEnum usageType);

  /**
   * 获取资源使用方式
   *
   * @param request
   * @return
   */
  List<DataSourceUsage> query(DataSourceUsageQueryRequest request);
}
