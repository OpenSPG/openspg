package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.core.BuilderException;

public class PipelineConfigException extends BuilderException {
  protected PipelineConfigException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }
}
