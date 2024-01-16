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

package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
public class SPGTypeMappingNodeConfig extends BaseNodeConfig {

  @Getter
  @AllArgsConstructor
  public static class MappingFilter {
    private final String columnName;
    private final String columnValue;
  }

  public enum MappingType {
    PROPERTY,
    RELATION,
    SUB_PROPERTY,
    SUB_RELATION,
    ;
  }

  @Getter
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class MappingConfig {
    private final String source;
    private final String target;
    @EqualsAndHashCode.Exclude private final BaseStrategyConfig strategyConfig;
    private final MappingType mappingType;

    public String getFirstSplit() {
      return target.split("#")[0];
    }

    public String getFirst2Split() {
      String[] splits = target.split("#");
      return String.format("%s#%s", splits[0], splits[1]);
    }

    public boolean isPropertyLinking() {
      return mappingType.equals(MappingType.PROPERTY) && source != null;
    }

    public boolean isPropertyPredicting() {
      return mappingType.equals(MappingType.PROPERTY) && source == null;
    }

    public boolean isRelationLinking() {
      return mappingType.equals(MappingType.RELATION) && source != null;
    }

    public boolean isRelationPredicting() {
      return mappingType.equals(MappingType.RELATION) && source == null;
    }

    public boolean isSubRelationLinking() {
      return mappingType.equals(MappingType.SUB_RELATION) && source != null;
    }
  }

  private final String spgType;

  private final List<MappingFilter> mappingFilters;

  private final List<MappingConfig> mappingConfigs;

  private final BaseFusingConfig subjectFusingConfig;

  public SPGTypeMappingNodeConfig(
      String spgType,
      List<MappingFilter> mappingFilters,
      List<MappingConfig> mappingConfigs,
      BaseFusingConfig subjectFusingConfig) {
    super(NodeTypeEnum.SPG_TYPE_MAPPING);
    this.spgType = spgType;
    this.mappingFilters = mappingFilters;
    this.mappingConfigs = mappingConfigs;
    this.subjectFusingConfig = subjectFusingConfig;
  }

  public List<MappingConfig> getPropertyLinkingConfigs() {
    return mappingConfigs.stream()
        .filter(MappingConfig::isPropertyLinking)
        .collect(Collectors.toList());
  }

  public List<MappingConfig> getRelationLinkingConfigs() {
    return mappingConfigs.stream()
        .filter(MappingConfig::isRelationLinking)
        .collect(Collectors.toList());
  }

  public List<MappingConfig> getPropertyPredictingConfigs() {
    return mappingConfigs.stream()
        .filter(MappingConfig::isPropertyPredicting)
        .collect(Collectors.toList());
  }

  public List<MappingConfig> getRelationPredictingConfigs() {
    return mappingConfigs.stream()
        .filter(MappingConfig::isRelationPredicting)
        .collect(Collectors.toList());
  }

  public List<MappingConfig> getSubRelationLinkingConfigs() {
    return mappingConfigs.stream()
        .filter(MappingConfig::isSubRelationLinking)
        .collect(Collectors.toList());
  }
}
