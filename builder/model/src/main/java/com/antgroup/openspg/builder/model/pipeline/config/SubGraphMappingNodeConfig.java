package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import java.util.List;
import lombok.Getter;

@Getter
public class SubGraphMappingNodeConfig extends BaseMappingNodeConfig {

  private final List<BaseMappingNodeConfig> childrenNodeConfigs;

  public SubGraphMappingNodeConfig(List<BaseMappingNodeConfig> childrenNodeConfigs) {
    super(NodeTypeEnum.SUBGRAPH_MAPPING);
    this.childrenNodeConfigs = childrenNodeConfigs;
  }
}
