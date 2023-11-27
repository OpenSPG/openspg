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

package com.antgroup.openspg.server.core.schema.model.type;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.antgroup.openspg.server.core.schema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.server.core.schema.model.semantic.BaseConceptSemantic;
import com.antgroup.openspg.server.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.server.core.schema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.server.core.schema.model.semantic.SystemPredicateEnum;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The abstraction of entity sets with similar features, is a general summary and description of
 * entity cognition.
 */
public class Concept extends BaseValObj {

  private static final long serialVersionUID = -8896309568664475638L;

  /** The unique name of concept. */
  private final ConceptIdentifier name;

  /** The list of semantic defined on concept. */
  private final List<BaseConceptSemantic> semantics;

  public Concept(ConceptIdentifier name, List<BaseConceptSemantic> semantics) {
    this.name = name;
    this.semantics = semantics;
  }

  public ConceptIdentifier getName() {
    return name;
  }

  public List<BaseConceptSemantic> getSemantics() {
    return semantics;
  }

  public List<DynamicTaxonomySemantic> getDynamicTaxonomySemantics() {
    return semantics.stream()
        .map(
            semantic -> {
              if (!(semantic instanceof DynamicTaxonomySemantic)) {
                return null;
              }
              return (DynamicTaxonomySemantic) semantic;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public List<LogicalCausationSemantic> getLogicalCausationSemantics() {
    return semantics.stream()
        .map(
            semantic -> {
              if (!(semantic instanceof LogicalCausationSemantic)) {
                return null;
              }
              LogicalCausationSemantic relationSemantic = (LogicalCausationSemantic) semantic;
              if (!SystemPredicateEnum.LEAD_TO
                  .getName()
                  .equals(relationSemantic.getPredicateIdentifier().getName())) {
                return null;
              }
              return relationSemantic;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
