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

package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.NodeTypeEnum;

import java.util.List;

public class ExtractNodeConfig extends BaseNodeConfig {

  /** Columns of the node config. */
  private final List<String> outputFields;

  /** OperatorConfig of the node. */
  private final OperatorConfig operatorConfig;

  public ExtractNodeConfig(List<String> columns, OperatorConfig operatorConfig) {
    super(NodeTypeEnum.EXTRACT);
    this.outputFields = columns;
    this.operatorConfig = operatorConfig;
  }

  public List<String> getOutputFields() {
    return outputFields;
  }

  public OperatorConfig getOperatorConfig() {
    return operatorConfig;
  }
}
