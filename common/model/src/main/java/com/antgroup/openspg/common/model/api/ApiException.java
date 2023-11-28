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

package com.antgroup.openspg.common.model.api;

import com.antgroup.openspg.common.model.exception.OpenSPGException;

public class ApiException extends OpenSPGException {

  private ApiException(Throwable cause, String messagePattern, Object... args) {
    super(cause, true, true, messagePattern, args);
  }

  private ApiException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }

  public static ApiException withErrorMsg(String errorMsg) {
    return new ApiException("request server with errorMsg: {}", errorMsg);
  }

  public static ApiException notFound(String name) {
    if (name == null) {
      name = "";
    }
    return new ApiException("{} not found!", name);
  }

  public static ApiException connectError(Throwable e) {
    return new ApiException(e, "connect server error");
  }
}
