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

package com.antgroup.openspg.schema.model.semantic.request;

import com.antgroup.openspg.common.model.base.BaseRequest;

/** Request to define dynamic taxonomy */
public class DefineDynamicTaxonomyRequest extends BaseRequest {

  private static final long serialVersionUID = 4142844732718634621L;

  /** The unique name of concept type. */
  private String conceptTypeName;

  /** The name of concept. */
  private String conceptName;

  /** The dsl concept of logical rule. */
  private String dsl;

  public String getConceptTypeName() {
    return conceptTypeName;
  }

  public void setConceptTypeName(String conceptTypeName) {
    this.conceptTypeName = conceptTypeName;
  }

  public String getConceptName() {
    return conceptName;
  }

  public void setConceptName(String conceptName) {
    this.conceptName = conceptName;
  }

  public String getDsl() {
    return dsl;
  }

  public void setDsl(String dsl) {
    this.dsl = dsl;
  }
}
