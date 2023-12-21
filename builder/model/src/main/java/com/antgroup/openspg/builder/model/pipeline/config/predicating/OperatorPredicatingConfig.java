package com.antgroup.openspg.builder.model.pipeline.config.predicating;

import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.PredicatingTypeEnum;
import lombok.Getter;

@Getter
public class OperatorPredicatingConfig extends BasePredicatingConfig {

  private final OperatorConfig operatorConfig;

  public OperatorPredicatingConfig(OperatorConfig operatorConfig) {
    super(PredicatingTypeEnum.OPERATOR);
    this.operatorConfig = operatorConfig;
  }
}
