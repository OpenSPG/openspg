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

import com.antgroup.kg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.dto.service.request.ReasonerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.ThinkerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.ReasonerTaskResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.ThinkerTaskResponse;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.service.ReasonerManager;
import com.antgroup.openspg.server.common.model.reasoner.ReasonerTask;
import com.antgroup.openspg.server.common.model.reasoner.ThinkerTask;
import com.antgroup.openspg.server.core.reasoner.service.CatalogService;
import com.antgroup.openspg.server.core.reasoner.service.ReasonerService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReasonerManagerImpl implements ReasonerManager {
  @Autowired private ProjectManager projectManager;
  @Autowired private ReasonerService reasonerService;
  @Autowired private CatalogService catalogService;

  @Override
  public ThinkerTaskResponse thinker(ThinkerTaskRequest request) {
    ThinkerTask task = new ThinkerTask();
    task.setTaskId(UUID.randomUUID().toString());
    task.setMode(request.getMode());
    task.setObject(request.getObject());
    task.setPredicate(request.getPredicate());
    task.setSubject(request.getSubject());
    task.setProjectId(request.getProjectId());
    task.setParams(request.getParams());
    String graphStoreUrl = getGraphStoreUrl(request.getProjectId());
    task.setGraphStoreUrl(graphStoreUrl);
    List<Result> res =  reasonerService.thinker(task);
    ThinkerTaskResponse response = new ThinkerTaskResponse();
    response.setProjectId(request.getProjectId());
    response.setTaskId(task.getTaskId());
    response.setResult(Collections.singletonList(res));
    return response;
  }

  @Override
  public ReasonerTaskResponse reason(ReasonerTaskRequest request) {
    String graphStoreUrl = getGraphStoreUrl(request.getProjectId());
    ReasonerTask reasonerTask = new ReasonerTask();
    if (request.getTaskId() == null) {
      request.setTaskId(UUID.randomUUID().toString());
    }
    reasonerTask.setTaskId(request.getTaskId());
    reasonerTask.setDsl(request.getDsl());
    reasonerTask.setParams(request.getParams());
    reasonerTask.setProjectId(request.getProjectId());
    reasonerTask.setGraphStoreUrl(graphStoreUrl);
    ReasonerTask ret = reasonerService.runTask(reasonerTask);
    ReasonerTaskResponse reasonerTaskResponse = new ReasonerTaskResponse();
    reasonerTaskResponse.setProjectId(request.getProjectId());
    reasonerTaskResponse.setTask(ret);
    return reasonerTaskResponse;
  }

  @Override
  public ProjectSchema getReasonSchema(Long projectId) {
    return catalogService.getSchemaInfo(projectId, getGraphStoreUrl(projectId));
  }

  protected String getGraphStoreUrl(Long projectId) {
    return projectManager.getGraphStoreUrl(projectId);
  }
}
