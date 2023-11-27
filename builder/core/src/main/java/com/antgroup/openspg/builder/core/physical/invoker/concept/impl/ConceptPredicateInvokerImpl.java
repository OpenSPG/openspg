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

package com.antgroup.openspg.builder.core.physical.invoker.concept.impl;

import com.antgroup.kg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.kg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.kg.reasoner.graphstate.GraphState;
import com.antgroup.kg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.concept.ConceptPredicateInvoker;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.concept.ConceptService;
import com.antgroup.openspg.core.spgbuilder.engine.runtime.RuntimeContext;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.spgreasoner.service.util.LocalRunnerUtils;
import com.antgroup.openspg.core.spgschema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.core.spgschema.model.type.ConceptList;
import java.util.ArrayList;
import java.util.List;

public class ConceptPredicateInvokerImpl implements ConceptPredicateInvoker {

  private final ConceptService conceptService = new ConceptServiceImpl();
  private RuntimeContext context;
  private BelongToPredicate belongToPredicate;
  private LeadToPredicate leadToPredicate;

  @Override
  public void init(RuntimeContext context) {
    this.context = context;
    Catalog catalog =
        LocalRunnerUtils.buildCatalog(
            context.getProjectId(), new KgSchemaConnectionInfo(context.getSchemaUrl(), ""));
    GraphState<IVertexId> graphState =
        LocalRunnerUtils.buildGraphState(context.getGraphStoreConnInfo());

    belongToPredicate = new BelongToPredicate();
    belongToPredicate.setCatalog(catalog);
    belongToPredicate.setGraphState(graphState);

    leadToPredicate = new LeadToPredicate(belongToPredicate, conceptService);
    leadToPredicate.setCatalog(catalog);
    leadToPredicate.setGraphState(graphState);
  }

  @Override
  public List<BaseSPGRecord> invoke(BaseAdvancedRecord record) {
    List<BaseSPGRecord> results = new ArrayList<>(1);
    results.add(record);

    if (!context.isEnableLeadTo()) {
      return results;
    }

    ConceptList conceptList = conceptService.query(record);
    if (conceptList == null) {
      return results;
    }

    // First run the belongTo concept semantics and assign the result to the record field.
    for (DynamicTaxonomySemantic belongTo : conceptList.getDynamicTaxonomyList()) {
      results = belongToPredicate.process(results, belongTo);
    }

    // Then run leadTo based on the results of belongTo
    for (LogicalCausationSemantic leadTo : conceptList.getLogicalCausation()) {
      results = leadToPredicate.process(results, leadTo);
    }
    return results;
  }
}
