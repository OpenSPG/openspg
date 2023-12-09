package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.pipeline.config.BaseExtractNodeConfig;

public abstract class BaseExtractProcessor extends BaseProcessor<BaseExtractNodeConfig> {

  public BaseExtractProcessor(String id, String name, BaseExtractNodeConfig config) {
    super(id, name, config);
  }
}
