package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import java.util.List;
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
    private final BasePropertyNormalizerConfig normalizerConfig;
  }

  public abstract List<BaseSPGIdentifier> getIdentifiers();

  public List<MappingConfig> getMappingConfigs() {
    throw new UnsupportedOperationException();
  }
}
