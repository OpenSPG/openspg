package com.antgroup.openspg.builder.core.reason;

import com.antgroup.openspg.builder.core.physical.process.BaseProcessor;
import com.antgroup.openspg.builder.core.reason.impl.CausalConceptReasoner;
import com.antgroup.openspg.builder.core.reason.impl.InductiveConceptReasoner;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.core.schema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.reasoner.catalog.impl.OpenSPGCatalog;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.google.common.collect.Lists;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ReasonProcessor extends BaseProcessor<ReasonProcessor.ReasonerNodeConfig> {

  public static class ReasonerNodeConfig extends BaseNodeConfig {
    public ReasonerNodeConfig() {
      super(NodeTypeEnum.REASON);
    }
  }

  private InductiveConceptReasoner inductiveConceptReasoner;
  private CausalConceptReasoner causalConceptReasoner;

  public ReasonProcessor() {
    super("", "", null);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    Catalog catalog = buildCatalog();
    GraphState<IVertexId> graphState =
        buildGraphState(context.getCatalog().getGraphStoreConnInfo());
    this.inductiveConceptReasoner = new InductiveConceptReasoner();
    this.inductiveConceptReasoner.setCatalog(catalog);
    this.inductiveConceptReasoner.setGraphState(graphState);

    this.causalConceptReasoner = new CausalConceptReasoner();
    this.causalConceptReasoner.setCatalog(catalog);
    this.causalConceptReasoner.setBuilderCatalog(context.getCatalog());
    this.causalConceptReasoner.setGraphState(graphState);
    this.causalConceptReasoner.setInductiveConceptReasoner(inductiveConceptReasoner);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> results = new ArrayList<>();
    for (BaseRecord baseRecord : inputs) {
      if (!(baseRecord instanceof BaseAdvancedRecord)) {
        continue;
      }
      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) baseRecord;

      // now only supports single classification of one entity type
      ConceptList conceptList =
          ReasonerProcessorUtils.getConceptList(advancedRecord, context.getCatalog());
      if (conceptList == null) {
        continue;
      }

      // perform inductive and causal reasoning logic on the input advancedRecord
      results.addAll(reasoning(advancedRecord, conceptList));
    }
    return results;
  }

  private List<BaseSPGRecord> reasoning(BaseAdvancedRecord record, ConceptList conceptList) {
    // run the inductive reasoning logic
    List<BaseSPGRecord> spgRecords = Lists.newArrayList(record);
    for (DynamicTaxonomySemantic belongTo : conceptList.getDynamicTaxonomyList()) {
      spgRecords = inductiveConceptReasoner.reason(spgRecords, belongTo);
    }

    // then run causal reasoning logic
    for (LogicalCausationSemantic leadTo : conceptList.getLogicalCausation()) {
      spgRecords = causalConceptReasoner.reason(spgRecords, leadTo);
    }
    return spgRecords;
  }

  private Catalog buildCatalog() {
    Catalog catalog =
        new OpenSPGCatalog(context.getProjectId(), null, context.getCatalog().getProjectSchema());
    catalog.init();
    return catalog;
  }

  private GraphState<IVertexId> buildGraphState(GraphStoreConnectionInfo connInfo) {
    CloudExtGraphState cloudExtGraphState = new CloudExtGraphState();

    Map<String, Object> params = new HashMap<>();
    params.put("cloudext.graphstore.schema", connInfo.getScheme());
    params.putAll(connInfo.getParams());
    cloudExtGraphState.init((Map) Collections.unmodifiableMap(params));
    return cloudExtGraphState;
  }

  @Override
  public void close() throws Exception {}
}
