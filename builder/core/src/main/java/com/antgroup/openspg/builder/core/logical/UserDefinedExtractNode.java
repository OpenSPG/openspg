package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.config.UserDefinedExtractNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class UserDefinedExtractNode extends BaseLogicalNode<UserDefinedExtractNodeConfig> {

  public UserDefinedExtractNode(String id, String name, UserDefinedExtractNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.USER_DEFINED_EXTRACT, nodeConfig);
  }
}
