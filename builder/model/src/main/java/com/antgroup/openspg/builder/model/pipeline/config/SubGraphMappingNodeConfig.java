package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class SubGraphMappingNodeConfig extends BaseMappingNodeConfig {

  private final List<BaseMappingNodeConfig> childrenNodeConfigs;

  public SubGraphMappingNodeConfig(List<BaseMappingNodeConfig> childrenNodeConfigs) {
    super(NodeTypeEnum.SUBGRAPH_MAPPING);
    this.childrenNodeConfigs = childrenNodeConfigs;
  }

  @Override
  public List<BaseSPGIdentifier> getIdentifiers() {
    return childrenNodeConfigs.stream()
        .flatMap(x -> x.getIdentifiers().stream())
        .distinct()
        .collect(Collectors.toList());
  }
}
