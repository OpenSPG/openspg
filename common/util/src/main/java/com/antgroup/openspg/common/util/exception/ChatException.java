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

public class ChatException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  protected String code;
  protected String message;
  protected String[] params;

  public ChatException(Message errorMessage) {
    super(errorMessage.getMessage());
    this.code = errorMessage.getCode();
    this.message = errorMessage.getMessage();
  }

  public ChatException(Throwable t, Message errorMessage) {
    super(errorMessage.getMessage(), t);
    this.code = errorMessage.getCode();
    this.message = errorMessage.getMessage();
  }

  public ChatException(Message errorMessage, String... params) {
    super(String.format(errorMessage.getMessage(), params));
    this.code = errorMessage.getCode();
    this.message = String.format(errorMessage.getMessage(), params);
    this.params = params;
  }

  public ChatException(Throwable t, Message errorMessage, String... params) {
    super(String.format(errorMessage.getMessage(), params), t);
    this.code = errorMessage.getCode();
    this.message = String.format(errorMessage.getMessage(), params);
    this.params = params;
  }

  public ChatException(String code, String message, String... params) {
    super(String.format(message, params));
    this.code = code;
    this.message = String.format(message, params);
    this.params = params;
  }

  public ChatException(String code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String[] getParams() {
    return params;
  }

  public void setParams(String[] params) {
    this.params = params;
  }

  @Override
  public String toString() {
    return "ChatException{"
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
