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

package com.antgroup.openspg.server.schema.core.model.semantic;

import com.antgroup.openspg.server.schema.core.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.server.schema.core.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.server.schema.core.model.identifier.SPGTypeIdentifier;

/**
 * The taxonomic semantic between entity and concept. the predicate is fixed to be {@code
 * SystemPredicate.BelongTo} and the object is target concept. usually it contains a logical rule to
 * express the taxonomic condition.
 */
public class DynamicTaxonomySemantic extends BaseConceptSemantic {

  private static final long serialVersionUID = 8151467193342790248L;

  /** Identifier of the taxonomy predicate, it's fixed to be belongTo. */
  private final PredicateIdentifier predicateIdentifier =
      new PredicateIdentifier(SystemPredicateEnum.BELONG_TO.getName());

  /** The unique name of the object concept type. */
  private final SPGTypeIdentifier conceptTypeIdentifier;

  /** The name of the concept. */
  private final ConceptIdentifier conceptIdentifier;

  /** Details of the logic rule */
  private final LogicalRule logicalRule;

  public DynamicTaxonomySemantic(
      SPGTypeIdentifier conceptTypeIdentifier,
      ConceptIdentifier conceptIdentifier,
      LogicalRule logicalRule) {
    this.conceptTypeIdentifier = conceptTypeIdentifier;
    this.conceptIdentifier = conceptIdentifier;
    this.logicalRule = logicalRule;
  }

  public PredicateIdentifier getPredicateIdentifier() {
    return predicateIdentifier;
  }

  public SPGTypeIdentifier getConceptTypeIdentifier() {
    return conceptTypeIdentifier;
  }

  public ConceptIdentifier getConceptIdentifier() {
    return conceptIdentifier;
  }

  public LogicalRule getLogicalRule() {
    return logicalRule;
  }

  public RuleCode getRuleCode() {
    return logicalRule == null ? null : logicalRule.getCode();
  }
}
