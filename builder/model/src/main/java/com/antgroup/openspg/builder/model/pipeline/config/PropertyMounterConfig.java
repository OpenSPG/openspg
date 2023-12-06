package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyMounterTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.Getter;

@Getter
public abstract class PropertyMounterConfig extends BaseValObj {

  private final PropertyMounterTypeEnum mounterType;

  public PropertyMounterConfig(PropertyMounterTypeEnum mounterType) {
    this.mounterType = mounterType;
  }
}
