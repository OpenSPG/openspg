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

package com.antgroup.openspg.server.core.schema.model.semantic.meta;

import com.antgroup.openspg.server.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.core.schema.model.type.SPGTypeEnum;
import java.util.EnumSet;

public class InclusiveRelation extends BaseSemanticRelation {

  private static final long serialVersionUID = 987603016727346252L;

  enum InclusivePredicateEnum {

    /** is-part-of */
    isPartOf("是...的一部分"),
    ;

    private final String nameZh;

    InclusivePredicateEnum(String nameZh) {
      this.nameZh = nameZh;
    }

    public String getNameZh() {
      return nameZh;
    }
  }

  /** Accept concept type only */
  private final SPGTypeIdentifier subjectConceptTypeIdentifier;

  /** Accept concept type only */
  private final SPGTypeIdentifier objectConceptTypeIdentifier;

  private final InclusivePredicateEnum predicate;

  public InclusiveRelation(
      SPGTypeIdentifier subjectConceptTypeIdentifier,
      SPGTypeIdentifier objectConceptTypeIdentifier,
      InclusivePredicateEnum predicateEnum) {
    super(EnumSet.of(SPGTypeEnum.CONCEPT_TYPE), EnumSet.of(SPGTypeEnum.CONCEPT_TYPE));

    this.subjectConceptTypeIdentifier = subjectConceptTypeIdentifier;
    this.objectConceptTypeIdentifier = objectConceptTypeIdentifier;
    this.predicate = predicateEnum;
  }

  public SPGTypeIdentifier getSubjectConceptTypeIdentifier() {
    return subjectConceptTypeIdentifier;
  }

  public SPGTypeIdentifier getObjectConceptTypeIdentifier() {
    return objectConceptTypeIdentifier;
  }

  public InclusivePredicateEnum getPredicate() {
    return predicate;
  }
}
