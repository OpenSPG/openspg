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

package com.antgroup.openspg.core.schema.model.semantic.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/** Request to remove logical causation between concepts. */
public class RemoveTripleSemanticRequest extends BaseRequest {

  private static final long serialVersionUID = -3165344348700966342L;

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

  /** The semantic type of the triple. */
  private String semanticType;

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

  public String getSemanticType() {
    return semanticType;
  }

  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType;
  }
}
