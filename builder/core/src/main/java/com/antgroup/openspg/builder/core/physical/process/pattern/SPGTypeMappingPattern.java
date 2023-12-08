package com.antgroup.openspg.builder.core.physical.process.pattern;

import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.MappingPatternEnum;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class SPGTypeMappingPattern extends BaseMappingPattern {

  private final SPGTypeIdentifier identifier;
  private MappingNodeConfig config;
  private BaseSPGType spgType;
  private Map<String, List<PropertyMounter>> propertyMounters;

  public SPGTypeMappingPattern(SPGTypeIdentifier identifier) {
    super(MappingPatternEnum.SPG_TYPE);
    this.identifier = identifier;
  }

  public SPGTypeMappingPattern(String identifier) {
    this(SPGTypeIdentifier.parse(identifier));
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
    List<BaseRecord> spgRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (isFiltered(record, config.getMappingFilters())) {
        continue;
      }
      BuilderRecord newRecord = doMapping(record, config.getMappingConfigs());
      BaseAdvancedRecord advancedRecord = toSPGRecord(newRecord, spgType);
      propertyMount(advancedRecord, propertyMounters);

      spgRecords.add(advancedRecord);
    }
    return spgRecords;
  }
}
