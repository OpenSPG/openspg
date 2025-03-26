/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.common.util.exception;

import com.antgroup.openspg.common.util.exception.message.Message;
import java.util.Arrays;

/** spgApp current exception class */
public class SpgException extends RuntimeException {

  private static final long serialVersionUID = -5354789951776002075L;

  protected String code;
  protected String message;
  protected String[] params;

  public SpgException(Message errorMessage) {
    super(errorMessage.getMessage());
    this.code = errorMessage.getCode();
    this.message = errorMessage.getMessage();
  }

  public SpgException(Throwable t, Message errorMessage) {
    super(errorMessage.getMessage(), t);
    this.code = errorMessage.getCode();
    this.message = errorMessage.getMessage();
  }

  public SpgException(Message errorMessage, String... params) {
    super(String.format(errorMessage.getMessage(), params));
    this.code = errorMessage.getCode();
    this.message = String.format(errorMessage.getMessage(), params);
    this.params = params;
  }

  public SpgException(Throwable t, Message errorMessage, String... params) {
    super(String.format(errorMessage.getMessage(), params), t);
    this.code = errorMessage.getCode();
    this.message = String.format(errorMessage.getMessage(), params);
    this.params = params;
  }

  public SpgException(String code, String message, String... params) {
    super(String.format(message, params));
    this.code = code;
    this.message = String.format(message, params);
    this.params = params;
  }

  public SpgException(String code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  /**
   * Getter method for property <tt>code</tt>.
   *
   * @return property value of code
   */
  public String getCode() {
    return code;
  }

  /**
   * Setter method for property <tt>code</tt>.
   *
   * @param code value to be assigned to property code
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Getter method for property <tt>message</tt>.
   *
   * @return property value of message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Setter method for property <tt>message</tt>.
   *
   * @param message value to be assigned to property message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Getter method for property <tt>params</tt>.
   *
   * @return property value of params
   */
  public String[] getParams() {
    return params;
  }

  /**
   * Setter method for property <tt>params</tt>.
   *
   * @param params value to be assigned to property params
   */
  public void setParams(String[] params) {
    this.params = params;
  }

  /**
   * toString
   *
   * @return
   */
  @Override
  public String toString() {
    return "SpgAppException{"
        + "code='"
        + code
        + '\''
        + ", message='"
        + message
        + '\''
        + ", params="
        + Arrays.toString(params)
        + '}';
  }
}
