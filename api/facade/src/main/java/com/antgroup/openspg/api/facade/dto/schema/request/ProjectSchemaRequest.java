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

package com.antgroup.openspg.api.facade.dto.schema.request;

import com.antgroup.openspg.common.model.base.BaseRequest;

/** Get all schema type in project request. */
public class ProjectSchemaRequest extends BaseRequest {

  private static final long serialVersionUID = -3780343797046897462L;

  /** The unique id of project. */
  private Long projectId;

  public ProjectSchemaRequest() {}

  public ProjectSchemaRequest(Long projectId) {
    this.projectId = projectId;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  @Override
  public String toString() {
    return String.valueOf(projectId);
  }
}
