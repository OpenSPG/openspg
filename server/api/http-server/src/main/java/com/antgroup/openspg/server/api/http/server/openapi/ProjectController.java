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

import static com.antgroup.openspg.common.constants.SpgAppConstant.ACCOUNT_PATTERN;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.common.util.enums.ProjectTagEnum;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.project.Project;
import com.baidu.brpc.utils.CollectionUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProjectController extends BaseController {

  @Autowired private SchemaController schemaController;

  @Autowired private ProjectManager projectManager;

  @Autowired private PermissionManager permissionManager;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Object> create(@RequestBody ProjectCreateRequest request) {
    log.info("HTTP Create Project Params: {}", JSON.toJSONString(request));
    return HttpBizTemplate.execute(
        new HttpBizCallback<Project>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request body", request);
            AssertUtils.assertParamObjectIsNotNull("userNo", request.getUserNo());
            if (!ACCOUNT_PATTERN.matcher(request.getUserNo()).matches()) {
              throw new IllegalParamsException(
                  "account(userNo) length is 6-20, only support letters,numbers and underscores");
            }
            AssertUtils.assertParamObjectIsNotNull("name", request.getName());
            AssertUtils.assertParamObjectIsNotNull("namespace", request.getNamespace());
            AssertUtils.assertParamStringIsNotBlank("tag", request.getTag());
            AssertUtils.assertParamStringIsNotBlank("visibility", request.getVisibility());
            AssertUtils.assertParamObjectIsNotNull("config", request.getConfig());
            AssertUtils.assertParamIsTrue(
                "namespace length >= 3", request.getNamespace().length() >= 3);
            if (StringUtils.equals(request.getTag(), ProjectTagEnum.LOCAL.name())
                && !request.getConfig().containsKey(SpgAppConstant.VECTORIZER)) {
              throw new IllegalParamsException("tag = LOCAL, vectorizer cannot be empty");
            }
          }

          @Override
          public Project action() {
            request.setIsKnext(Boolean.TRUE);
            Project project = projectManager.create(request);
            if (null == project || null == project.getId()) {
              return null;
            }
            if (ProjectTagEnum.PUBLIC_NET.name().equalsIgnoreCase(request.getTag())) {
              return project;
            }
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
                    projectManager.completionVectorizer(configJson);
                    config = configJson.toJSONString();
                  }
                  newProjectList.add(
                      new Project(
                          project.getId(),
                          project.getName(),
                          project.getDescription(),
                          project.getNamespace(),
                          project.getTenantId(),
                          config,
                          project.getTag()));
                });
            return newProjectList;
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/update")
  public ResponseEntity<Object> update(@RequestBody ProjectCreateRequest request) {
    log.info("HTTP Update Project Params: {}", JSON.toJSONString(request));
    return HttpBizTemplate.execute(
        new HttpBizCallback<Project>() {

          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request body", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
            AssertUtils.assertParamStringIsNotBlank("userNo", request.getUserNo());
            AssertUtils.assertParamObjectIsNotNull("config", request.getConfig());
            String userNo = request.getUserNo();
            boolean hasPermission =
                permissionManager.hasPermission(
                    userNo,
                    request.getId(),
                    ResourceTagEnum.KNOWLEDGE_BASE.name(),
                    PermissionEnum.OWNER.name());
            if (!hasPermission) {
              throw new SpgException(SpgMessageEnum.KB_NOT_OWNER);
            }
            Project project = projectManager.queryById(request.getId());
            AssertUtils.assertParamObjectIsNotNull("query project by id", project);
            ProjectQueryRequest projectQueryRequest = new ProjectQueryRequest();
            projectQueryRequest.setName(request.getName());
            List<Project> query = projectManager.query(projectQueryRequest);
            if (CollectionUtils.isNotEmpty(query)
                && query.stream().anyMatch(it -> !it.getId().equals(request.getId()))) {
              throw new SpgException(SpgMessageEnum.KB_NAME_EXIST);
            }
          }

          @Override
          public Project action() {
            request.setIsKnext(Boolean.TRUE);
            return projectManager.update(request);
          }
        });
  }
}
