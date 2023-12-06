package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.core.physical.process.BaseProcessor;

public class PropertyMounterException extends BuilderRecordException {

  public PropertyMounterException(
      BaseProcessor<?> processor, Throwable cause, String messagePattern, Object... args) {
    super(processor, cause, messagePattern, args);
  }
}
