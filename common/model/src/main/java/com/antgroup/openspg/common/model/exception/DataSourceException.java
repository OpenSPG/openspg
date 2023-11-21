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

package com.antgroup.openspg.common.model.exception;

import com.antgroup.openspg.common.model.datasource.DataSourceUsageTypeEnum;

public class DataSourceException extends OpenSPGException {

  private DataSourceException(Throwable cause, String messagePattern, Object... args) {
    super(cause, true, true, messagePattern, args);
  }

  private DataSourceException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }

  public static DataSourceException dataSourceNotExist(String dataSourceName) {
    return new DataSourceException("cannot find dataSource with name={}", dataSourceName);
  }

  public static DataSourceException noUsageForProject(
      DataSourceUsageTypeEnum dataSourceUsageType, Long projectId) {
    return new DataSourceException(
        "there is no {} dataSource for project={}", dataSourceUsageType, projectId);
  }
}
