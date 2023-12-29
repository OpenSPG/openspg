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

import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import java.util.List;
import lombok.Getter;

@Getter
public class SubGraphMappingNodeConfig extends BaseMappingNodeConfig {

  private final String spgType;

  private final List<MappingFilter> mappingFilters;

  private final List<MappingConfig> mappingConfigs;

  private final BaseFusingConfig subjectFusingConfig;

  private final List<PredictingConfig> predictingConfigs;

  public SubGraphMappingNodeConfig(
      String spgType,
      List<MappingFilter> mappingFilters,
      List<MappingConfig> mappingConfigs,
      BaseFusingConfig subjectFusingConfig,
      List<PredictingConfig> predictingConfigs) {
    super(NodeTypeEnum.SUBGRAPH_MAPPING);
    this.spgType = spgType;
    this.mappingFilters = mappingFilters;
    this.mappingConfigs = mappingConfigs;
    this.subjectFusingConfig = subjectFusingConfig;
    this.predictingConfigs = predictingConfigs;
  }
}
