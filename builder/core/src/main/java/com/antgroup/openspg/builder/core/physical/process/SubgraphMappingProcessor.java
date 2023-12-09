package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.RelationMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.SubGraphMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubgraphMappingProcessor extends BaseMappingProcessor<SubGraphMappingNodeConfig> {

  private final Map<BaseSPGIdentifier, BaseMappingNodeConfig> mappingNodeConfigs = new HashMap<>();
  private final Map<BaseSPGIdentifier, BaseOntology> ontologies = new HashMap<>();
  private final Map<BaseSPGIdentifier, Map<String, List<PropertyMounter>>> propertyMounters =
      new HashMap<>();

  public SubgraphMappingProcessor(String id, String name, SubGraphMappingNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    for (BaseMappingNodeConfig mappingConfig : config.getMappingConfigs()) {
      switch (mappingConfig.getType()) {
        case SPG_TYPE_MAPPING:
          SPGTypeMappingNodeConfig mappingConfig1 = (SPGTypeMappingNodeConfig) mappingConfig;
          SPGTypeIdentifier identifier1 = SPGTypeIdentifier.parse(mappingConfig1.getSpgType());
          this.ontologies.put(identifier1, loadSchema(identifier1, context.getProjectSchema()));
          this.propertyMounters.put(
              identifier1, loadPropertyMounters(mappingConfig1.getMappingConfigs()));
          mappingNodeConfigs.put(identifier1, mappingConfig1);
          break;
        case RELATION_MAPPING:
          RelationMappingNodeConfig mappingConfig2 = (RelationMappingNodeConfig) mappingConfig;
          RelationIdentifier identifier2 = RelationIdentifier.parse(mappingConfig2.getRelation());
          this.ontologies.put(identifier2, loadSchema(identifier2, context.getProjectSchema()));
          mappingNodeConfigs.put(identifier2, mappingConfig2);
        default:
          throw new PipelineConfigException("illegal mapping config for SubgraphMappingProcessor");
      }
    }
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> spgRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (record.getIdentifier() != null) {
        // if the spgType or relation to which the record belongs is identified in the record, it is
        // directly mapped to the spgType or relation.
        spgRecords.add(mappingRecordWithIdentifier(record, record.getIdentifier()));
      } else {
        // otherwise, traverse all mapping configurations for mapping.
        for (BaseSPGIdentifier identifier : mappingNodeConfigs.keySet()) {
          spgRecords.add(mappingRecordWithIdentifier(record, identifier));
        }
      }
    }
    return spgRecords;
  }

  private BaseRecord mappingRecordWithIdentifier(
      BuilderRecord record, BaseSPGIdentifier identifier) {
    BaseMappingNodeConfig mappingNodeConfig = mappingNodeConfigs.get(identifier);
    BaseOntology baseOntology = ontologies.get(identifier);

    BaseRecord result = null;
    switch (mappingNodeConfig.getType()) {
      case SPG_TYPE_MAPPING:
        Map<String, List<PropertyMounter>> mounters = propertyMounters.get(identifier);
        result =
            SPGTypeMappingProcessor.spgTypeRecordMapping(
                record,
                (BaseSPGType) baseOntology,
                (SPGTypeMappingNodeConfig) mappingNodeConfig,
                mounters);
        break;
      case RELATION_MAPPING:
        result =
            RelationMappingProcessor.relationRecordMapping(
                record, (Relation) baseOntology, (RelationMappingNodeConfig) mappingNodeConfig);
        break;
      default:
        throw new PipelineConfigException("illegal mapping config for SubgraphMappingProcessor");
    }
    return result;
  }

  @Override
  public void close() throws Exception {}
}
