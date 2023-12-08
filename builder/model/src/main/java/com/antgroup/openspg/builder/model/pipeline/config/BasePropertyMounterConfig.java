package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyMounterTypeEnum;
import lombok.Getter;

@Getter
public abstract class BasePropertyMounterConfig {

  /** the type of property mounter */
  private final PropertyMounterTypeEnum type;

  protected BasePropertyMounterConfig(PropertyMounterTypeEnum type) {
    this.type = type;
  }
}
