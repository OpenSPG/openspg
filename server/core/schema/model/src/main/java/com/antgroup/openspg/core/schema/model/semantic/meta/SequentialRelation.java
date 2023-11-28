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

package com.antgroup.openspg.core.schema.model.semantic.meta;

import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;

import java.util.EnumSet;

public class SequentialRelation extends BaseSemanticRelation {

  private static final long serialVersionUID = -2382855534544499669L;

  enum SequentialPredicateEnum {

    /** happened-before */
    happenedBefore("先于...发生"),
    ;

    private final String nameZh;

    SequentialPredicateEnum(String nameZh) {
      this.nameZh = nameZh;
    }

    public String getNameZh() {
      return nameZh;
    }
  }

  /** Accept concept/event type */
  private final SPGTypeIdentifier subjectTypeIdentifier;

  /** Accept concept/event type，keep consistent with the type of subjectTypeIdentifier */
  private final SPGTypeIdentifier objectTypeIdentifier;

  private final SequentialPredicateEnum predicate;

  public SequentialRelation(
      SPGTypeIdentifier subjectTypeIdentifier,
      SPGTypeIdentifier objectTypeIdentifier,
      SequentialPredicateEnum predicate) {
    super(
        EnumSet.of(SPGTypeEnum.CONCEPT_TYPE, SPGTypeEnum.EVENT_TYPE),
        EnumSet.of(SPGTypeEnum.CONCEPT_TYPE, SPGTypeEnum.EVENT_TYPE));

    this.subjectTypeIdentifier = subjectTypeIdentifier;
    this.objectTypeIdentifier = objectTypeIdentifier;
    this.predicate = predicate;
  }

  public SPGTypeIdentifier getSubjectTypeIdentifier() {
    return subjectTypeIdentifier;
  }

  public SPGTypeIdentifier getObjectTypeIdentifier() {
    return objectTypeIdentifier;
  }

  public SequentialPredicateEnum getPredicate() {
    return predicate;
  }
}
