package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;
import lombok.Getter;

@Getter
public abstract class BasePropertyMounterConfig {

  /** the type of property mounter */
  private final PropertyNormalizerTypeEnum type;

  protected BasePropertyMounterConfig(PropertyNormalizerTypeEnum type) {
    this.type = type;
  }
}
