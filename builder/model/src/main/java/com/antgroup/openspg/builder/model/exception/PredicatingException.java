package com.antgroup.openspg.builder.model.exception;

public class PredicatingException extends BuilderRecordException {

  public PredicatingException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public PredicatingException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
