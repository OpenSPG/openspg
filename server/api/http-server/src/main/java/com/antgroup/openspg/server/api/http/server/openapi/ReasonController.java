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

package com.antgroup.openspg.server.api.http.server.openapi;

import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.dto.service.request.ReasonerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.ThinkerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.ReasonerTaskResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.ThinkerTaskResponse;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.service.ReasonerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/public/v1/reason")
public class ReasonController extends BaseController {

  @Autowired private ReasonerManager reasonerManager;

  @RequestMapping(method = RequestMethod.POST, value = "/run")
  public ResponseEntity<Object> reason(@RequestBody ReasonerTaskRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ReasonerTaskResponse>() {
          @Override
          public void check() {}

          @Override
          public ReasonerTaskResponse action() {
            return reasonerManager.reason(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/thinker")
  public ResponseEntity<Object> reason(@RequestBody ThinkerTaskRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ThinkerTaskResponse>() {
          @Override
          public void check() {}

          @Override
          public ThinkerTaskResponse action() {
            return reasonerManager.thinker(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.GET, value = "/schema")
  public ResponseEntity<Object> getReasonSchema(@RequestParam Long projectId) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ProjectSchema>() {
          @Override
          public void check() {}

          @Override
          public ProjectSchema action() {
            return reasonerManager.getReasonSchema(projectId);
          }
        });
  }
}
