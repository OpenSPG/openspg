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

package com.antgroup.openspg.builder.core.compiler.physical.invoker.impl;

import com.antgroup.openspg.builder.core.compiler.physical.invoker.InvokerFactory;
import com.antgroup.openspg.builder.core.compiler.physical.invoker.InvokerParam;
import com.antgroup.openspg.builder.core.compiler.physical.invoker.concept.ConceptPredicateInvoker;
import com.antgroup.openspg.builder.core.compiler.physical.invoker.concept.impl.ConceptPredicateInvokerImpl;
import com.antgroup.openspg.builder.core.compiler.physical.invoker.operator.OperatorInvoker;
import com.antgroup.openspg.builder.core.compiler.physical.invoker.operator.impl.OperatorInvokerImpl;
import com.antgroup.openspg.builder.core.compiler.physical.process.BaseProcessor;
import com.antgroup.openspg.builder.core.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.core.runtime.BuilderRecordException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.runtime.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.core.runtime.record.BasePropertyRecord;
import com.antgroup.openspg.builder.core.runtime.record.BaseRecord;
import com.antgroup.openspg.builder.core.runtime.record.BaseSPGRecord;
import java.util.ArrayList;
import java.util.List;

public class InvokerFactoryImpl implements InvokerFactory {

  private OperatorInvoker operatorInvoker;
  private ConceptPredicateInvoker predicateInvoker;

  @Override
  public void init(RuntimeContext context) {
    operatorInvoker = new OperatorInvokerImpl();
    operatorInvoker.init(context);

    predicateInvoker = new ConceptPredicateInvokerImpl();
    predicateInvoker.init(context);
  }

  @Override
  public void register(OperatorConfig operatorConfig) {
    operatorInvoker.register(operatorConfig);
  }

  @Override
  public List<BaseRecord> invoke(InvokerParam param) {
    List<BaseRecord> resultRecords = new ArrayList<>();
    BaseSPGRecord baseSpgRecord = param.getBaseSpgRecord();

    if (baseSpgRecord instanceof BaseAdvancedRecord) {
      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) baseSpgRecord;
      List<BaseSPGRecord> predicatedRecords = null;
      try {
        // 触发belongTo/leadTo等谓词计算
        predicatedRecords = predicateInvoker.invoke(advancedRecord);
      } catch (Throwable e) {
        throw new BuilderRecordException(
            param.getProcessor(), e, "call predicate error, errorMsg={}", e.getMessage());
      }

      try {
        for (BaseSPGRecord predicateRecord : predicatedRecords) {
          if (predicateRecord instanceof BaseAdvancedRecord) {
            BaseAdvancedRecord predicatedAdvancedRecord = (BaseAdvancedRecord) predicateRecord;
            // 触发属性标化/实体链指等算子计算
            BaseAdvancedRecord operatorRecord =
                operatorInvoker.invoke(predicatedAdvancedRecord, param.getProperty2Operator());
            resultRecords.add(operatorRecord);
          } else {
            resultRecords.add(predicateRecord);
          }
        }
      } catch (Throwable e) {
        throw new BuilderRecordException(
            param.getProcessor(), e, "call operator error, errorMsg={}", e.getMessage());
      }
    } else {
      resultRecords.add(baseSpgRecord);
    }
    resultRecords.forEach(r -> setStdAndIdsValue(param.getProcessor(), r));
    return resultRecords;
  }

  private void setStdAndIdsValue(BaseProcessor<?> processor, BaseRecord baseRecord) {
    if (!(baseRecord instanceof BaseSPGRecord)) {
      return;
    }

    BaseSPGRecord baseSpgRecord = (BaseSPGRecord) baseRecord;
    for (BasePropertyRecord propertyRecord : baseSpgRecord.getProperties()) {
      // 统一设置std&ids
      try {
        propertyRecord.setStdValue();
      } catch (NumberFormatException e) {
        throw new BuilderRecordException(
            processor,
            e,
            "the type of property:{} is {}, but {} found",
            propertyRecord.getName(),
            propertyRecord.getObjectTypeRef().getName(),
            propertyRecord.getValue().getStdOrRawValue());
      }
      propertyRecord.setIdsValue();
    }
  }
}
