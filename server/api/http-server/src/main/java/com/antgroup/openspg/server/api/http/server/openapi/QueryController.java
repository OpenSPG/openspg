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

import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.service.QueryManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/public/v1/query")
public class QueryController extends BaseController {

  @Autowired private QueryManager queryManager;

  @RequestMapping(method = RequestMethod.POST, value = "/spgType")
  public ResponseEntity<Object> query(@RequestBody SPGTypeQueryRequest query) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<SPGTypeInstance>>() {
          @Override
          public void check() {}

          @Override
          public List<SPGTypeInstance> action() {
            return queryManager.query(query);
          }
        });
  }
}
