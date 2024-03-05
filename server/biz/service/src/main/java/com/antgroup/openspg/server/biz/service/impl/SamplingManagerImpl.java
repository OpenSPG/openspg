package com.antgroup.openspg.server.biz.service.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGDataQueryService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.ScanLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.api.facade.dto.service.request.RelationSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.RelationInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import com.antgroup.openspg.server.biz.service.SamplingManager;
import com.antgroup.openspg.server.biz.service.convertor.InstanceConvertor;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SamplingManagerImpl implements SamplingManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public List<SPGTypeInstance> spgTypeSampling(SPGTypeSamplingRequest request) {
    LPGDataQueryService graphStoreClient = getGraphStoreClient();
    GraphLPGRecordStruct results =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new ScanLPGRecordQuery(request.getSpgType(), request.getLimit()));
    return CollectionsUtils.listMap(results.getVertices(), InstanceConvertor::toInstance);
  }

  @Override
  public List<RelationInstance> relationSampling(RelationSamplingRequest request) {
    LPGDataQueryService graphStoreClient = getGraphStoreClient();
    GraphLPGRecordStruct results =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new ScanLPGRecordQuery(
                    new EdgeTypeName(
                        request.getSrcSpgType(), request.getRelation(), request.getDstSpgType()),
                    request.getLimit()));
    return CollectionsUtils.listMap(results.getEdges(), InstanceConvertor::toInstance);
  }

  private LPGDataQueryService getGraphStoreClient() {
    return (LPGDataQueryService)
        GraphStoreClientDriverManager.getClient(appEnvConfig.getGraphStoreUrl());
  }
}
