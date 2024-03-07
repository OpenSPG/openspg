package com.antgroup.openspg.server.biz.schema.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGDataQueryService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.ObjectVertexRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptType;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptLevelInstanceRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptInstanceResponse;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptLevelInstanceResponse;
import com.antgroup.openspg.server.biz.schema.ConceptInstanceManager;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import com.google.common.collect.Sets;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptInstanceManagerImpl implements ConceptInstanceManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Autowired private SPGTypeService spgTypeService;

  @Override
  public ConceptLevelInstanceResponse queryConceptLevelInstance(
      ConceptLevelInstanceRequest request) {
    LPGDataQueryService graphStoreClient = getGraphStoreClient();

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
                new ObjectVertexRecordQuery(
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

  private LPGDataQueryService getGraphStoreClient() {
    return (LPGDataQueryService)
        GraphStoreClientDriverManager.getClient(appEnvConfig.getGraphStoreUrl());
  }

  private ConceptLevelInstanceResponse toResponse(
      ConceptLevelInstanceRequest request, GraphLPGRecordStruct graph) {
    ConceptLevelInstanceResponse response = new ConceptLevelInstanceResponse();
    response.setConceptType(request.getConceptType());
    response.setRootConceptInstance(request.getRootConceptInstance());
    response.setChildren(
        graph.getVertices().stream()
            .map(
                vertex -> {
                  ConceptInstanceResponse instance = new ConceptInstanceResponse();
                  instance.setId(vertex.getId());
                  instance.setProperties(vertex.toPropertyMap());
                  return instance;
                })
            .collect(Collectors.toList()));
    return response;
  }
}
