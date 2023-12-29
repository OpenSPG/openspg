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

package com.antgroup.openspg.builder.core.strategy.fusing.impl;

import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonRecordConvertor;
import com.antgroup.openspg.builder.core.physical.operator.protocol.InvokeResultWrapper;
import com.antgroup.openspg.builder.core.physical.operator.protocol.PythonRecord;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.fusing.EntityFusing;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.OperatorFusingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
@SuppressWarnings("unchecked")
public class OperatorFusing implements EntityFusing {

  private BuilderContext context;
  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorFusingConfig fusingConfig;
  private final OperatorFactory operatorFactory;
  private final RecordLinking recordLinking;

  public OperatorFusing(OperatorFusingConfig fusingConfig) {
    this.fusingConfig = fusingConfig;
    this.operatorFactory = PythonOperatorFactory.getInstance();
    this.recordLinking = new RecordLinkingImpl();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    this.context = context;

    recordLinking.init(context);

    operatorFactory.init(context);
    operatorFactory.loadOperator(fusingConfig.getOperatorConfig());
  }

  @Override
  public List<BaseAdvancedRecord> fusing(List<BaseAdvancedRecord> records) throws FusingException {
    List<Map<String, Object>> pythonRecords =
        CollectionsUtils.listMap(records, r -> PythonRecordConvertor.toPythonRecord(r).toMap());
    InvokeResultWrapper<List<PythonRecord>> invokeResultWrapper = null;
    try {
      Map<String, Object> result =
          (Map<String, Object>)
              operatorFactory.invoke(fusingConfig.getOperatorConfig(), pythonRecords);

      invokeResultWrapper =
          mapper.convertValue(
              result, new TypeReference<InvokeResultWrapper<List<PythonRecord>>>() {});
    } catch (Exception e) {
      throw new FusingException(e, "fusing error");
    }

    if (invokeResultWrapper == null || CollectionUtils.isEmpty(invokeResultWrapper.getData())) {
      return Collections.emptyList();
    }
    return CollectionsUtils.listMap(
        invokeResultWrapper.getData(),
        r -> PythonRecordConvertor.toAdvancedRecord(r, recordLinking, context.getCatalog()));
  }
}
