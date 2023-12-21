package com.antgroup.openspg.builder.core.operator.predicating;

import com.antgroup.openspg.builder.core.operator.predicating.impl.OperatorPredicting;
import com.antgroup.openspg.builder.model.pipeline.config.predicating.BasePredictingConfig;
import com.antgroup.openspg.builder.model.pipeline.config.predicating.OperatorPredictingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.PredictingTypeEnum;

public class PropertyPredictingFactory {

  public static PropertyPredicting getPropertyPredicating(BasePredictingConfig config) {
    PredictingTypeEnum predicatingType = config.getPredictingType();
    switch (predicatingType) {
      case OPERATOR:
        return new OperatorPredicting((OperatorPredictingConfig) config);
      default:
        throw new IllegalArgumentException("illegal predicating type=" + predicatingType);
    }
  }
}
