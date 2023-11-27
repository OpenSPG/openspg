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

package com.antgroup.openspg.server.core.builder.model.operator;

import com.antgroup.openspg.server.common.model.base.BaseModel;

public class OperatorVersion extends BaseModel {

  /** Unique ID of the operator. */
  private final Long overviewId;

  /** Main class of the operator. */
  private final String mainClass;

  /** The file path of the operator is composed of the URL, operator name, and operator version. */
  private final String filePath;

  /** Version of the operator increases by 1 after each release. */
  private final Integer version;

  public OperatorVersion(Long overviewId, String mainClass, String filePath, Integer version) {
    this.overviewId = overviewId;
    this.mainClass = mainClass;
    this.filePath = filePath;
    this.version = version;
  }

  public Long getOverviewId() {
    return overviewId;
  }

  public String getMainClass() {
    return mainClass;
  }

  public String getFilePath() {
    return filePath;
  }

  public Integer getVersion() {
    return version;
  }
}
