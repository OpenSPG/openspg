package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.Getter;

@Getter
public abstract class BasePropertyNormalizerConfig extends BaseValObj {

  private final PropertyNormalizerTypeEnum normalizerType;

  public BasePropertyNormalizerConfig(PropertyNormalizerTypeEnum normalizerType) {
    this.normalizerType = normalizerType;
  }
}
