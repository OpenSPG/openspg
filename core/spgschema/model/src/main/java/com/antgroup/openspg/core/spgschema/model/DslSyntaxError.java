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

package com.antgroup.openspg.core.spgschema.model;

import com.antgroup.openspg.common.model.exception.OpenSPGException;

public class DslSyntaxError extends OpenSPGException {

  private DslSyntaxError(Throwable cause, String messagePattern, Object... args) {
    super(cause, true, true, messagePattern, args);
  }

  private DslSyntaxError(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }

  public static DslSyntaxError dslSyntaxError(String errorPart) {
    return new DslSyntaxError("dsl has syntax error: {}", errorPart);
  }

  public static DslSyntaxError dslSyntaxError(Throwable cause) {
    return new DslSyntaxError(cause, "dsl is invalid: {}", cause.getMessage());
  }
}
