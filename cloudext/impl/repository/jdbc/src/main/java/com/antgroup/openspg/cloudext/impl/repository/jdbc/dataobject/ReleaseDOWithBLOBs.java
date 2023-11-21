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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject;

public class ReleaseDOWithBLOBs extends ReleaseDO {
  private String schemaView;

  private String description;

  private String changeProcedureId;

  private String operationDetail;

  private String errorDetail;

  private String operationInfo;

  public String getSchemaView() {
    return schemaView;
  }

  public void setSchemaView(String schemaView) {
    this.schemaView = schemaView == null ? null : schemaView.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public String getChangeProcedureId() {
    return changeProcedureId;
  }

  public void setChangeProcedureId(String changeProcedureId) {
    this.changeProcedureId = changeProcedureId == null ? null : changeProcedureId.trim();
  }

  public String getOperationDetail() {
    return operationDetail;
  }

  public void setOperationDetail(String operationDetail) {
    this.operationDetail = operationDetail == null ? null : operationDetail.trim();
  }

  public String getErrorDetail() {
    return errorDetail;
  }

  public void setErrorDetail(String errorDetail) {
    this.errorDetail = errorDetail == null ? null : errorDetail.trim();
  }

  public String getOperationInfo() {
    return operationInfo;
  }

  public void setOperationInfo(String operationInfo) {
    this.operationInfo = operationInfo == null ? null : operationInfo.trim();
  }
}
