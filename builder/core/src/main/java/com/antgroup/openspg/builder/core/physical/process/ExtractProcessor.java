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

package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.core.physical.invoker.operator.OperatorInvoker;
import com.antgroup.openspg.builder.core.physical.invoker.operator.impl.OperatorInvokerImpl;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.ExtractNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Knowledge extraction processor is used to extract unstructured text into structured information
 * can be achieved through extraction services or extraction rules.
 *
 * <p>Extraction services are often built based on machine learning and natural language processing
 * techniques. They automatically analyze text and extract key information. These services may
 * include entity recognition, relationship extraction, event extraction, and more. They leverage
 * contextual and semantic understanding to extract information from the text.
 *
 * <p>Extraction rules, on the other hand, rely on predefined rules and patterns to extract
 * information. By defining specific rules and patterns, it becomes possible to match certain
 * patterns in the text and extract the desired information. These rules can be designed based on
 * regular expressions, keyword matching, grammar rules, and other techniques.
 *
 * <p>Both extraction services and extraction rules aim to convert unstructured text into structured
 * information, enabling computers to understand and process the important content and relationships
 * within the text. This helps in organizing, searching, and analyzing textual data more
 * effectively, extracting valuable insights and information from it.
 */
@Slf4j
public class ExtractProcessor extends BaseProcessor<ExtractNodeConfig> {

  private OperatorInvoker operatorInvoker;

  public ExtractProcessor(String id, String name, ExtractNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    this.operatorInvoker = new OperatorInvokerImpl();
    this.operatorInvoker.init(context);
    this.operatorInvoker.register(config.getOperatorConfig());
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> records) {
    List<BaseRecord> resultRecords = new ArrayList<>(records.size());
    for (BaseRecord record : records) {
      List<BuilderRecord> invokeResults =
          operatorInvoker.invoke((BuilderRecord) record, config.getOperatorConfig());
      resultRecords.addAll(invokeResults);
    }
    return resultRecords;
  }

  @Override
  public void close() throws Exception {}
}
