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

import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceUsageCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataSourceUsageQueryRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.DataSourceManager;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceUsage;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "DataSourceController", description = "数据源管理")
@Controller
@RequestMapping("/public/v1")
public class DataSourceController extends BaseController {

  @Autowired private DataSourceManager dataSourceManager;

  @RequestMapping(value = "/dataSource", method = RequestMethod.POST)
  public ResponseEntity<Object> create(@RequestBody DataSourceCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<DataSource>() {
          @Override
          public void check() {}

          @Override
          public DataSource action() {
            return dataSourceManager.create(request);
          }
        });
  }

  @RequestMapping(value = "/dataSource", method = RequestMethod.GET)
  public ResponseEntity<Object> query(
      @RequestParam(required = false) String type, @RequestParam(required = false) String name) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<DataSource>>() {
          @Override
          public void check() {}

          @Override
          public List<DataSource> action() {
            DataSourceQueryRequest request = new DataSourceQueryRequest();
            request.setType(type);
            request.setName(name);
            return dataSourceManager.query(request);
          }
        });
  }

  @RequestMapping(value = "/dataSourceUsage", method = RequestMethod.POST)
  public ResponseEntity<Object> mount(@RequestBody DataSourceUsageCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<DataSourceUsage>() {
          @Override
          public void check() {}

          @Override
          public DataSourceUsage action() {
            return dataSourceManager.mount(request);
          }
        });
  }

  @RequestMapping(value = "/dataSourceUsage", method = RequestMethod.GET)
  public ResponseEntity<Object> query(
      @RequestParam(required = false) String dataSourceName,
      @RequestParam(required = false) String usageType,
      @RequestParam(required = false) String mountObjectType,
      @RequestParam(required = false) String mountObjectId) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<DataSourceUsage>>() {
          @Override
          public void check() {}

          @Override
          public List<DataSourceUsage> action() {
            DataSourceUsageQueryRequest request = new DataSourceUsageQueryRequest();
            request.setDataSourceName(dataSourceName);
            request.setUsageType(usageType);
            request.setMountObjectType(mountObjectType);
            request.setMountObjectId(mountObjectId);
            return dataSourceManager.query(request);
          }
        });
  }
}
