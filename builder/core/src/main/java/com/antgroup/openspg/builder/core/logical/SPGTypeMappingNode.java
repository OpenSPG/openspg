package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class SPGTypeMappingNode extends BaseLogicalNode<SPGTypeMappingNodeConfig> {

  public SPGTypeMappingNode(String id, String name, SPGTypeMappingNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.SPG_TYPE_MAPPING, nodeConfig);
  }
}
