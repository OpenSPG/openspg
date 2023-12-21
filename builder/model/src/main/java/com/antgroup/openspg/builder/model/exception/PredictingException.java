package com.antgroup.openspg.builder.model.exception;

public class PredictingException extends BuilderRecordException {

  public PredictingException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public PredictingException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
