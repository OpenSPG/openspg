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

package com.antgroup.openspg.server.schema.core.model.type;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.antgroup.openspg.server.schema.core.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.server.schema.core.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.server.schema.core.model.semantic.LogicalCausationSemantic;
import java.util.ArrayList;
import java.util.List;

public class ConceptList extends BaseValObj {

  private final List<Concept> concepts;

  public ConceptList(List<Concept> concepts) {
    this.concepts = concepts;
  }

  public List<Concept> getConcepts() {
    return concepts;
  }

  public List<DynamicTaxonomySemantic> getDynamicTaxonomyList() {
    List<DynamicTaxonomySemantic> semantics = new ArrayList<>();
    for (Concept concept : concepts) {
      semantics.addAll(concept.getDynamicTaxonomySemantics());
    }
    return semantics;
  }

  public List<DynamicTaxonomySemantic> getDynamicTaxonomyList(ConceptIdentifier conceptName) {
    List<DynamicTaxonomySemantic> semantics = new ArrayList<>();
    for (Concept concept : concepts) {
      if (concept.getName().equals(conceptName)) {
        semantics.addAll(concept.getDynamicTaxonomySemantics());
      }
    }
    return semantics;
  }

  public List<LogicalCausationSemantic> getLogicalCausation() {
    List<LogicalCausationSemantic> semantics = new ArrayList<>();
    for (Concept concept : concepts) {
      semantics.addAll(concept.getLogicalCausationSemantics());
    }
    return semantics;
  }

  public List<LogicalCausationSemantic> getLogicalCausation(ConceptIdentifier conceptName) {
    List<LogicalCausationSemantic> semantics = new ArrayList<>();
    for (Concept concept : concepts) {
      if (concept.getName().equals(conceptName)) {
        semantics.addAll(concept.getLogicalCausationSemantics());
      }
    }
    return semantics;
  }
}
