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

package com.antgroup.openspg.server.api.http.server.openapi;

import com.antgroup.openspg.biz.spgreasoner.ReasonerManager;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerDslRunRequest;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerJobInstQuery;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerJobSubmitRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.core.reasoner.model.service.BaseReasonerReceipt;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInst;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/public/v1/reasoner")
public class ReasonerController extends BaseController {

  @Autowired private ReasonerManager reasonerManager;

  @RequestMapping(value = "/runDsl", method = RequestMethod.POST)
  public ResponseEntity<Object> runDsl(@RequestBody ReasonerDslRunRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<BaseReasonerReceipt>() {
          @Override
          public void check() {}

          @Override
          public BaseReasonerReceipt action() {
            return reasonerManager.runDsl(request);
          }
        });
  }

  @RequestMapping(value = "/submitJobInfo", method = RequestMethod.POST)
  public ResponseEntity<Object> submitJobInfo(@RequestBody ReasonerJobSubmitRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<BaseReasonerReceipt>() {
          @Override
          public void check() {}

          @Override
          public BaseReasonerReceipt action() {
            return reasonerManager.submitJob(request);
          }
        });
  }

  @RequestMapping(value = "/queryJobInst", method = RequestMethod.GET)
  public ResponseEntity<Object> queryJobInst(@RequestParam(required = false) Long jobInstId) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<ReasonerJobInst>>() {
          @Override
          public void check() {}

          @Override
          public List<ReasonerJobInst> action() {
            ReasonerJobInstQuery query = new ReasonerJobInstQuery();
            query.setReasonerJobInstId(jobInstId);
            return reasonerManager.queryJobInst(query);
          }
        });
  }
}
