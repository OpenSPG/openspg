package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;
import java.util.Map;
import lombok.Getter;

@Getter
public class OperatorPropertyNormalizerConfig extends PropertyNormalizerConfig {

  private final OperatorConfig config;

  private final Map<String, String> params;

  public OperatorPropertyNormalizerConfig(OperatorConfig config, Map<String, String> params) {
    super(PropertyNormalizerTypeEnum.OPERATOR);
    this.config = config;
    this.params = params;
  }
}
