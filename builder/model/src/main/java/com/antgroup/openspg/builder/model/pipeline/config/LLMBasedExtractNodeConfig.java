package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import lombok.Getter;

@Getter
public class LLMBasedExtractNodeConfig extends BaseExtractNodeConfig {

  public LLMBasedExtractNodeConfig(OperatorConfig operatorConfig) {
    super(NodeTypeEnum.LLM_BASED_EXTRACT, operatorConfig);
  }
}
