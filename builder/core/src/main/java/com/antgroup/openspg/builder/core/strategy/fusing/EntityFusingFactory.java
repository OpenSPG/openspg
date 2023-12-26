package com.antgroup.openspg.builder.core.strategy.fusing;

import com.antgroup.openspg.builder.core.strategy.fusing.impl.NewInstanceFusing;
import com.antgroup.openspg.builder.core.strategy.fusing.impl.OperatorFusing;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.OperatorFusingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.FusingTypeEnum;

public class EntityFusingFactory {

  public static EntityFusing getEntityFusing(BaseFusingConfig config) {
    FusingTypeEnum fusingType =
        config == null ? FusingTypeEnum.NEW_INSTANCE : config.getFusingType();
    switch (fusingType) {
      case OPERATOR:
        return new OperatorFusing((OperatorFusingConfig) config);
      case NEW_INSTANCE:
        return NewInstanceFusing.INSTANCE;
      default:
        throw new IllegalArgumentException("illegal fusing type=" + fusingType);
    }
  }
}
