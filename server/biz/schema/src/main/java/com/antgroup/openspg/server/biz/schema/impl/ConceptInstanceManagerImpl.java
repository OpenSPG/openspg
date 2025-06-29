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

package com.antgroup.openspg.server.biz.schema.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGDataQueryService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.BatchVertexLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.OneHopLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.core.schema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptType;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptLevelInstanceRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptInstanceResponse;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptLevelInstanceResponse;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.schema.ConceptInstanceManager;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConceptInstanceManagerImpl implements ConceptInstanceManager {

  @Autowired private SPGTypeService spgTypeService;

  @Autowired private ProjectManager projectManager;

  @Override
  public ConceptLevelInstanceResponse queryConceptLevelInstance(
      ConceptLevelInstanceRequest request) {
    Long projectId = request.getProjectId();
    if (projectId == null) {
      projectId = getProjectId(request.getConceptType());
    }
    String graphStoreUrl = projectManager.getGraphStoreUrl(projectId);
    LPGDataQueryService graphStoreClient =
        (LPGDataQueryService) GraphStoreClientDriverManager.getClient(graphStoreUrl);
    BaseSPGType spgType =
        spgTypeService.querySPGTypeByIdentifier(SPGTypeIdentifier.parse(request.getConceptType()));
    if (spgType == null || !spgType.isConceptType()) {
      throw new IllegalArgumentException(
          String.format("%s is not a concept type", request.getConceptType()));
    }
    ConceptType conceptType = (ConceptType) spgType;
    GraphLPGRecordStruct graph =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new OneHopLPGRecordQuery(
                    request.getRootConceptInstance(),
                    request.getConceptType(),
                    Sets.newHashSet(
                        new EdgeTypeName(
                            request.getConceptType(),
                            conceptType.getConceptLayerConfig().getHypernymPredicate(),
                            request.getConceptType())),
                    Direction.IN));
    return toResponse(request, graph);
  }

  @Override
  public List<ConceptInstanceResponse> query(
      Long projectId, String conceptType, Set<String> conceptInstanceIds) {
    if (projectId == null) {
      projectId = getProjectId(conceptType);
    }
    String graphStoreUrl = projectManager.getGraphStoreUrl(projectId);
    LPGDataQueryService graphStoreClient =
        (LPGDataQueryService) GraphStoreClientDriverManager.getClient(graphStoreUrl);
    GraphLPGRecordStruct graph =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new BatchVertexLPGRecordQuery(conceptInstanceIds, conceptType));
    return toResponse(graph);
  }

  private Long getProjectId(String conceptType) {
    Project project =
        projectManager.queryByNamespace(SPGTypeIdentifier.parse(conceptType).getNamespace());
    if (project == null) {
      throw new IllegalArgumentException(
          String.format("can not find project by namespace %s", conceptType));
    }
    return project.getId();
  }

  private ConceptLevelInstanceResponse toResponse(
      ConceptLevelInstanceRequest request, GraphLPGRecordStruct graph) {
    ConceptLevelInstanceResponse response = new ConceptLevelInstanceResponse();
    response.setConceptType(request.getConceptType());
    response.setRootConceptInstance(request.getRootConceptInstance());
    response.setChildren(toResponse(graph));
    return response;
  }

  private List<ConceptInstanceResponse> toResponse(GraphLPGRecordStruct graph) {
    return graph.getVertices().stream()
        .map(
            vertex -> {
              ConceptInstanceResponse instance = new ConceptInstanceResponse();
              instance.setId(vertex.getId());
              instance.setProperties(vertex.toPropertyMap());
              return instance;
            })
        .filter(it -> !ConceptIdentifier.ROOT.equals(it.getId()))
        .collect(Collectors.toList());
  }
}
