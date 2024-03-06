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

package com.antgroup.openspg.server.biz.service.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGDataQueryService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.BatchVertexLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import com.antgroup.openspg.server.biz.service.QueryManager;
import com.antgroup.openspg.server.biz.service.convertor.InstanceConvertor;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryManagerImpl implements QueryManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public List<SPGTypeInstance> query(SPGTypeQueryRequest query) {
    if (CollectionUtils.isEmpty(query.getIds()) || StringUtils.isBlank(query.getSpgType())) {
      return Collections.emptyList();
    }
    LPGDataQueryService graphStoreClient = getGraphStoreClient();
    GraphLPGRecordStruct results =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new BatchVertexLPGRecordQuery(query.getIds(), query.getSpgType()));

    return CollectionsUtils.listMap(results.getVertices(), InstanceConvertor::toInstance);
  }

  private LPGDataQueryService getGraphStoreClient() {
    return (LPGDataQueryService)
        GraphStoreClientDriverManager.getClient(appEnvConfig.getGraphStoreUrl());
  }
}
