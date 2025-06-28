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
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.CommonEnum;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceQuery;
import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/datasource")
@Slf4j
public class DataSourceController extends BaseController {

  @Autowired private DataSourceService dataSourceService;

  @RequestMapping(value = "/insert", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Boolean> insert(@RequestBody DataSource request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/datasource/insert request: {}", JSON.toJSONString(request));
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("name", request.getDbName());
            AssertUtils.assertParamObjectIsNotNull("url", request.getDbUrl());
          }

          @Override
          public Boolean action() {
            if (StringUtils.isBlank(request.getCreateUser())) {
              Account account = getLoginAccount();
              String user = account != null ? account.getWorkNo() : BuilderConstant.SYSTEM;
              request.setCreateUser(user);
            }
            request.setUpdateUser(request.getCreateUser());
            return dataSourceService.insert(request) > 0;
          }
        });
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Boolean> update(@RequestBody DataSource request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/datasource/update request: {}", JSON.toJSONString(request));
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
            AssertUtils.assertParamObjectIsNotNull("name", request.getDbName());
            AssertUtils.assertParamObjectIsNotNull("url", request.getDbUrl());
          }

          @Override
          public Boolean action() {
            Account account = getLoginAccount();
            String user = account != null ? account.getWorkNo() : BuilderConstant.SYSTEM;
            request.setUpdateUser(user);
            return dataSourceService.update(request) > 0;
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
            log.info("/datasource/delete id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return dataSourceService.deleteById(id) > 0;
          }
        });
  }

  @RequestMapping(value = "/getById", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<DataSource> getById(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<DataSource>() {
          @Override
          public void check() {
            log.info("/datasource/getById id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public DataSource action() {
            return dataSourceService.getById(id);
          }
        });
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Paged<DataSource>> search(@RequestBody DataSourceQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<DataSource>>() {
          @Override
          public void check() {
            log.info("/datasource/search request: {}", JSON.toJSONString(request));
          }

          @Override
          public Paged<DataSource> action() {
            return dataSourceService.query(request);
          }
        });
  }

  @RequestMapping(value = "/getAllDatabase", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<List<String>> getAllDatabase(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<String>>() {
          @Override
          public void check() {
            log.info("/datasource/getAllDatabase id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public List<String> action() {
            return dataSourceService.getAllDatabase(id);
          }
        });
  }

  @RequestMapping(value = "/getAllTable", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<List<String>> getAllTable(Long id, String dbName, String keyword) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<String>>() {
          @Override
          public void check() {
            log.info("/datasource/getAllDatabase id: {} dbName: {}", id, dbName);
            AssertUtils.assertParamObjectIsNotNull("id", id);
            AssertUtils.assertParamObjectIsNotNull("dbName", dbName);
          }

          @Override
          public List<String> action() {
            return dataSourceService.getAllTable(id, dbName, keyword);
          }
        });
  }

  @RequestMapping(value = "/getTableDetail", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<List<Column>> getTableDetail(Long id, String dbName, String tableName) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Column>>() {
          @Override
          public void check() {
            log.info(
                "/datasource/getAllDatabase id: {} dbName: {} tableName: {}",
                id,
                dbName,
                tableName);
            AssertUtils.assertParamObjectIsNotNull("id", id);
            AssertUtils.assertParamObjectIsNotNull("dbName", dbName);
            AssertUtils.assertParamObjectIsNotNull("tableName", tableName);
          }

          @Override
          public List<Column> action() {
            return dataSourceService.getTableDetail(id, dbName, tableName);
          }
        });
  }

  @RequestMapping(value = "/testConnect", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Boolean> testConnect(@RequestBody DataSource request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/datasource/testConnect request: {}", JSON.toJSONString(request));
          }

          @Override
          public Boolean action() {
            return dataSourceService.testConnect(request);
          }
        });
  }

  @RequestMapping(value = "/getDataSourceType", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<List<Column>> getDataSourceType(String category) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Column>>() {
          @Override
          public void check() {
            log.info("/datasource/getDataSourceType category: {}", category);
          }

          @Override
          public List<Column> action() {
            return dataSourceService.getDataSourceType(category);
          }
        });
  }

  @RequestMapping(value = "/getDataSourceGroupByType", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<List<Column>> getDataSourceGroupByType(@RequestBody DataSourceQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Column>>() {
          @Override
          public void check() {
            log.info("/datasource/getDataSourceGroupByType request: {}", request);
          }

          @Override
          public List<Column> action() {
            List<Column> types = Lists.newArrayList();
            List<DataSource> allType = dataSourceService.getGroupByType(request);
            for (DataSource dataSource : allType) {
              CommonEnum.DataSourceType type = dataSource.getType();
              if (type == null) {
                continue;
              }
              types.add(new Column(type.name(), type.getCategory().name(), type.getName()));
            }

            return types;
          }
        });
  }
}
