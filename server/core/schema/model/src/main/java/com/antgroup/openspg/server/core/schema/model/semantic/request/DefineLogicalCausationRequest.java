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

package com.antgroup.openspg.server.core.schema.model.semantic.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/** Request to define logical causation between concepts. */
public class DefineLogicalCausationRequest extends BaseRequest {

  private static final long serialVersionUID = 3663132552543144765L;

  /** The unique name of subject concept type. */
  private String subjectConceptTypeName;

  /** The concept name of subject in spo triple. */
  private String subjectConceptName;

  /** The predicate name in spo triple */
  private String predicateName;

  /** The unique name of object concept type. */
  private String objectConceptTypeName;

  /** The concept name of object in spo triple */
  private String objectConceptName;

  /** The dsl content of logic rule defined in spo. */
  private String dsl;

  public String getSubjectConceptName() {
    return subjectConceptName;
  }

  public void setSubjectConceptName(String subjectConceptName) {
    this.subjectConceptName = subjectConceptName;
  }

  public String getPredicateName() {
    return predicateName;
  }

  public void setPredicateName(String predicateName) {
    this.predicateName = predicateName;
  }

  public String getObjectConceptName() {
    return objectConceptName;
  }

  public void setObjectConceptName(String objectConceptName) {
    this.objectConceptName = objectConceptName;
  }

  public String getDsl() {
    return dsl;
  }

  public void setDsl(String dsl) {
    this.dsl = dsl;
  }

  public String getSubjectConceptTypeName() {
    return subjectConceptTypeName;
  }

  public void setSubjectConceptTypeName(String subjectConceptTypeName) {
    this.subjectConceptTypeName = subjectConceptTypeName;
  }

  public String getObjectConceptTypeName() {
    return objectConceptTypeName;
  }

  public void setObjectConceptTypeName(String objectConceptTypeName) {
    this.objectConceptTypeName = objectConceptTypeName;
  }
}
