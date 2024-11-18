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

package com.antgroup.openspg.builder.core.strategy.linking;

import com.antgroup.openspg.builder.core.strategy.linking.impl.IdEqualsLinking;
import com.antgroup.openspg.builder.core.strategy.linking.impl.OperatorLinking;
import com.antgroup.openspg.builder.model.pipeline.config.linking.BaseLinkingConfig;
import com.antgroup.openspg.builder.model.pipeline.config.linking.OperatorLinkingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.LinkingTypeEnum;

public class PropertyLinkingFactory {

  public static PropertyLinking getPropertyLinking(BaseLinkingConfig config) {
    LinkingTypeEnum linkingType = config.getLinkingType();
    switch (linkingType) {
      case OPERATOR:
        return new OperatorLinking((OperatorLinkingConfig) config);
      case ID_EQUALS:
        return IdEqualsLinking.INSTANCE;
      default:
        throw new IllegalArgumentException("illegal linking type=" + linkingType);
    }
  }
}
