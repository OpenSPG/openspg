package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import java.util.List;

import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.google.common.collect.Lists;
import lombok.Getter;

@Getter
public class RelationMappingNodeConfig extends BaseMappingNodeConfig {

  private final String relation;

  private final List<MappingFilter> mappingFilters;

  private final List<MappingConfig> mappingConfigs;

  public RelationMappingNodeConfig(
      String relation, List<MappingFilter> mappingFilters, List<MappingConfig> mappingConfigs) {
    super(NodeTypeEnum.RELATION_MAPPING);
    this.relation = relation;
    this.mappingFilters = mappingFilters;
    this.mappingConfigs = mappingConfigs;
  }

  @Override
  public List<BaseSPGIdentifier> getIdentifiers() {
    return Lists.newArrayList(RelationIdentifier.parse(relation));
  }
}
