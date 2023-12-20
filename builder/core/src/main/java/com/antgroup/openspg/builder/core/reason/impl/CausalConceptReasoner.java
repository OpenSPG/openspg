package com.antgroup.openspg.builder.core.reason.impl;

import com.antgroup.openspg.builder.core.reason.ConceptReasoner;
import com.antgroup.openspg.builder.core.reason.ReasonerProcessorUtils;
import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyValue;
import com.antgroup.openspg.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.core.schema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.core.schema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.runner.local.KGReasonerLocalRunner;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;

public class CausalConceptReasoner implements ConceptReasoner<LogicalCausationSemantic> {

  @Setter private InductiveConceptReasoner inductiveConceptReasoner;
  @Setter private BuilderCatalog builderCatalog;
  @Setter private Catalog catalog;
  @Setter private GraphState<IVertexId> graphState;

  @Override
  public List<BaseSPGRecord> reason(
      List<BaseSPGRecord> records, LogicalCausationSemantic conceptSemantic) {
    List<BaseSPGRecord> results = new ArrayList<>(records);
    propagate(records, conceptSemantic, results);
    return results;
  }

  private void propagate(
      List<BaseSPGRecord> spgRecords,
      LogicalCausationSemantic conceptSemantic,
      List<BaseSPGRecord> results) {
    List<BaseAdvancedRecord> toPropagated = new ArrayList<>();
    for (BaseSPGRecord spgRecord : spgRecords) {
      if (!(spgRecord instanceof BaseAdvancedRecord)) {
        continue;
      }
      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) spgRecord;
      SPGPropertyRecord belongToPropertyRecord =
          advancedRecord.getPredicateProperty(SystemPredicateEnum.BELONG_TO);
      if (belongToPropertyRecord == null) {
        // if it does not belong to a concept, then it is not propagated
        // because propagation is based on concepts.
        continue;
      }

      SPGPropertyValue propertyValue = belongToPropertyRecord.getValue();
      if (propertyValue == null
          || !propertyValue.getRaw().contains(conceptSemantic.getSubjectIdentifier().getId())) {
        // If the concept of belonging is not the starting point of the current leadTo,
        // then propagation is not carried out.
        continue;
      }
      toPropagated.add(advancedRecord);
    }

    // initiating this round of event propagation based on toPropagated.
    for (BaseAdvancedRecord advancedRecord : toPropagated) {
      List<BaseSPGRecord> leadToRecords = leadTo(advancedRecord, conceptSemantic);
      if (CollectionUtils.isEmpty(leadToRecords)) {
        continue;
      }
      results.addAll(leadToRecords);

      // Determine the belongTo of the events propagated out.
      for (BaseSPGRecord leadToRecord : leadToRecords) {
        ConceptList conceptList =
            ReasonerProcessorUtils.getConceptList(leadToRecord, builderCatalog);
        if (conceptList == null) {
          continue;
        }

        List<BaseSPGRecord> nextSpgRecords = Lists.newArrayList(leadToRecord);
        for (DynamicTaxonomySemantic belongTo :
            conceptList.getDynamicTaxonomyList(conceptSemantic.getObjectIdentifier())) {
          nextSpgRecords = inductiveConceptReasoner.reason(nextSpgRecords, belongTo);
        }

        for (LogicalCausationSemantic nextLeadTo :
            conceptList.getLogicalCausation(conceptSemantic.getObjectIdentifier())) {
          propagate(nextSpgRecords, nextLeadTo, results);
        }
      }
    }
  }

  private List<BaseSPGRecord> leadTo(BaseAdvancedRecord record, LogicalCausationSemantic leadTo) {
    LocalReasonerTask reasonerTask = new LocalReasonerTask();

    reasonerTask.setCatalog(catalog);
    reasonerTask.setGraphState(graphState);
    reasonerTask.setDsl(leadTo.getLogicalRule().getContent());
    reasonerTask.setStartIdList(Lists.newArrayList(Tuple2.apply(record.getId(), record.getName())));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult reasonerResult = runner.run(reasonerTask);
    return ReasonerProcessorUtils.toSpgRecords(reasonerResult, builderCatalog);
  }
}
