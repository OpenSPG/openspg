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

import com.antgroup.openspg.builder.model.pipeline.config.predicting.BasePredictingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class BaseMappingNodeConfig extends BaseNodeConfig {

  public BaseMappingNodeConfig(NodeTypeEnum type) {
    super(type);
  }

  @Getter
  @AllArgsConstructor
  public static class MappingFilter {
    private final String columnName;
    private final String columnValue;
  }

  @Getter
  @AllArgsConstructor
  public static class MappingConfig {
    private final String source;
    private final String target;
    private final BaseStrategyConfig strategyConfig;
  }

  @Getter
  @AllArgsConstructor
  public static class PredictingConfig {
    private final String target;
    private final BasePredictingConfig predictingConfig;
  }
}
