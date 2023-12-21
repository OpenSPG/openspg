package com.antgroup.openspg.builder.model.pipeline.config.predicating;

import com.antgroup.openspg.builder.model.pipeline.config.BaseStrategyConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StrategyTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.PredictingTypeEnum;
import lombok.Getter;

@Getter
public abstract class BasePredictingConfig extends BaseStrategyConfig {

  private final PredictingTypeEnum predictingType;

  public BasePredictingConfig(PredictingTypeEnum predictingType) {
    super(StrategyTypeEnum.PREDICTING);
    this.predictingType = predictingType;
  }
}
