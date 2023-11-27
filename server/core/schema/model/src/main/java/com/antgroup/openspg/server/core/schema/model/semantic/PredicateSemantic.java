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

package com.antgroup.openspg.server.core.schema.model.semantic;

import com.antgroup.openspg.server.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.server.core.schema.model.predicate.PropertyRef;

/** The semantics between properties or relations. */
public class PredicateSemantic extends BaseSemantic {

  private static final long serialVersionUID = 8408543037073978968L;

  /** Property reference as the subject */
  private PropertyRef subjectTypeRef;

  /** Predicate name */
  private PredicateIdentifier predicateIdentifier;

  /** Property reference as the object */
  private PropertyRef objectTypeRef;

  public PredicateSemantic() {}

  public PredicateSemantic(
      PropertyRef subjectTypeRef, PredicateIdentifier predicate, PropertyRef objectTypeRef) {
    super(subjectTypeRef.getOntologyType());
    this.subjectTypeRef = subjectTypeRef;
    this.predicateIdentifier = predicate;
    this.objectTypeRef = objectTypeRef;
  }

  public void setSubjectTypeRef(PropertyRef subjectTypeRef) {
    this.subjectTypeRef = subjectTypeRef;
  }

  public PredicateIdentifier getPredicateIdentifier() {
    return predicateIdentifier;
  }

  public PropertyRef getSubjectTypeRef() {
    return subjectTypeRef;
  }

  public PropertyRef getObjectTypeRef() {
    return objectTypeRef;
  }

  public Long getSubjectUniqueId() {
    return subjectTypeRef == null ? null : subjectTypeRef.getUniqueId();
  }

  public Long getObjectUniqueId() {
    return objectTypeRef == null ? null : objectTypeRef.getUniqueId();
  }

  public SPGTripleIdentifier getTripleName() {
    return new SPGTripleIdentifier(
        subjectTypeRef.newSpgTripleName(), predicateIdentifier, objectTypeRef.newSpgTripleName());
  }
}
