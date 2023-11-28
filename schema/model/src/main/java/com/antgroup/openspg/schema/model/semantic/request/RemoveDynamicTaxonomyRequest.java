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

/** Request to remove dynamic taxonomy of concept. */
public class RemoveDynamicTaxonomyRequest extends BaseRequest {

  private static final long serialVersionUID = 3663132552543144765L;

  /** The unique name of object concept type. */
  private String objectConceptTypeName;

  /** The concept name of object in spo triple */
  private String objectConceptName;

  public String getObjectConceptName() {
    return objectConceptName;
  }

  public void setObjectConceptName(String objectConceptName) {
    this.objectConceptName = objectConceptName;
  }

  public String getObjectConceptTypeName() {
    return objectConceptTypeName;
  }

  public void setObjectConceptTypeName(String objectConceptTypeName) {
    this.objectConceptTypeName = objectConceptTypeName;
  }
}
