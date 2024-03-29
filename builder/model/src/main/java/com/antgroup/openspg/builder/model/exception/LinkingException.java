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

package com.antgroup.openspg.builder.model.exception;

public class LinkingException extends BuilderRecordException {

  public LinkingException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  public LinkingException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }
}
