package com.antgroup.openspg.builder.model.exception;

public class PropertyNormalizeException extends BuilderRecordException {

  public PropertyNormalizeException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public PropertyNormalizeException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
