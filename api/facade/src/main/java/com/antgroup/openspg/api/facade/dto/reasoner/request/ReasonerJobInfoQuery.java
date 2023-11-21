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

package com.antgroup.openspg.api.facade.dto.reasoner.request;

import com.antgroup.openspg.common.model.base.BaseQuery;

public class ReasonerJobInfoQuery extends BaseQuery {

  private Long reasonerJobInfoId;

  private String externalJobInfoId;

  public Long getReasonerJobInfoId() {
    return reasonerJobInfoId;
  }

  public ReasonerJobInfoQuery setReasonerJobInfoId(Long reasonerJobInfoId) {
    this.reasonerJobInfoId = reasonerJobInfoId;
    return this;
  }

  public String getExternalJobInfoId() {
    return externalJobInfoId;
  }

  public ReasonerJobInfoQuery setExternalJobInfoId(String externalJobInfoId) {
    this.externalJobInfoId = externalJobInfoId;
    return this;
  }
}
