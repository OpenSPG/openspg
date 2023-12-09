package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.config.SubGraphMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class SubGraphMappingNode extends BaseLogicalNode<SubGraphMappingNodeConfig> {

  public SubGraphMappingNode(String id, String name, SubGraphMappingNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.SUBGRAPH_MAPPING, nodeConfig);
  }
}
