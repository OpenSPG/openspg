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

package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.core.schema.model.semantic.TripleSemantic;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.catalog.impl.OpenSPGCatalog;
import com.antgroup.openspg.reasoner.common.exception.SystemError;
import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.thinker.SimplifyThinkerParser;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.client.ConceptFacade;
import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.server.api.http.client.HttpConceptFacade;
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class OpenSPGLogicCatalog extends LogicCatalog {
  private Catalog openSPGCatalog;
  private Long projectId;
  private SchemaFacade schemaFacade;
  private ConceptFacade conceptFacade;

  public OpenSPGLogicCatalog(Long projectId, KgSchemaConnectionInfo connInfo) {
    this.openSPGCatalog = new OpenSPGCatalog(projectId, connInfo, null);
    HttpClientBootstrap.init(new ConnectionInfo(connInfo.uri()));
    this.projectId = projectId;
    this.schemaFacade = new HttpSchemaFacade();
    this.conceptFacade = new HttpConceptFacade();
  }

  @Override
  public LogicNetwork loadLogicNetwork() {
    ProjectSchemaRequest request = new ProjectSchemaRequest();
    request.setProjectId(projectId);
    ApiResponse<ProjectSchema> projectSchema = schemaFacade.queryProjectSchema(request);
    if (!projectSchema.isSuccess()) {
      throw new SystemError("Cannot get schema for projectId=" + projectId, null);
    }
    Set<String> conceptTypes =
        projectSchema.getData().getSpgTypes().stream()
            .filter(e -> e.getSpgTypeEnum() == SPGTypeEnum.CONCEPT_TYPE)
            .map(BaseSPGType::getName)
            .collect(Collectors.toSet());

    SPGTypeRequest spgTypeRequest = new SPGTypeRequest(StringUtils.join(conceptTypes, ","));
    ApiResponse<List<TripleSemantic>> response =
        conceptFacade.getReasoningConceptsDetail(spgTypeRequest);
    if (!response.isSuccess()) {
      throw new SystemError("Cannot get schema for projectId=" + projectId, null);
    }
    LogicNetwork logicNetwork = new LogicNetwork();
    for (TripleSemantic ts : response.getData()) {
      SimplifyThinkerParser parser = new SimplifyThinkerParser();
      Rule rule = parser.parseSimplifyDsl(ts.getLogicalRule().getContent(), null).head();
      logicNetwork.addRule(rule);
    }

    return logicNetwork;
  }

  @Override
  public SemanticPropertyGraph getKnowledgeGraph() {
    return this.openSPGCatalog.getKnowledgeGraph();
  }

  @Override
  public scala.collection.immutable.Map<AbstractConnection, scala.collection.immutable.Set<String>>
      getConnections() {
    return this.openSPGCatalog.getConnections();
  }

  @Override
  public scala.collection.immutable.Set<Field> getDefaultNodeProperties() {
    return this.openSPGCatalog.getDefaultNodeProperties();
  }

  @Override
  public scala.collection.immutable.Set<Field> getDefaultEdgeProperties() {
    return this.openSPGCatalog.getDefaultEdgeProperties();
  }
}
