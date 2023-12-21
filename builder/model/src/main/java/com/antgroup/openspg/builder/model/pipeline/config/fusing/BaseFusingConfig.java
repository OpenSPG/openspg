package com.antgroup.openspg.builder.model.pipeline.config.fusing;

import com.antgroup.openspg.builder.model.pipeline.config.BaseOperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.FusingTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.OperatorTypeEnum;
import lombok.Getter;

@Getter
public abstract class BaseFusingConfig extends BaseOperatorConfig {

  private final FusingTypeEnum fusingType;

  public BaseFusingConfig(FusingTypeEnum fusingType) {
    super(OperatorTypeEnum.FUSING);
    this.fusingType = fusingType;
  }
}
