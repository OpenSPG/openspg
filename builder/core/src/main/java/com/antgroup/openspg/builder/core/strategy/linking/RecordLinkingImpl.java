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

package com.antgroup.openspg.builder.core.strategy.linking;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.impl.BasicPropertyLinking;
import com.antgroup.openspg.builder.core.strategy.linking.impl.IdEqualsLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.linking.BaseLinkingConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

public class RecordLinkingImpl implements RecordLinking {

  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final BasicPropertyLinking basicPropertyLinking;
  private final Map<String, PropertyLinking> semanticPropertyLinking;

  @Setter private PropertyLinking defaultPropertyLinking = IdEqualsLinking.INSTANCE;

  public RecordLinkingImpl(List<BaseMappingNodeConfig.MappingConfig> mappingConfigs) {
    this.mappingConfigs = mappingConfigs;
    this.basicPropertyLinking = new BasicPropertyLinking();
    this.semanticPropertyLinking = new HashMap<>(mappingConfigs.size());
  }

  public RecordLinkingImpl() {
    this(Collections.emptyList());
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    basicPropertyLinking.init(context);
    defaultPropertyLinking.init(context);
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }
    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      if (mappingConfig.getStrategyConfig() != null) {
        PropertyLinking propertyLinking =
            PropertyLinkingFactory.getPropertyLinking(
                (BaseLinkingConfig) mappingConfig.getStrategyConfig());
        propertyLinking.init(context);
        semanticPropertyLinking.put(mappingConfig.getTarget(), propertyLinking);
      }
    }
  }

  @Override
  public void linking(BaseSPGRecord spgRecord) throws LinkingException {
    for (BasePropertyRecord propertyRecord : spgRecord.getProperties()) {
      if (propertyRecord.isSemanticProperty()) {
        PropertyLinking propertyLinking = semanticPropertyLinking.get(propertyRecord.getName());
        if (propertyLinking != null) {
          // we use user-defined normalizer to normalize property value
          propertyLinking.linking(propertyRecord);
        } else {
          // we use default normalizer to normalize property value
          defaultPropertyLinking.linking(propertyRecord);
        }
      } else {
        // we use basic normalizer to normalize property value
        basicPropertyLinking.linking(propertyRecord);
      }
    }
  }
}
