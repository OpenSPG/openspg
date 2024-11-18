/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.builder.core.reason.impl;

import com.antgroup.openspg.builder.core.reason.ConceptReasoner;
import com.antgroup.openspg.builder.core.reason.ReasonerProcessorUtils;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;

@Slf4j
public class InductiveConceptReasoner implements ConceptReasoner<DynamicTaxonomySemantic> {

  @Setter private Catalog catalog;
  @Setter private GraphState<IVertexId> graphState;

  @Override
  public List<BaseSPGRecord> reason(
      List<BaseSPGRecord> records, DynamicTaxonomySemantic conceptSemantic) {
    for (BaseRecord spgRecord : records) {
      LocalReasonerTask reasonerTask = new LocalReasonerTask();

      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) spgRecord;
      reasonerTask.setCatalog(catalog);
      reasonerTask.setGraphState(graphState);
      reasonerTask.setDsl(conceptSemantic.getLogicalRule().getContent());
      reasonerTask.setStartIdList(Lists.newArrayList(getTupleFrom(advancedRecord)));
      Map<String, Object> params = new HashMap<>();
      params.put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, "false");
      params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, "true");
      reasonerTask.setParams(params);
      reasonerTask.setExecutionRecorder(new DefaultRecorder());

      LocalReasonerRunner runner = new LocalReasonerRunner();
      LocalReasonerResult reasonerResult = runner.run(reasonerTask);
      log.info(reasonerTask.getExecutionRecorder().toReadableString());
      ReasonerProcessorUtils.setBelongToProperty(reasonerResult, advancedRecord);
    }
    return records;
  }

  private Tuple2<String, String> getTupleFrom(BaseAdvancedRecord advancedRecord) {
    return Tuple2.apply(advancedRecord.getId(), advancedRecord.getName());
  }
}
