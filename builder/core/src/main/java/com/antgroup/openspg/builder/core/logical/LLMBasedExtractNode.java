package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.config.LLMBasedExtractNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class LLMBasedExtractNode extends BaseLogicalNode<LLMBasedExtractNodeConfig> {

  public LLMBasedExtractNode(String id, String name, LLMBasedExtractNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.LLM_BASED_EXTRACT, nodeConfig);
  }
}
