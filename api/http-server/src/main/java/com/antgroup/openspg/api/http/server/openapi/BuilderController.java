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

import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobSubmitRequest;
import com.antgroup.openspg.api.http.server.BaseController;
import com.antgroup.openspg.api.http.server.HttpBizCallback;
import com.antgroup.openspg.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.biz.spgbuilder.BuilderManager;
import com.antgroup.openspg.core.spgbuilder.model.service.BaseBuilderReceipt;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/public/v1/builder")
public class BuilderController extends BaseController {

    @Autowired
    private BuilderManager builderManager;

    @RequestMapping(value = "/submitJobInfo", method = RequestMethod.POST)
    public ResponseEntity<Object> submitJobInfo(@RequestBody BuilderJobSubmitRequest request) {
        return HttpBizTemplate.execute(new HttpBizCallback<BaseBuilderReceipt>() {
            @Override
            public void check() {
            }

            @Override
            public BaseBuilderReceipt action() {
                return builderManager.submitJobInfo(request);
            }
        });
    }

    @RequestMapping(value = "/queryJobInst", method = RequestMethod.GET)
    public ResponseEntity<Object> queryJobInst(
        @RequestParam(required = false) Long jobInstId) {
        return HttpBizTemplate.execute(new HttpBizCallback<List<BuilderJobInst>>() {
            @Override
            public void check() {
            }

            @Override
            public List<BuilderJobInst> action() {
                BuilderJobInstQuery query = new BuilderJobInstQuery();
                query.setBuildingJobInstId(jobInstId);
                return builderManager.queryJobInst(query);
            }
        });
    }
}
