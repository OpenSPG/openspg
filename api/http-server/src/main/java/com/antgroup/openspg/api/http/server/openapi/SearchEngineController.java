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

package com.antgroup.openspg.api.http.server.openapi;

import com.antgroup.openspg.api.facade.dto.common.request.SearchEngineIndexRequest;
import com.antgroup.openspg.api.facade.dto.common.response.SearchEngineIndexResponse;
import com.antgroup.openspg.api.http.server.BaseController;
import com.antgroup.openspg.api.http.server.HttpBizCallback;
import com.antgroup.openspg.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.biz.common.SearchEngineManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "SearchEngineController")
@Controller
@RequestMapping("/public/v1/searchEngine")
public class SearchEngineController extends BaseController {

  @Autowired private SearchEngineManager searchEngineManager;

  @RequestMapping(value = "/index", method = RequestMethod.GET)
  public ResponseEntity<Object> queryIndex(@RequestParam String spgType) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<SearchEngineIndexResponse>() {
          @Override
          public void check() {}

          @Override
          public SearchEngineIndexResponse action() {
            SearchEngineIndexRequest request = new SearchEngineIndexRequest();
            request.setSpgType(spgType);
            return searchEngineManager.queryIndex(request);
          }
        });
  }
}
