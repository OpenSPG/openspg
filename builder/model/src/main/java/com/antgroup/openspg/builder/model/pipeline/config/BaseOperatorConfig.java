package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.OperatorTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.Getter;

@Getter
public abstract class BaseOperatorConfig extends BaseValObj {

  private final OperatorTypeEnum operatorType;

  public BaseOperatorConfig(OperatorTypeEnum operatorType) {
    this.operatorType = operatorType;
  }
}
