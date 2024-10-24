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

package com.antgroup.openspg.server.core.schema.service.alter.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.impl.graphstore.neo4j.Neo4jConstants;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jAdminUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jCommonUtils;
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class Neo4jSyncer extends BaseSchemaSyncer {

  @Autowired private ProjectService projectService;

  @Override
  public void syncSchema(Long projectId, SPGSchemaAlterCmd schemaEditCmd) {
    Project project = projectService.queryById(projectId);
    int vectorDimensions = getVectorDimensions(project);
    String url = projectService.getGraphStoreUrl(projectId);
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
    String database = uriComponents.getQueryParams().getFirst(Neo4jConstants.DATABASE);
    String host =
        String.format(
            "%s://%s:%s",
            uriComponents.getScheme(), uriComponents.getHost(), uriComponents.getPort());
    String user = uriComponents.getQueryParams().getFirst(Neo4jConstants.USER);
    String password = uriComponents.getQueryParams().getFirst(Neo4jConstants.PASSWORD);
    Neo4jAdminUtils driver = new Neo4jAdminUtils(host, user, password, database);
    driver.neo4jGraph.initializeSchema(
        schemaEditCmd.getSpgSchema().getSpgTypes(), vectorDimensions);
  }

  private int getVectorDimensions(Project project) {
    String config = project.getConfig();
    if (StringUtils.isNotBlank(config)) {
      JSONObject vectorizerConfig =
          JSON.parseObject(config).getJSONObject(CommonConstants.VECTORIZER);
      if (vectorizerConfig == null) {
        return Neo4jCommonUtils.DEFAULT_VECTOR_DIMENSIONS;
      }
      Integer vectorDimensions = vectorizerConfig.getInteger(CommonConstants.VECTOR_DIMENSIONS);
      if (vectorDimensions != null) return vectorDimensions;
    }
    return Neo4jCommonUtils.DEFAULT_VECTOR_DIMENSIONS;
  }
}
