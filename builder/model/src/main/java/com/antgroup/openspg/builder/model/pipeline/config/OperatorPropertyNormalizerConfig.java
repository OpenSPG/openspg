package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;
import lombok.Getter;

@Getter
public class OperatorPropertyNormalizerConfig extends BasePropertyNormalizerConfig {

  private final OperatorConfig operatorConfig;

  public OperatorPropertyNormalizerConfig(OperatorConfig config) {
    super(PropertyNormalizerTypeEnum.OPERATOR);
    this.operatorConfig = config;
  }
}
