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

package com.antgroup.openspg.builder.core.strategy.predicting;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredictingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyValue;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class RecordPredictingImpl implements RecordPredicting {

  private final List<BaseMappingNodeConfig.PredictingConfig> predicatingConfigs;
  private final Map<String, PropertyPredicting> semanticPropertyPredicating;

  public RecordPredictingImpl(List<BaseMappingNodeConfig.PredictingConfig> predicatingConfigs) {
    this.predicatingConfigs = predicatingConfigs;
    this.semanticPropertyPredicating = new HashMap<>();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    if (CollectionUtils.isEmpty(predicatingConfigs)) {
      return;
    }

    for (BaseMappingNodeConfig.PredictingConfig predicatingConfig : predicatingConfigs) {
      PropertyPredicting propertyPredicating =
          PropertyPredictingFactory.getPropertyPredicating(predicatingConfig.getPredictingConfig());
      propertyPredicating.init(context);
      semanticPropertyPredicating.put(predicatingConfig.getTarget(), propertyPredicating);
    }
  }

  @Override
  public void predicting(BaseAdvancedRecord advancedRecord) throws PredictingException {
    Map<String, Property> propertyMap = advancedRecord.getSpgType().getPropertyMap();
    for (Map.Entry<String, PropertyPredicting> entry : semanticPropertyPredicating.entrySet()) {
      String propertyKey = entry.getKey();
      PropertyPredicting predicting = entry.getValue();

      Property property = propertyMap.get(propertyKey);
      if (property == null) {
        continue;
      }

      List<BaseAdvancedRecord> predictedRecords = predicting.predicting(advancedRecord);
      if (CollectionUtils.isEmpty(predictedRecords)) {
        continue;
      }

      SPGPropertyValue value = null;
      if (property.isMultiValue()) {
        List<String> bizIds =
            predictedRecords.stream()
                .map(BaseAdvancedRecord::getId)
                .distinct()
                .collect(Collectors.toList());

        value = new SPGPropertyValue(String.join(",", bizIds));
        value.setStrStds(bizIds);
        value.setIds(bizIds);
      } else {
        BaseAdvancedRecord firstPredictedRecord = predictedRecords.get(0);

        value = new SPGPropertyValue(firstPredictedRecord.getId());
        value.setSingleStd(firstPredictedRecord.getId());
        value.setSingleId(firstPredictedRecord.getId());
      }
      advancedRecord.addSpgProperties(new SPGPropertyRecord(property, value));
    }
  }
}
