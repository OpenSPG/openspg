package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;
import lombok.Getter;

@Getter
public class OperatorPropertyNormalizerConfig extends BasePropertyNormalizerConfig {

  private final OperatorConfig config;

  public OperatorPropertyNormalizerConfig(OperatorConfig config) {
    super(PropertyNormalizerTypeEnum.OPERATOR);
    this.config = config;
  }
}
