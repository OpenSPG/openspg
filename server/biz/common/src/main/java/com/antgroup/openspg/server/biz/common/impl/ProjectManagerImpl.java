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
import com.antgroup.openspg.core.schema.model.SPGSchema;
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.project.ProjectRepository;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.schema.service.alter.sync.BaseSchemaSyncer;
import com.antgroup.openspg.server.core.schema.service.alter.sync.SchemaStorageEnum;
import com.antgroup.openspg.server.core.schema.service.alter.sync.SchemaSyncerFactory;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ProjectManagerImpl implements ProjectManager {

  @Autowired private ProjectRepository projectRepository;
  @Autowired private ProjectService projectService;
  @Autowired private SchemaSyncerFactory schemaSyncerFactory;
  @Autowired private SPGTypeService spgTypeService;

  @Value("${cloudext.graphstore.url:}")
  private String url;

  @Override
  public Project create(ProjectCreateRequest request) {
    JSONObject config = setDatabase(request.getConfig(), request.getNamespace());
    Project project =
        new Project(
            null,
            request.getName(),
            request.getDescription(),
            request.getNamespace(),
            request.getTenantId(),
            config.toJSONString());
    setGraphStore(project, config, true);
    Long projectId = projectRepository.save(project);
    project.setId(projectId);
    return project;
  }

  @Override
  public Project update(ProjectCreateRequest request) {
    Project project = projectRepository.queryById(request.getId());
    JSONObject config = setDatabase(request.getConfig(), project.getNamespace());
    setGraphStore(project, config, true);
    config = setVectorDimensions(config, project);
    Project update =
        new Project(
            request.getId(),
            request.getName(),
            request.getDescription(),
            null,
            null,
            config.toJSONString());
    update = projectRepository.update(update);
    long start = System.currentTimeMillis();
    createSchema(request.getId());
    log.info("createSchema cost {} ms", System.currentTimeMillis() - start);
    return update;
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
    try {
      deleteDatabase(project);
    } catch (Exception e) {
      log.error("delete project database Exception:" + project, e);
    }

    return projectRepository.deleteById(projectId);
  }

  public void deleteDatabase(Project project) {
    JSONObject config = JSONObject.parseObject(project.getConfig());
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
    }
    if (graphStore.containsKey(Neo4jConstants.PASSWORD)) {
      password = graphStore.getString(Neo4jConstants.PASSWORD);
    }
    String dropDatabase = project.getNamespace().toLowerCase();
    Neo4jAdminUtils driver = new Neo4jAdminUtils(host, user, password, database);
    driver.neo4jGraph.dropDatabase(dropDatabase);
  }

  public void setGraphStore(Project project, JSONObject config, boolean createDatabase) {
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
    } else {
      graphStore.put(Neo4jConstants.URI, host);
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
      String projectDatabase = project.getNamespace().toLowerCase();
      driver.neo4jGraph.createDatabase(projectDatabase);
    }
  }

  public void createSchema(Long projectId) {
    try {
      BaseSchemaSyncer schemaSyncer = schemaSyncerFactory.getSchemaSyncer(SchemaStorageEnum.GRAPH);
      if (schemaSyncer != null) {
        Set<SPGTypeIdentifier> spreadStdTypeNames = spgTypeService.querySpreadStdTypeName();
        List<BaseSPGType> spgTypes = spgTypeService.queryProjectSchema(projectId).getSpgTypes();
        SPGSchemaAlterCmd schemaEditCmd =
            new SPGSchemaAlterCmd(new SPGSchema(spgTypes, spreadStdTypeNames));
        schemaSyncer.syncSchema(projectId, schemaEditCmd);
      }
    } catch (Exception e) {
      log.error("createSchema Exception:" + projectId, e);
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

  @Override
  public Project queryByNamespace(String namespace) {
    return projectRepository.queryByNamespace(namespace);
  }
}
