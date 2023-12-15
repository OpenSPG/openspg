/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.server.common.model.exception;

import com.antgroup.openspg.server.common.model.MessageFormatter;
import org.apache.commons.lang3.StringUtils;

/** This should be a superclass of exceptions arising from OpenSPG code. */
public class OpenSPGException extends RuntimeException {

  public OpenSPGException(
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String messagePattern,
      Object[] args) {
    super(format(messagePattern, args), cause, enableSuppression, writableStackTrace);
  }

  private static String format(String messagePattern, Object[] args) {
    if (StringUtils.isBlank(messagePattern)) {
      return "";
    } else {
      return MessageFormatter.format(messagePattern, args);
    }
  }
}
