package com.antgroup.openspg.server.biz.service.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGDataQueryService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.ScanLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.server.api.facade.dto.service.request.RelationSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSamplingRequest;
import com.antgroup.openspg.server.biz.service.SamplingManager;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SamplingManagerImpl implements SamplingManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public List<VertexRecord> spgTypeSampling(SPGTypeSamplingRequest request) {
    LPGDataQueryService graphStoreClient = getGraphStoreClient();
    GraphLPGRecordStruct results =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new ScanLPGRecordQuery(request.getSpgType(), request.getLimit()));
    return results.getVertices();
  }

  @Override
  public List<EdgeRecord> relationSampling(RelationSamplingRequest request) {
    LPGDataQueryService graphStoreClient = getGraphStoreClient();
    GraphLPGRecordStruct results =
        (GraphLPGRecordStruct)
            graphStoreClient.queryRecord(
                new ScanLPGRecordQuery(
                    new EdgeTypeName(
                        request.getSrcSpgType(), request.getRelation(), request.getDstSpgType()),
                    request.getLimit()));
    return results.getEdges();
  }

  private LPGDataQueryService getGraphStoreClient() {
    return (LPGDataQueryService)
        GraphStoreClientDriverManager.getClient(appEnvConfig.getGraphStoreUrl());
  }
}
