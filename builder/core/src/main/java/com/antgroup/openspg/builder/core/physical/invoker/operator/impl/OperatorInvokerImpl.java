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

package com.antgroup.openspg.builder.core.physical.invoker.operator.impl;

import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.core.physical.invoker.operator.OperatorInvoker;
import com.antgroup.openspg.builder.core.physical.invoker.operator.protocol.EvalResult;
import com.antgroup.openspg.builder.core.physical.invoker.operator.protocol.Vertex;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
@SuppressWarnings("unchecked")
public class OperatorInvokerImpl implements OperatorInvoker {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorFactory operatorFactory;

  public OperatorInvokerImpl() {
    operatorFactory = new OperatorFactoryImpl();
  }

  @Override
  public void init(RuntimeContext context) {
    operatorFactory.init(context);
  }

  @Override
  public void register(OperatorConfig config) {
    operatorFactory.register(config);
  }

  @Override
  public List<BuilderRecord> invoke(BuilderRecord builderRecord, OperatorConfig operatorConfig) {
    List<BuilderRecord> resultRecords = new ArrayList<>(3);

    OperatorKey operatorKey = operatorConfig.toKey();
    EvalResult<List<Vertex>> evalResult;
    Vertex inputVertex = new Vertex().setProps(builderRecord.getProps());
    Map<String, Object> result =
        (Map<String, Object>) operatorFactory.invoke(operatorKey, inputVertex.toMap());
    evalResult = mapper.convertValue(result, new TypeReference<EvalResult<List<Vertex>>>() {});

    if (evalResult == null || CollectionUtils.isEmpty(evalResult.getData())) {
      return Collections.emptyList();
    }

    for (Vertex vertex : evalResult.getData()) {
      resultRecords.add(new BuilderRecord(builderRecord.getRecordId(), vertex.getProps()));
    }
    return resultRecords;
  }

  @Override
  public BaseAdvancedRecord invoke(
      BaseAdvancedRecord baseSpgRecord, Map<String, OperatorConfig> operatorConfigs) {
    Map<String, String> propertyMap = baseSpgRecord.getRawPropertyValueMap();
    for (SPGPropertyRecord propertyRecord : baseSpgRecord.getSpgProperties()) {
      OperatorConfig operatorConfig = operatorConfigs.get(propertyRecord.getName());

      if (operatorConfig != null
          && !SPGRecordTypeEnum.RELATION.equals(baseSpgRecord.getRecordType())) {
        // If an operator is configured on an attribute, the operator will be executed in the
        // future.
        OperatorKey operatorKey = operatorConfig.toKey();
        Vertex vertex = new Vertex().setProps(propertyMap);

        List<String> rawValues = rawValues(propertyRecord);
        switch (operatorConfig.getOperatorType()) {
          case ENTITY_LINK:
            List<String> bizIds = new ArrayList<>(rawValues.size());
            for (String rawValue : rawValues) {
              Map<String, Object> result =
                  (Map<String, Object>)
                      operatorFactory.invoke(operatorKey, rawValue, vertex.toMap());
              EvalResult<List<Vertex>> evalResult1 =
                  mapper.convertValue(result, new TypeReference<EvalResult<List<Vertex>>>() {});

              if (evalResult1 == null || CollectionUtils.isEmpty(evalResult1.getData())) {
                continue;
              }
              evalResult1.getData().stream()
                  .map(Vertex::getBizId)
                  .filter(Objects::nonNull)
                  .forEach(bizIds::add);
            }
            if (CollectionUtils.isNotEmpty(bizIds)) {
              //  If the linking is successful, set the ids of the target entities to the property.
              propertyRecord.getValue().setIds(String.join(",", bizIds));
            }
            break;
          case PROPERTY_NORMALIZE:
            List<String> stdValues = new ArrayList<>(rawValues.size());
            for (String rawValue : rawValues) {
              Map<String, Object> stdValue =
                  (Map<String, Object>)
                      operatorFactory.invoke(operatorKey, rawValue, vertex.toMap());
              EvalResult<String> evalResult2 =
                  mapper.convertValue(stdValue, new TypeReference<EvalResult<String>>() {});

              if (evalResult2 != null && StringUtils.isNotBlank(evalResult2.getData())) {
                stdValues.add(evalResult2.getData());
              }
            }
            if (CollectionUtils.isEmpty(stdValues)) {
              // Set the standardized value to the property.
              propertyRecord.getValue().setStd(String.join(",", stdValues));
            }
            break;
          default:
            throw new IllegalArgumentException(
                "illegal operatorType=" + operatorConfig.getOperatorType());
        }
      }
    }
    return baseSpgRecord;
  }

  private List<String> rawValues(SPGPropertyRecord propertyRecord) {
    List<String> rawValues = null;
    Constraint constraint = propertyRecord.getProperty().getConstraint();
    String rawValue = propertyRecord.getValue().getRaw();
    if (constraint != null
        && constraint.contains(ConstraintTypeEnum.MULTI_VALUE)
        && rawValue != null) {
      rawValues = Arrays.stream(rawValue.split(",")).collect(Collectors.toList());
    } else {
      rawValues = new ArrayList<>(1);
      rawValues.add(rawValue);
    }
    return rawValues;
  }
}
