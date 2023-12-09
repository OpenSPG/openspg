package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.config.RelationMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class RelationMappingNode extends BaseLogicalNode<RelationMappingNodeConfig> {

  public RelationMappingNode(String id, String name, RelationMappingNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.RELATION_MAPPING, nodeConfig);
  }
}
