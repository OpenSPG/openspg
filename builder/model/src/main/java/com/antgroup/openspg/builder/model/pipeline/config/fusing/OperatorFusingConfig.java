package com.antgroup.openspg.builder.model.pipeline.config.fusing;

import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.FusingTypeEnum;
import lombok.Getter;

@Getter
public class OperatorFusingConfig extends BaseFusingConfig {

  private final OperatorConfig operatorConfig;

  public OperatorFusingConfig(OperatorConfig operatorConfig) {
    super(FusingTypeEnum.OPERATOR);
    this.operatorConfig = operatorConfig;
  }
}
