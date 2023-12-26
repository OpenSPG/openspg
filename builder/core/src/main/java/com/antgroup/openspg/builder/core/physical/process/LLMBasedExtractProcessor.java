package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.pipeline.config.LLMBasedExtractNodeConfig;

public class LLMBasedExtractProcessor extends BaseExtractProcessor<LLMBasedExtractNodeConfig> {

  public LLMBasedExtractProcessor(String id, String name, LLMBasedExtractNodeConfig config) {
    super(id, name, config);
  }
}
