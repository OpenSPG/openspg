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

import com.antgroup.openspg.server.api.facade.dto.builder.request.OperatorCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.builder.request.OperatorVersionRequest;
import com.antgroup.openspg.server.api.facade.dto.builder.response.OperatorCreateResponse;
import com.antgroup.openspg.server.api.facade.dto.builder.response.OperatorVersionResponse;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.builder.core.operator.OperatorOverview;
import com.antgroup.openspg.builder.core.operator.OperatorVersion;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.antgroup.openspg.server.biz.builder.OperatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/public/v1/operator")
public class OperatorController extends BaseController {

  @Autowired private OperatorManager operatorManager;

  @RequestMapping(value = "/overview", method = RequestMethod.GET)
  public ResponseEntity<Object> queryOverview(@RequestParam(required = false) String name) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<OperatorOverview>>() {
          @Override
          public void check() {}

          @Override
          public List<OperatorOverview> action() {
            return operatorManager.listOverview(name);
          }
        });
  }

  @RequestMapping(value = "/version", method = RequestMethod.GET)
  public ResponseEntity<Object> queryVersion(@RequestParam String name) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<OperatorVersion>>() {
          @Override
          public void check() {}

          @Override
          public List<OperatorVersion> action() {
            return operatorManager.listVersion(name);
          }
        });
  }

  @RequestMapping(value = "/overview", method = RequestMethod.POST)
  public ResponseEntity<Object> create(@RequestBody OperatorCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<OperatorCreateResponse>() {
          @Override
          public void check() {}

          @Override
          public OperatorCreateResponse action() {
            return operatorManager.create(request);
          }
        });
  }

  @RequestMapping(
      value = "/version",
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Object> addVersion(OperatorVersionRequest request, MultipartFile file) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<OperatorVersionResponse>() {
          @Override
          public void check() {}

          @Override
          public OperatorVersionResponse action() {
            InputStream inputStream = null;
            try {
              inputStream = file.getInputStream();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            return operatorManager.addVersion(request, inputStream);
          }
        });
  }
}
