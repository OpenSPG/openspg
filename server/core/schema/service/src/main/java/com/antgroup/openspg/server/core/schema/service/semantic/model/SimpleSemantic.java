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

package com.antgroup.openspg.server.core.schema.service.semantic.model;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;

/** Simple semantic object, mapping data object */
public class SimpleSemantic extends BaseValObj {

  private static final long serialVersionUID = 3587557458744988394L;

  /** The unique id of subject. */
  private final String subjectId;

  /** The unique id of object. */
  private final String objectId;

  /** The predicate name. */
  private final PredicateIdentifier predicateIdentifier;

  /** The unique name of subject type. */
  private SPGTypeIdentifier subjectTypeIdentifier;

  /** The unique name of object type. */
  private SPGTypeIdentifier objectTypeIdentifier;

  /** Rule code of logic rule. */
  private RuleCode ruleCode;

  /** Ontology type. */
  private SPGOntologyEnum ontologyEnum;

  public SimpleSemantic(
      SPGOntologyEnum ontologyEnum,
      String subjectId,
      String objectId,
      PredicateIdentifier predicateIdentifier) {
    this.ontologyEnum = ontologyEnum;
    this.subjectId = subjectId;
    this.objectId = objectId;
    this.predicateIdentifier = predicateIdentifier;
  }

  public SimpleSemantic(
      SPGOntologyEnum ontologyEnum,
      String subjectId,
      String objectId,
      PredicateIdentifier predicateIdentifier,
      SPGTypeIdentifier subjectTypeIdentifier,
      SPGTypeIdentifier objectTypeIdentifier,
      RuleCode ruleCode) {
    this.ontologyEnum = ontologyEnum;
    this.subjectId = subjectId;
    this.objectId = objectId;
    this.predicateIdentifier = predicateIdentifier;
    this.subjectTypeIdentifier = subjectTypeIdentifier;
    this.objectTypeIdentifier = objectTypeIdentifier;
    this.ruleCode = ruleCode;
  }

  public String getSubjectId() {
    return subjectId;
  }

  public String getObjectId() {
    return objectId;
  }

  public PredicateIdentifier getPredicateIdentifier() {
    return predicateIdentifier;
  }

  public SPGTypeIdentifier getSubjectTypeIdentifier() {
    return subjectTypeIdentifier;
  }

  public SPGTypeIdentifier getObjectTypeIdentifier() {
    return objectTypeIdentifier;
  }

  public RuleCode getRuleCode() {
    return ruleCode;
  }

  public void setSubjectTypeIdentifier(SPGTypeIdentifier subjectTypeIdentifier) {
    this.subjectTypeIdentifier = subjectTypeIdentifier;
  }

  public void setObjectTypeIdentifier(SPGTypeIdentifier objectTypeIdentifier) {
    this.objectTypeIdentifier = objectTypeIdentifier;
  }

  public void setRuleCode(RuleCode ruleCode) {
    this.ruleCode = ruleCode;
  }

  public SPGOntologyEnum getOntologyType() {
    return ontologyEnum;
  }

  public void setOntologyType(SPGOntologyEnum ontologyEnum) {
    this.ontologyEnum = ontologyEnum;
  }
}
