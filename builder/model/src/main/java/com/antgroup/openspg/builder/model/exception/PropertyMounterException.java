package com.antgroup.openspg.builder.model.exception;

public class PropertyMounterException extends BuilderRecordException {

  public PropertyMounterException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public PropertyMounterException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
