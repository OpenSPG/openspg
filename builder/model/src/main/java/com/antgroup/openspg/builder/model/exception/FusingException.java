package com.antgroup.openspg.builder.model.exception;

public class FusingException extends BuilderRecordException {

  public FusingException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public FusingException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
