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

package com.antgroup.openspg.server.common.model.datasource;

/** Data source usage type */
public enum DataSourceUsageTypeEnum {
  KG_STORE,
  OPERATOR_STORE,
  FILE_STORE,
  SEARCH,
  JOB_SCHEDULER,
  COMPUTING,
  TABLE_STORE,
  UNKNOWN,
  ;
}
