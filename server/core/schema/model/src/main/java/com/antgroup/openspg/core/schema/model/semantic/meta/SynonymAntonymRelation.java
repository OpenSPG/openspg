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

import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import java.util.EnumSet;

public class SynonymAntonymRelation extends BaseSemanticRelation {

  private static final long serialVersionUID = -75107956026027535L;

  enum SynonymAntonymPredicateEnum {
    /** synonymy */
    synonym("同义"),

    /** antonymy */
    antonym("反义"),
    ;

    private final String nameZh;

    SynonymAntonymPredicateEnum(String nameZh) {
      this.nameZh = nameZh;
    }

    public String getNameZh() {
      return nameZh;
    }
  }

  /** Accept concept type only */
  private final SPGTypeIdentifier conceptTypeIdentifier;

  private final SynonymAntonymPredicateEnum predicate;

  public SynonymAntonymRelation(
      SPGTypeIdentifier conceptTypeIdentifier, SynonymAntonymPredicateEnum predicate) {
    super(EnumSet.of(SPGTypeEnum.CONCEPT_TYPE), EnumSet.of(SPGTypeEnum.CONCEPT_TYPE));

    this.conceptTypeIdentifier = conceptTypeIdentifier;
    this.predicate = predicate;
  }

  public SPGTypeIdentifier getConceptTypeIdentifier() {
    return conceptTypeIdentifier;
  }

  public SynonymAntonymPredicateEnum getPredicate() {
    return predicate;
  }
}
