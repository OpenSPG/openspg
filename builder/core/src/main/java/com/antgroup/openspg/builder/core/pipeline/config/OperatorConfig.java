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

package com.antgroup.openspg.builder.core.pipeline.config;

import com.antgroup.openspg.server.schema.core.model.type.OperatorKey;
import com.antgroup.openspg.server.schema.core.model.type.OperatorTypeEnum;
import java.util.Map;
import lombok.Getter;

/** Operator related configuration, including name, version, operator address, entry class, etc. */
@Getter
public class OperatorConfig {

  /** The name of the operator. */
  private final String name;

  /** The version of the operator. */
  private final Integer version;

  /** The address where the operator is deployed. */
  private final String jarAddress;

  /** The entry class for executing the operator. */
  private final String mainClass;

  /**
   * Operator types, including linking operator, extraction operator, and standardization operator.
   */
  private final OperatorTypeEnum operatorType;

  /**
   * The extension parameters of the operator, mainly defined by the user and used internally within
   * the operator.
   */
  private final Map<String, String> params;

  public OperatorConfig(
      String name,
      Integer version,
      String jarAddress,
      String mainClass,
      OperatorTypeEnum operatorType,
      Map<String, String> params) {
    this.name = name;
    this.version = version;
    this.jarAddress = jarAddress;
    this.mainClass = mainClass;
    this.operatorType = operatorType;
    this.params = params;
  }

  public OperatorKey toKey() {
    return new OperatorKey(name, version);
  }
}
