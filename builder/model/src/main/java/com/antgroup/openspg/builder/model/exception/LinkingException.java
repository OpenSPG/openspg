package com.antgroup.openspg.builder.model.exception;

public class LinkingException extends BuilderRecordException {

  public LinkingException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public LinkingException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
