package com.antgroup.openspg.builder.core.operator.predicating;

import com.antgroup.openspg.builder.core.operator.predicating.impl.OperatorPredicating;
import com.antgroup.openspg.builder.model.pipeline.config.predicating.BasePredicatingConfig;
import com.antgroup.openspg.builder.model.pipeline.config.predicating.OperatorPredicatingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.PredicatingTypeEnum;

public class PropertyPredicatingFactory {

  public static PropertyPredicating getPropertyPredicating(BasePredicatingConfig config) {
    PredicatingTypeEnum predicatingType = config.getPredicatingType();
    switch (predicatingType) {
      case OPERATOR:
        return new OperatorPredicating((OperatorPredicatingConfig) config);
      default:
        throw new IllegalArgumentException("illegal predicating type=" + predicatingType);
    }
  }
}
