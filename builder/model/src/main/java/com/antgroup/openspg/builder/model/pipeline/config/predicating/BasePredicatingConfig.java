package com.antgroup.openspg.builder.model.pipeline.config.predicating;

import com.antgroup.openspg.builder.model.pipeline.config.BaseOperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.OperatorTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.PredicatingTypeEnum;
import lombok.Getter;

@Getter
public abstract class BasePredicatingConfig extends BaseOperatorConfig {

  private final PredicatingTypeEnum predicatingType;

  public BasePredicatingConfig(PredicatingTypeEnum predicatingType) {
    super(OperatorTypeEnum.PREDICATING);
    this.predicatingType = predicatingType;
  }
}
