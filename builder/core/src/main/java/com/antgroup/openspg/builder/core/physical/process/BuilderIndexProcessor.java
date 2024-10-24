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

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.BuilderIndexNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.cloudext.interfaces.cache.CacheClient;
import com.antgroup.openspg.cloudext.interfaces.cache.CacheClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxRecordManipulateCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecordAlterItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class BuilderIndexProcessor extends BaseProcessor<BuilderIndexNodeConfig> {

  private ExecuteNode node;
  private SearchEngineClient searchEngineClient;
  private CacheClient cacheClient;

  public BuilderIndexProcessor(String id, String name, BuilderIndexNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    searchEngineClient = SearchEngineClientDriverManager.getClient(context.getSearchEngineUrl());
    cacheClient = CacheClientDriverManager.getClient(context.getCacheUrl());
    this.node = context.getExecuteNodes().get(getId());
  }

  @Override
  public void close() throws Exception {}

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    node.setStatus(StatusEnum.RUNNING);
    node.addTraceLog("Start builder index...");
    for (BaseRecord record : inputs) {
      ChunkRecord.Chunk chunk = ((ChunkRecord) record).getChunk();
      node.addTraceLog("Start index write id:%s", chunk.getId());
      cacheClient.putObject(chunk.getId(), chunk.getContent());
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> properties = mapper.convertValue(chunk, Map.class);
      IdxRecordManipulateCmd cmd =
          new IdxRecordManipulateCmd(
              Lists.newArrayList(
                  new IdxRecordAlterItem(
                      RecordAlterOperationEnum.UPSERT,
                      new IdxRecord("Index._Chunk", chunk.getId(), 0.0, properties))));
      searchEngineClient.manipulateRecord(cmd);
      node.addTraceLog("Index write successful");
    }
    node.addTraceLog("Builder index complete");
    node.setStatus(StatusEnum.FINISH);
    return inputs;
  }
}
