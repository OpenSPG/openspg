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

package com.antgroup.openspg.server.api.facade.dto.schema;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/** Query concept instance */
public class ConceptRequest extends BaseRequest {

  private static final long serialVersionUID = -6316872733250486132L;

  /** The unique name of meta concept */
  private String conceptTypeName;

  /** The concept name */
  private String conceptName;

  public String getConceptTypeName() {
    return conceptTypeName;
  }

  public ConceptRequest setConceptTypeName(String conceptTypeName) {
    this.conceptTypeName = conceptTypeName;
    return this;
  }

  public String getConceptName() {
    return conceptName;
  }

  public ConceptRequest setConceptName(String conceptName) {
    this.conceptName = conceptName;
    return this;
  }

  @Override
  public String toString() {
    return String.format("%s/%s", conceptTypeName, conceptName);
  }
}
