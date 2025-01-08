/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.api.facade.dto.service.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspg.server.common.model.job.SubGraph;

public class WriterGraphRequest extends BaseRequest {

  private static final long serialVersionUID = -5051318132737772511L;

  private SubGraph subGraph;

  /** UPSERT OR DELETE * */
  private String operation;

  Long projectId;

  Boolean enableLeadTo;

  String token;

  public WriterGraphRequest() {}

  public WriterGraphRequest(
      SubGraph subGraph, String operation, Long projectId, Boolean enableLeadTo, String token) {
    this.subGraph = subGraph;
    this.operation = operation;
    this.projectId = projectId;
    this.enableLeadTo = enableLeadTo;
    this.token = token;
  }

  public SubGraph getSubGraph() {
    return subGraph;
  }

  public void setSubGraph(SubGraph subGraph) {
    this.subGraph = subGraph;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Boolean getEnableLeadTo() {
    return enableLeadTo;
  }

  public void setEnableLeadTo(Boolean enableLeadTo) {
    this.enableLeadTo = enableLeadTo;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
