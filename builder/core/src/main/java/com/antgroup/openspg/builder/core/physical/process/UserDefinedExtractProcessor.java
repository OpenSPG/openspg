package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.pipeline.config.UserDefinedExtractNodeConfig;

public class UserDefinedExtractProcessor
    extends BaseExtractProcessor<UserDefinedExtractNodeConfig> {

  public UserDefinedExtractProcessor(String id, String name, UserDefinedExtractNodeConfig config) {
    super(id, name, config);
  }
}
