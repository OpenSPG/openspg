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
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.ConfigManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.model.project.Project;
import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/public/v1/project")
public class ProjectController extends BaseController {

  @Autowired private SchemaController schemaController;

  @Autowired private ProjectManager projectManager;

  @Autowired private ConfigManager configManager;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Object> create(@RequestBody ProjectCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Project>() {
          @Override
          public void check() {}

          @Override
          public Project action() {
            Project project = projectManager.create(request);
            if (request.getAutoSchema() == null || Boolean.TRUE.equals(request.getAutoSchema())) {
              SchemaAlterRequest request = new SchemaAlterRequest();
              request.setProjectId(project.getId());
              request.setSchemaDraft(
                  SchemaController.getDefaultSchemaDraft(project.getNamespace()));
              schemaController.alterSchema(request);
            }
            return project;
          }
        });
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<Object> query(
      @RequestParam(required = false) Long tenantId,
      @RequestParam(required = false) Long projectId) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<Project>>() {
          @Override
          public void check() {}

          @Override
          public List<Project> action() {
            ProjectQueryRequest request = new ProjectQueryRequest();
            request.setTenantId(tenantId);
            request.setProjectId(projectId);
            List<Project> projectList = projectManager.query(request);
            List<Project> newProjectList = Lists.newArrayList();
            projectList.forEach(
                project -> {
                  String config = project.getConfig();
                  JSONObject configJson = JSON.parseObject(config);
                  if (configJson != null) {
                    configManager.backwardCompatible(configJson);
                    JSONObject vectorizer =
                        configManager.clearRedundantField(
                            configJson.getJSONObject(SpgAppConstant.VECTORIZER),
                            SpgAppConstant.VECTORIZER);
                    configJson.put(SpgAppConstant.VECTORIZER, vectorizer);
                    config = configJson.toJSONString();
                  }
                  newProjectList.add(
                      new Project(
                          project.getId(),
                          project.getName(),
                          project.getDescription(),
                          project.getNamespace(),
                          project.getTenantId(),
                          config));
                });
            return newProjectList;
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/update")
  public ResponseEntity<Object> update(@RequestBody ProjectCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Project>() {
          @Override
          public void check() {}

          @Override
          public Project action() {
            return projectManager.update(request);
          }
        });
  }
}
