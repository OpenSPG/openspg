package com.antgroup.openspg.builder.model.pipeline.config.predicating;

import com.antgroup.openspg.builder.model.pipeline.config.BaseOperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.OperatorTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.PredictingTypeEnum;
import lombok.Getter;

@Getter
public abstract class BasePredictingConfig extends BaseOperatorConfig {

  private final PredictingTypeEnum predictingType;

  public BasePredictingConfig(PredictingTypeEnum predictingType) {
    super(OperatorTypeEnum.PREDICTING);
    this.predictingType = predictingType;
  }
}
