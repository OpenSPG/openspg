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

package com.antgroup.openspg.builder.runner.local.physical.source.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.StringSourceNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.StringRecord;
import com.antgroup.openspg.builder.runner.local.physical.source.BaseSourceReader;
import com.antgroup.openspg.common.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringSourceReader extends BaseSourceReader<StringSourceNodeConfig> {

  private ExecuteNode node;

  private String document;

  public StringSourceReader(String id, String name, StringSourceNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    if (context.getExecuteNodes() != null) {
      this.node = context.getExecuteNodes().get(getId());
      if (node != null) {
        node.setStatus(StatusEnum.RUNNING);
        node.addTraceLog("Start reading document...");
      }
    }
    this.document = config.getDocument();
  }

  @Override
  public void close() throws Exception {
    if (node != null) {
      node.setStatus(StatusEnum.FINISH);
    }
  }

  @Override
  public List<BaseRecord> read() {
    List<BaseRecord> results = new ArrayList<>();
    if (StringUtils.isBlank(document)) {
      node.setStatus(StatusEnum.FINISH);
      return results;
    }
    BaseRecord record = new StringRecord(document);
    results.add(record);
    document = null;
    if (node != null) {
      node.addTraceLog("Read document complete bytes:%s", config.getDocument().length());
      node.setStatus(StatusEnum.FINISH);
    }
    return results;
  }
}
