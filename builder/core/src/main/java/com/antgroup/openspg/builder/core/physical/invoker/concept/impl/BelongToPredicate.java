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

import com.antgroup.kg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.kg.reasoner.graphstate.GraphState;
import com.antgroup.kg.reasoner.local.KGReasonerLocalRunner;
import com.antgroup.kg.reasoner.local.model.LocalReasonerResult;
import com.antgroup.kg.reasoner.local.model.LocalReasonerTask;
import com.antgroup.kg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.concept.ConceptPredicate;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.concept.convertor.ReasonerResultConvertor;
import com.antgroup.openspg.core.spgbuilder.engine.runtime.RuntimeContext;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.spgschema.model.semantic.DynamicTaxonomySemantic;
import com.google.common.collect.Lists;
import java.util.List;
import scala.Tuple2;

public class BelongToPredicate implements ConceptPredicate<DynamicTaxonomySemantic> {

  private Catalog catalog;
  private GraphState<IVertexId> graphState;

  @Override
  public void init(RuntimeContext context) {}

  @Override
  public List<BaseSPGRecord> process(
      List<BaseSPGRecord> spgRecords, DynamicTaxonomySemantic belongTo) {
    for (BaseSPGRecord spgRecord : spgRecords) {
      LocalReasonerTask reasonerTask = new LocalReasonerTask();

      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) spgRecord;
      reasonerTask.setCatalog(catalog);
      reasonerTask.setGraphState(graphState);
      reasonerTask.setDsl(belongTo.getLogicalRule().getContent());
      reasonerTask.setStartIdList(Lists.newArrayList(getTupleFrom(advancedRecord)));

      KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
      LocalReasonerResult reasonerResult = runner.run(reasonerTask);
      ReasonerResultConvertor.setBelongToProperty(reasonerResult, advancedRecord);
    }
    return spgRecords;
  }

  private Tuple2<String, String> getTupleFrom(BaseAdvancedRecord advancedRecord) {
    return Tuple2.apply(advancedRecord.getId(), advancedRecord.getName());
  }

  public void setCatalog(Catalog catalog) {
    this.catalog = catalog;
  }

  public void setGraphState(GraphState<IVertexId> graphState) {
    this.graphState = graphState;
  }
}
