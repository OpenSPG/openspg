/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

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
