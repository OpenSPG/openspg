package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.operator.linking.RecordLinking;
import com.antgroup.openspg.builder.core.operator.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.operator.linking.impl.SearchBasedLinking;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
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

  private Relation relation;
  private RecordLinking recordLinking;

  public RelationMappingProcessor(String id, String name, RelationMappingNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    RelationIdentifier identifier = RelationIdentifier.parse(config.getRelation());
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
      RelationRecord relationRecord =
          relationRecordMapping(record, relation, config, recordLinking);
      if (relationRecord != null) {
        spgRecords.add(relationRecord);
      }
    }
    return spgRecords;
  }

  private static RelationRecord relationRecordMapping(
      BuilderRecord record,
      Relation relation,
      RelationMappingNodeConfig mappingConfig,
      RecordLinking recordLinking) {
    if (isFiltered(record, mappingConfig.getMappingFilters())) {
      return null;
    }

    BuilderRecord mappedRecord = mapping(record, mappingConfig.getMappingConfigs());
    RelationRecord relationRecord = toSPGRecord(mappedRecord, relation);
    recordLinking.propertyLinking(relationRecord);
    return relationRecord;
  }

  @Override
  public void close() throws Exception {}
}
