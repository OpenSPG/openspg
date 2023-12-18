package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;

public class IdEqualsPropertyNormalizerConfig extends BasePropertyNormalizerConfig {

  public IdEqualsPropertyNormalizerConfig() {
    super(PropertyNormalizerTypeEnum.ID_EQUALS);
  }
}
