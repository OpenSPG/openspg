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

package com.antgroup.openspg.server.core.schema.service.semantic.model;

import com.antgroup.openspg.server.common.model.base.BaseToString;
import lombok.Getter;
import lombok.Setter;

/** Dsl content verification results */
@Getter
@Setter
public class DslCheckResult extends BaseToString {

  private static final long serialVersionUID = -3009813729688112961L;

  /** Whether the dsl content verification passes */
  private boolean pass = true;

  /** The part of dsl content that fails the verification */
  private String errorPart;
}
