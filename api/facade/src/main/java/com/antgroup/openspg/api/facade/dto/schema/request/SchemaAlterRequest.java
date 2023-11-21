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
import com.antgroup.openspg.core.spgschema.model.alter.SchemaDraft;

/** Commit schema draft request. */
public class SchemaAlterRequest extends BaseRequest {

  private static final long serialVersionUID = 2100602183655382637L;

  /** The project id that alter schema. */
  private Long projectId;

  /** The schema draft content. */
  private SchemaDraft schemaDraft;

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public SchemaDraft getSchemaDraft() {
    return schemaDraft;
  }

  public void setSchemaDraft(SchemaDraft schemaDraft) {
    this.schemaDraft = schemaDraft;
  }
}
