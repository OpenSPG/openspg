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

package com.antgroup.openspg.server.api.http.client.dto.builder.request;

import com.antgroup.openspg.common.model.base.BaseQuery;

public class BuilderJobInstQuery extends BaseQuery {

  private Long buildingJobInstId;

  private String externalJobInstId;

  public Long getBuildingJobInstId() {
    return buildingJobInstId;
  }

  public BuilderJobInstQuery setBuildingJobInstId(Long buildingJobInstId) {
    this.buildingJobInstId = buildingJobInstId;
    return this;
  }

  public String getExternalJobInstId() {
    return externalJobInstId;
  }

  public BuilderJobInstQuery setExternalJobInstId(String externalJobInstId) {
    this.externalJobInstId = externalJobInstId;
    return this;
  }
}
