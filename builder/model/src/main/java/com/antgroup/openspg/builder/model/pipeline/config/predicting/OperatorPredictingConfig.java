package com.antgroup.openspg.builder.model.pipeline.config.predicting;

import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.PredictingTypeEnum;
import lombok.Getter;

@Getter
public class OperatorPredictingConfig extends BasePredictingConfig {

  private final OperatorConfig operatorConfig;

  public OperatorPredictingConfig(OperatorConfig operatorConfig) {
    super(PredictingTypeEnum.OPERATOR);
    this.operatorConfig = operatorConfig;
  }
}
