/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.builder.core.strategy.predicting;

import com.antgroup.openspg.builder.core.strategy.predicting.impl.OperatorPredicting;
import com.antgroup.openspg.builder.model.pipeline.config.predicting.BasePredictingConfig;
import com.antgroup.openspg.builder.model.pipeline.config.predicting.OperatorPredictingConfig;
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
