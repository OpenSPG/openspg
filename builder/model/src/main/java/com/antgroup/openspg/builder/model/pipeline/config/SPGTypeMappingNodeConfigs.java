package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import java.util.List;
import lombok.Getter;

@Getter
public class SPGTypeMappingNodeConfigs extends BaseNodeConfig {

  private final List<SPGTypeMappingNodeConfig> mappingNodeConfigs;

  public SPGTypeMappingNodeConfigs(List<SPGTypeMappingNodeConfig> mappingNodeConfigs) {
    super(NodeTypeEnum.SPG_TYPE_MAPPINGS);
    this.mappingNodeConfigs = mappingNodeConfigs;
  }
}
