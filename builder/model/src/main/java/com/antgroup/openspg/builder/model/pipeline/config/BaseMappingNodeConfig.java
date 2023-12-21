package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.config.predicating.BasePredicatingConfig;
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
    private final BaseOperatorConfig operatorConfig;
  }

  @Getter
  @AllArgsConstructor
  public static class PredicatingConfig {
    private final String target;
    private final BasePredicatingConfig predicatingConfig;
  }
}
