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

package com.antgroup.openspg.server.biz.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.impl.graphstore.neo4j.Neo4jConstants;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jAdminUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.project.ProjectRepository;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProjectManagerImpl implements ProjectManager {

  @Autowired private ProjectRepository projectRepository;
  @Autowired private ProjectService projectService;

  @Value("${cloudext.graphstore.url:}")
  private String url;

  @Override
  public Project create(ProjectCreateRequest request) {
    JSONObject config = setDatabase(request.getConfig(), request.getNamespace());
    setGraphStore(request.getNamespace(), config, true);
    Project project =
        new Project(
            null,
            request.getName(),
            request.getDesc(),
            request.getNamespace(),
            request.getTenantId(),
            config.toJSONString());
    Long projectId = projectRepository.save(project);
    project.setId(projectId);
    return project;
  }

  @Override
  public Project update(ProjectCreateRequest request) {
    Project project = projectRepository.queryById(request.getId());
    JSONObject config = setDatabase(request.getConfig(), project.getNamespace());
    setGraphStore(request.getNamespace(), config, false);
    config = setVectorDimensions(config, project);
    Project update = new Project(request.getId(), null, null, null, null, config.toJSONString());
    return projectRepository.update(update);
  }

  private JSONObject setDatabase(String configStr, String namespace) {
    JSONObject config = new JSONObject();
    if (StringUtils.isNotBlank(configStr)) {
      config = JSONObject.parseObject(configStr);
    }
    if (config.containsKey(CommonConstants.GRAPH_STORE)) {
      config
          .getJSONObject(CommonConstants.GRAPH_STORE)
          .put(CommonConstants.DATABASE, namespace.toLowerCase());
    } else {
      JSONObject graphStore = new JSONObject();
      graphStore.put(CommonConstants.DATABASE, namespace.toLowerCase());
      config.put(CommonConstants.GRAPH_STORE, graphStore);
    }
    return config;
  }

  private JSONObject setVectorDimensions(JSONObject config, Project project) {
    JSONObject oldConfig = JSONObject.parseObject(project.getConfig());
    String vectorDimensions = null;
    if (oldConfig.containsKey(CommonConstants.VECTORIZER)) {
      vectorDimensions =
          oldConfig
              .getJSONObject(CommonConstants.VECTORIZER)
              .getString(CommonConstants.VECTOR_DIMENSIONS);
    }
    if (StringUtils.isBlank(vectorDimensions)) {
      return config;
    }
    if (config.containsKey(CommonConstants.VECTORIZER)) {
      config
          .getJSONObject(CommonConstants.VECTORIZER)
          .put(CommonConstants.VECTOR_DIMENSIONS, vectorDimensions);
    } else {
      JSONObject graphStore = new JSONObject();
      graphStore.put(CommonConstants.VECTOR_DIMENSIONS, vectorDimensions);
      config.put(CommonConstants.VECTORIZER, graphStore);
    }
    return config;
  }

  @Override
  public Project queryById(Long projectId) {
    return projectRepository.queryById(projectId);
  }

  @Override
  public Integer deleteById(Long projectId) {
    Project project = projectRepository.queryById(projectId);
    if (project == null) {
      return 0;
    }
    deleteDatabase(project);

    return projectRepository.deleteById(projectId);
  }

  public void deleteDatabase(Project project) {
    JSONObject config = JSONObject.parseObject(project.getConfig());
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
    String database = uriComponents.getQueryParams().getFirst(Neo4jConstants.DATABASE);
    JSONObject graphStore = config.getJSONObject(CommonConstants.GRAPH_STORE);
    String host = graphStore.getString(Neo4jConstants.URI);
    String user = graphStore.getString(Neo4jConstants.USER);
    String password = graphStore.getString(Neo4jConstants.PASSWORD);
    String dropDatabase = project.getNamespace().toLowerCase();
    Neo4jAdminUtils driver = new Neo4jAdminUtils(host, user, password, database);
    driver.neo4jGraph.dropDatabase(dropDatabase);
  }

  public void setGraphStore(String namespace, JSONObject config, boolean createDatabase) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
    String database = uriComponents.getQueryParams().getFirst(Neo4jConstants.DATABASE);
    String host =
        String.format(
            "%s://%s:%s",
            uriComponents.getScheme(), uriComponents.getHost(), uriComponents.getPort());
    String user = uriComponents.getQueryParams().getFirst(Neo4jConstants.USER);
    String password = uriComponents.getQueryParams().getFirst(Neo4jConstants.PASSWORD);

    JSONObject graphStore = config.getJSONObject(CommonConstants.GRAPH_STORE);
    if (graphStore.containsKey(Neo4jConstants.URI)) {
      host = graphStore.getString(Neo4jConstants.URI);
    }
    if (graphStore.containsKey(Neo4jConstants.USER)) {
      user = graphStore.getString(Neo4jConstants.USER);
    } else {
      graphStore.put(Neo4jConstants.USER, user);
    }
    if (graphStore.containsKey(Neo4jConstants.PASSWORD)) {
      password = graphStore.getString(Neo4jConstants.PASSWORD);
    } else {
      graphStore.put(Neo4jConstants.PASSWORD, password);
    }
    if (createDatabase) {
      Neo4jAdminUtils driver = new Neo4jAdminUtils(host, user, password, database);
      String projectDatabase = namespace.toLowerCase();
      driver.neo4jGraph.createDatabase(projectDatabase);
    }
  }

  @Override
  public List<Project> query(ProjectQueryRequest request) {
    return projectRepository.query(request);
  }

  @Override
  public Paged<Project> queryPaged(ProjectQueryRequest request, int start, int size) {
    return projectRepository.queryPaged(request, start, size);
  }

  @Override
  public String getGraphStoreUrl(Long projectId) {
    return projectService.getGraphStoreUrl(projectId);
  }

  @Override
  public String getSearchEngineUrl(Long projectId) {
    // For Neo4j, GraphStore and SearchEngine are the same.
    return getGraphStoreUrl(projectId);
  }
}
