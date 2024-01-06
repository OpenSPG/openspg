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
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.predicting.BasePredictingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyValue;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class RecordPredictingImpl implements RecordPredicting {

  private final List<SPGTypeMappingNodeConfig.MappingConfig> predicatingConfigs;
  private final Map<SPGTypeMappingNodeConfig.MappingConfig, PropertyPredicting>
      semanticPropertyPredicating;

  public RecordPredictingImpl(List<SPGTypeMappingNodeConfig.MappingConfig> predicatingConfigs) {
    this.predicatingConfigs = predicatingConfigs;
    this.semanticPropertyPredicating = new HashMap<>();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    if (CollectionUtils.isEmpty(predicatingConfigs)) {
      return;
    }

    for (SPGTypeMappingNodeConfig.MappingConfig predicatingConfig : predicatingConfigs) {
      PropertyPredicting propertyPredicating =
          PropertyPredictingFactory.getPropertyPredicating(
              (BasePredictingConfig) predicatingConfig.getStrategyConfig());
      propertyPredicating.init(context);
      semanticPropertyPredicating.put(predicatingConfig, propertyPredicating);
    }
  }

  @Override
  public void predicting(BaseAdvancedRecord advancedRecord) throws PredictingException {
    propertyPredicting(advancedRecord);
    relationPredicting(advancedRecord);
  }

  private void propertyPredicting(BaseAdvancedRecord advancedRecord) {
    Map<String, Property> propertyMap = advancedRecord.getSpgType().getPropertyMap();
    for (Map.Entry<SPGTypeMappingNodeConfig.MappingConfig, PropertyPredicting> entry :
        semanticPropertyPredicating.entrySet()) {
      SPGTypeMappingNodeConfig.MappingConfig mappingConfig = entry.getKey();
      PropertyPredicting predicting = entry.getValue();

      if (!mappingConfig.getMappingType().equals(SPGTypeMappingNodeConfig.MappingType.PROPERTY)) {
        continue;
      }

      Property property = propertyMap.get(mappingConfig.getFirstSplit());
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

  private void relationPredicting(BaseAdvancedRecord advancedRecord) {
    Map<String, Relation> relationMap =
        advancedRecord.getSpgType().getRelations().stream()
            .collect(
                Collectors.toMap(
                    x -> String.format("%s#%s", x.getName(), x.getObjectTypeRef().getName()),
                    Function.identity()));
    for (Map.Entry<SPGTypeMappingNodeConfig.MappingConfig, PropertyPredicting> entry :
        semanticPropertyPredicating.entrySet()) {
      SPGTypeMappingNodeConfig.MappingConfig mappingConfig = entry.getKey();
      PropertyPredicting predicting = entry.getValue();

      if (!mappingConfig.getMappingType().equals(SPGTypeMappingNodeConfig.MappingType.RELATION)) {
        continue;
      }

      String first2Split = mappingConfig.getFirst2Split();
      Relation relation = relationMap.get(first2Split);
      if (relation == null) {
        continue;
      }

      List<BaseAdvancedRecord> predictedRecords = predicting.predicting(advancedRecord);
      if (CollectionUtils.isEmpty(predictedRecords)) {
        continue;
      }

      for (BaseAdvancedRecord predictedRecord : predictedRecords) {

        RelationRecord relationRecord =
            new RelationRecord(relation, null, predictedRecord.getId(), Collections.emptyList());
        advancedRecord.addRelationRecord(relationRecord);
      }
    }
  }
}
