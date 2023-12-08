package com.antgroup.openspg.builder.core.physical.process.pattern;

import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.MappingPatternEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class RelationMappingPattern extends BaseMappingPattern {

  private final RelationIdentifier identifier;
  private MappingNodeConfig config;
  private Relation relation;
  private Map<String, List<PropertyMounter>> propertyMounters;

  public RelationMappingPattern(RelationIdentifier identifier) {
    super(MappingPatternEnum.RELATION);
    this.identifier = identifier;
  }

  public RelationMappingPattern(String identifier) {
    this(RelationIdentifier.parse(identifier));
  }

  @Override
  public void loadMappingConfig(MappingNodeConfig config) {
    this.config = config;
  }

  @Override
  public void loadAndCheckSchema(ProjectSchema schema) {}

  @Override
  public void loadPropertyMounter() {
    propertyMounters = loadPropertyMounter(config.getMappingSchemas());
  }

  @Override
  public List<BaseRecord> mapping(List<BaseRecord> inputs) {
    List<BaseSPGRecord> spgRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (isFiltered(record, config.getMappingFilters())) {
        continue;
      }
      BuilderRecord newRecord = doMapping(record, config.getMappingConfigs());
      RelationRecord relationRecord = toSPGRecord(newRecord, relation);

      spgRecords.add(relationRecord);
    }
    return spgRecords;
  }
}
