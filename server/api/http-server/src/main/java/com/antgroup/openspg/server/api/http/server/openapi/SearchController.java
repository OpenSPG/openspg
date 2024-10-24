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

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.TextSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.VectorSearchRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.service.SearchManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/public/v1/search")
public class SearchController extends BaseController {

  @Autowired private SearchManager searchManager;

  @RequestMapping(method = RequestMethod.GET, value = "/spgType")
  public ResponseEntity<Object> spgTypeSearch(SPGTypeSearchRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<IdxRecord>>() {
          @Override
          public void check() {}

          @Override
          public List<IdxRecord> action() {
            return searchManager.spgTypeSearch(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/text")
  public ResponseEntity<Object> textSearch(@RequestBody TextSearchRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<IdxRecord>>() {
          @Override
          public void check() {}

          @Override
          public List<IdxRecord> action() {
            return searchManager.textSearch(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/vector")
  public ResponseEntity<Object> vectorSearch(@RequestBody VectorSearchRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<IdxRecord>>() {
          @Override
          public void check() {}

          @Override
          public List<IdxRecord> action() {
            return searchManager.vectorSearch(request);
          }
        });
  }
}
