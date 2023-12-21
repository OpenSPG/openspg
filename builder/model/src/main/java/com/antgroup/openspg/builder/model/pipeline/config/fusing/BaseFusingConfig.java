package com.antgroup.openspg.builder.model.pipeline.config.fusing;

import com.antgroup.openspg.builder.model.pipeline.config.BaseStrategyConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.FusingTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.StrategyTypeEnum;
import lombok.Getter;

@Getter
public abstract class BaseFusingConfig extends BaseStrategyConfig {

  private final FusingTypeEnum fusingType;

  public BaseFusingConfig(FusingTypeEnum fusingType) {
    super(StrategyTypeEnum.FUSING);
    this.fusingType = fusingType;
  }
}
