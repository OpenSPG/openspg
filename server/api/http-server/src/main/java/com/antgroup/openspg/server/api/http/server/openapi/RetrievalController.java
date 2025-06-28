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

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.common.model.retrieval.RetrievalQuery;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.retrieval.RetrievalService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/retrieval")
@Slf4j
public class RetrievalController extends BaseController {

  @Autowired private RetrievalService retrievalService;

  @Autowired private BuilderJobService builderJobService;

  @RequestMapping(value = "/getAll", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<List<Retrieval>> getAll() {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Retrieval>>() {
          @Override
          public void check() {
            log.info("/retrieval/getAll");
          }

          @Override
          public List<Retrieval> action() {
            return retrievalService.query(new RetrievalQuery()).getResults();
          }
        });
  }

  @RequestMapping(value = "/delete", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> delete(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/retrieval/delete id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return retrievalService.deleteById(id) > 0;
          }
        });
  }

  @RequestMapping(value = "/getById", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Retrieval> getById(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Retrieval>() {
          @Override
          public void check() {
            log.info("/retrieval/getById id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Retrieval action() {
            return retrievalService.getById(id);
          }
        });
  }

  @RequestMapping(value = "/getByProjectId", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<List<Retrieval>> getByProjectId(Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Retrieval>>() {
          @Override
          public void check() {
            log.info("/retrieval/getByProjectId projectId: {}", projectId);
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          @Override
          public List<Retrieval> action() {
            return retrievalService.getRetrievalByProjectId(projectId);
          }
        });
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Paged<Retrieval>> search(@RequestBody RetrievalQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<Retrieval>>() {
          @Override
          public void check() {
            log.info("/retrieval/search request: {}", JSON.toJSONString(request));
          }

          @Override
          public Paged<Retrieval> action() {
            return retrievalService.query(request);
          }
        });
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Long> update(@RequestBody Retrieval request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Long>() {
          @Override
          public void check() {
            log.info("/retrieval/update request: {}", JSON.toJSONString(request));
            AssertUtils.assertParamObjectIsNotNull("retrieval", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          @Override
          public Long action() {
            return retrievalService.update(request);
          }
        });
  }
}
