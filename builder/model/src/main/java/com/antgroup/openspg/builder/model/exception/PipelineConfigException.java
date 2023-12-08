package com.antgroup.openspg.builder.model.exception;

import com.antgroup.openspg.builder.model.BuilderException;

public class PipelineConfigException extends BuilderException {

  public PipelineConfigException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public PipelineConfigException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
