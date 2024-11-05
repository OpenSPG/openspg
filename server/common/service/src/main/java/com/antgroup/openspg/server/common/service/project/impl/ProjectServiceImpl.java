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

package com.antgroup.openspg.server.common.service.project.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.impl.graphstore.neo4j.Neo4jConstants;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.project.ProjectRepository;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProjectServiceImpl implements ProjectService {

  @Autowired private ProjectRepository projectRepository;

  @Value("${cloudext.graphstore.url:}")
  private String url;

  @Override
  public Project queryById(Long projectId) {
    return projectRepository.queryById(projectId);
  }

  @Override
  public String getGraphStoreUrl(Long projectId) {
    Project project = projectRepository.queryById(projectId);
    String url = this.url;
    String user = null;
    String password = null;
    String database = null;
    String config = project.getConfig();
    if (StringUtils.isNotBlank(config)) {
      JSONObject graphStore = JSON.parseObject(config).getJSONObject(CommonConstants.GRAPH_STORE);
      if (graphStore.containsKey(Neo4jConstants.URI)) {
        url = graphStore.getString(Neo4jConstants.URI);
      }
      if (graphStore.containsKey(Neo4jConstants.USER)) {
        user = graphStore.getString(Neo4jConstants.USER);
      }
      if (graphStore.containsKey(Neo4jConstants.PASSWORD)) {
        password = graphStore.getString(Neo4jConstants.PASSWORD);
      }
      if (graphStore.containsKey(Neo4jConstants.DATABASE)) {
        database = graphStore.getString(Neo4jConstants.DATABASE);
      }
    }

    UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
    database =
        StringUtils.isBlank(database)
            ? uriComponents.getQueryParams().getFirst(Neo4jConstants.DATABASE)
            : database;
    user =
        StringUtils.isBlank(user)
            ? uriComponents.getQueryParams().getFirst(Neo4jConstants.USER)
            : user;
    password =
        StringUtils.isBlank(password)
            ? uriComponents.getQueryParams().getFirst(Neo4jConstants.PASSWORD)
            : password;
    String host =
        String.format(
            "%s://%s:%s?user=%s&password=%s&database=%s&namespace=%s",
            uriComponents.getScheme(),
            uriComponents.getHost(),
            uriComponents.getPort(),
            user,
            password,
            database,
            project.getNamespace());
    return host;
  }
}
