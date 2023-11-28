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

import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceQueryRequest;

import java.util.List;
import java.util.Map;

public interface DataSourceRepository {

  /**
   * 创建数据源
   *
   * @param dataSource dataSource
   * @return 创建条数
   */
  int save(DataSource dataSource);

  /**
   * 根据数据源唯一名称获取数据源
   *
   * @param uniqueName 数据源唯一名称
   * @return 数据源
   */
  DataSource get(String uniqueName);

  /**
   * 查询数据源
   *
   * @param request
   * @return
   */
  Map<String, DataSource> query(DataSourceQueryRequest request);

  /**
   * 批量获取数据源
   *
   * @param uniqueNames 数据源唯一名称
   * @return 数据源
   */
  Map<String, DataSource> batchGet(List<String> uniqueNames);
}
