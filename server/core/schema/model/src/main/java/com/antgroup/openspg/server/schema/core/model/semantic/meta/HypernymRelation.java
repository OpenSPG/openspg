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

import com.antgroup.openspg.server.schema.core.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.schema.core.model.type.SPGTypeEnum;
import java.util.EnumSet;

public class HypernymRelation extends BaseSemanticRelation {

  private static final long serialVersionUID = -3412789693382375319L;

  enum HypernymPredicateEnum {
    /** is-a */
    isA("是一个"),

    /** has-a */
    hasA("有一个"),

    /** locate-at */
    locateAt("位于");

    private final String nameZh;

    HypernymPredicateEnum(String nameZh) {
      this.nameZh = nameZh;
    }

    public String getNameZh() {
      return nameZh;
    }
  }

  /** Accept concept type only */
  private final SPGTypeIdentifier conceptTypeIdentifier;

  public HypernymRelation(SPGTypeIdentifier conceptTypeIdentifier) {
    super(EnumSet.of(SPGTypeEnum.CONCEPT_TYPE), EnumSet.of(SPGTypeEnum.CONCEPT_TYPE));

    this.conceptTypeIdentifier = conceptTypeIdentifier;
  }

  public SPGTypeIdentifier getConceptTypeIdentifier() {
    return conceptTypeIdentifier;
  }
}
