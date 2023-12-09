package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class LLMBasedExtractNodeConfig extends BaseExtractNodeConfig {

  public LLMBasedExtractNodeConfig() {
    super(NodeTypeEnum.LLM_BASED_EXTRACT);
  }
}
