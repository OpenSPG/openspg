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

package com.antgroup.openspg.server.schema.core.model.semantic.meta;

import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import java.util.EnumSet;

public class InductiveRelation extends BaseSemanticRelation {

  private static final long serialVersionUID = -8989272387092424288L;

  enum InductivePredicateEnum {
    /** Classification can be performed through rules defined on predicates. */
    belongTo("属于"),
    ;

    private final String nameZh;

    InductivePredicateEnum(String nameZh) {
      this.nameZh = nameZh;
    }

    public String getNameZh() {
      return nameZh;
    }
  }

  /** Accept entity/event type */
  private final SPGTypeIdentifier subjectTypeIdentifier;

  /** Accept concept type only */
  private final SPGTypeIdentifier objectConceptTypeIdentifier;

  private final InductivePredicateEnum predicate;

  public InductiveRelation(
      SPGTypeIdentifier typeIdentifier,
      SPGTypeIdentifier conceptTypeIdentifier,
      InductivePredicateEnum predicateEnum) {
    super(
        EnumSet.of(SPGTypeEnum.ENTITY_TYPE, SPGTypeEnum.CONCEPT_TYPE),
        EnumSet.of(SPGTypeEnum.CONCEPT_TYPE));

    this.subjectTypeIdentifier = typeIdentifier;
    this.objectConceptTypeIdentifier = conceptTypeIdentifier;
    this.predicate = predicateEnum;
  }

  public SPGTypeIdentifier getSubjectTypeIdentifier() {
    return subjectTypeIdentifier;
  }

  public SPGTypeIdentifier getObjectConceptTypeIdentifier() {
    return objectConceptTypeIdentifier;
  }

  public InductivePredicateEnum getPredicate() {
    return predicate;
  }
}
