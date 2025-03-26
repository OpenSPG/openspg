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

package com.antgroup.openspg.builder.core.physical.process;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.PythonNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.StringRecord;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class PythonProcessor extends BaseProcessor<PythonNodeConfig> {

  PemjaConfig config;

  public PythonProcessor(String id, String name, PythonNodeConfig config) {
    super(id, name, config);
    this.config = config.getPemjaConfig();
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    if (StringUtils.isBlank(config.getPythonPaths())) {
      config.setPythonPaths(context.getPythonPaths());
    }
    if (StringUtils.isBlank(config.getPythonExec())) {
      config.setPythonExec(context.getPythonExec());
    }
  }

  @Override
  public void close() throws Exception {}

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> outputs = Lists.newArrayList();
    for (BaseRecord record : inputs) {
      Map map = new ObjectMapper().convertValue(record, Map.class);
      List<Object> results = (List<Object>) PemjaUtils.invoke(config, map);
      results.forEach(result -> outputs.add(new StringRecord(JSON.toJSONString(result))));
    }
    return outputs;
  }
}
