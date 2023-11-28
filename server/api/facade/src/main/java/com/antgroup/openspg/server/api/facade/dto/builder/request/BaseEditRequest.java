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

package com.antgroup.openspg.server.api.facade.dto.builder.request;

import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseRequest;

public class BaseEditRequest extends BaseRequest {

  private Long projectId;

  private RecordAlterOperationEnum alterOp;

  private String spgName;

  private SPGRecordTypeEnum spgRecordType;

  public String getSpgName() {
    return spgName;
  }

  public void setSpgName(String spgName) {
    this.spgName = spgName;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public RecordAlterOperationEnum getAlterOp() {
    return alterOp;
  }

  public void setAlterOp(RecordAlterOperationEnum alterOp) {
    this.alterOp = alterOp;
  }

  public SPGRecordTypeEnum getSpgRecordType() {
    return spgRecordType;
  }

  public void setSpgRecordType(SPGRecordTypeEnum spgRecordType) {
    this.spgRecordType = spgRecordType;
  }
}
