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

import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptLevelInstanceRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptInstanceResponse;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptLevelInstanceResponse;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.biz.schema.ConceptInstanceManager;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/conceptInstance")
public class ConceptInstanceController extends BaseController {

  @Autowired private ConceptInstanceManager conceptInstanceManager;

  @RequestMapping(value = "/level", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryConceptLevelInstance(ConceptLevelInstanceRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ConceptLevelInstanceResponse>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
          }

          @Override
          public ConceptLevelInstanceResponse action() {
            return conceptInstanceManager.queryConceptLevelInstance(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> query(
      @RequestParam String conceptType, @RequestParam Set<String> conceptInstanceIds) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<ConceptInstanceResponse>>() {
          @Override
          public void check() {}

          @Override
          public List<ConceptInstanceResponse> action() {
            return conceptInstanceManager.query(conceptType, conceptInstanceIds);
          }
        });
  }
}
