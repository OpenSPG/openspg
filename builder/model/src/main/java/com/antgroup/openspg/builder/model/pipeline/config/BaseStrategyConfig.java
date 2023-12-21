package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.StrategyTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.Getter;

@Getter
public abstract class BaseStrategyConfig extends BaseValObj {

  private final StrategyTypeEnum strategyType;

  public BaseStrategyConfig(StrategyTypeEnum strategyType) {
    this.strategyType = strategyType;
  }
}
