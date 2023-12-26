package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.strategy.linking.impl.SearchBasedLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.RelationMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import java.util.ArrayList;
import java.util.List;

public class RelationMappingProcessor extends BaseMappingProcessor<RelationMappingNodeConfig> {

  private final RelationIdentifier identifier;
  private Relation relation;
  private RecordLinking recordLinking;

  public RelationMappingProcessor(String id, String name, RelationMappingNodeConfig config) {
    super(id, name, config);
    this.identifier = RelationIdentifier.parse(config.getRelation());
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    this.relation = (Relation) loadSchema(identifier, context.getCatalog());

    this.recordLinking = new RecordLinkingImpl(config.getMappingConfigs());
    this.recordLinking.setDefaultPropertyLinking(new SearchBasedLinking());
    this.recordLinking.init(context);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> spgRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (isFiltered(record, config.getMappingFilters(), identifier)) {
        continue;
      }

      BuilderRecord mappedRecord = mapping(record, config.getMappingConfigs());
      RelationRecord relationRecord = toSPGRecord(mappedRecord, relation);
      recordLinking.linking(relationRecord);
      spgRecords.add(relationRecord);
    }
    return spgRecords;
  }

  @Override
  public void close() throws Exception {}
}
