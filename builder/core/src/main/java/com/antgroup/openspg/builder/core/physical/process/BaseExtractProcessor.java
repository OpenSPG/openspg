package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.pipeline.config.BaseExtractNodeConfig;

public abstract class BaseExtractProcessor<T extends BaseExtractNodeConfig>
    extends BaseProcessor<T> {

  public BaseExtractProcessor(String id, String name, T config) {
    super(id, name, config);
  }
}
