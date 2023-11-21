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

package com.antgroup.openspg.core.spgschema.model.semantic;

import com.antgroup.openspg.core.spgschema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;

/** Inductive predicate defined between concepts, usually the name of the predicate is "leadTo". */
public class LogicalCausationSemantic extends BaseConceptSemantic {

  private static final long serialVersionUID = -1943418046354258381L;

  /** The unique name of the subject concept type. */
  private final SPGTypeIdentifier subjectTypeIdentifier;

  /** The unique name of the subject concept. */
  private final ConceptIdentifier subjectIdentifier;

  /** The logic rule defined on the concept. */
  private final PredicateIdentifier predicateIdentifier;

  /** The unique name of the object concept type. */
  private final SPGTypeIdentifier objectTypeIdentifier;

  /** The unique name of the object concept. */
  private final ConceptIdentifier objectIdentifier;

  /** The details of the logic rule */
  private final LogicalRule logicalRule;

  public LogicalCausationSemantic(
      SPGTypeIdentifier subjectTypeIdentifier,
      ConceptIdentifier subjectIdentifier,
      PredicateIdentifier predicateIdentifier,
      SPGTypeIdentifier objectTypeIdentifier,
      ConceptIdentifier objectIdentifier,
      LogicalRule logicalRule) {
    this.subjectTypeIdentifier = subjectTypeIdentifier;
    this.subjectIdentifier = subjectIdentifier;
    this.predicateIdentifier = predicateIdentifier;
    this.objectTypeIdentifier = objectTypeIdentifier;
    this.objectIdentifier = objectIdentifier;
    this.logicalRule = logicalRule;
  }

  public SPGTypeIdentifier getSubjectTypeIdentifier() {
    return subjectTypeIdentifier;
  }

  public ConceptIdentifier getSubjectIdentifier() {
    return subjectIdentifier;
  }

  public PredicateIdentifier getPredicateIdentifier() {
    return predicateIdentifier;
  }

  public LogicalRule getLogicalRule() {
    return logicalRule;
  }

  public SPGTypeIdentifier getObjectTypeIdentifier() {
    return objectTypeIdentifier;
  }

  public ConceptIdentifier getObjectIdentifier() {
    return objectIdentifier;
  }
}
