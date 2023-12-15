package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;

@Getter
public class SPGTypeMappingNodeConfig extends BaseMappingNodeConfig {

  private final String spgType;

  private final List<MappingFilter> mappingFilters;

  private final List<MappingConfig> mappingConfigs;

  public SPGTypeMappingNodeConfig(
      String spgType, List<MappingFilter> mappingFilters, List<MappingConfig> mappingConfigs) {
    super(NodeTypeEnum.SPG_TYPE_MAPPING);
    this.spgType = spgType;
    this.mappingFilters = mappingFilters;
    this.mappingConfigs = mappingConfigs;
  }

  @Override
  public List<BaseSPGIdentifier> getIdentifiers() {
    return Lists.newArrayList(SPGTypeIdentifier.parse(spgType));
  }
}
