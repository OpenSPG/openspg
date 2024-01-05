package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import java.util.List;
import lombok.Getter;

@Getter
public class RelationMappingNodeConfig extends BaseNodeConfig {

  private final String relation;

  private final List<SPGTypeMappingNodeConfig.MappingFilter> mappingFilters;

  private final List<SPGTypeMappingNodeConfig.MappingConfig> mappingConfigs;

  public RelationMappingNodeConfig(
      String relation,
      List<SPGTypeMappingNodeConfig.MappingFilter> mappingFilters,
      List<SPGTypeMappingNodeConfig.MappingConfig> mappingConfigs) {
    super(NodeTypeEnum.RELATION_MAPPING);
    this.relation = relation;
    this.mappingFilters = mappingFilters;
    this.mappingConfigs = mappingConfigs;
  }
}
