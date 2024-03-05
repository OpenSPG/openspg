package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.RelationSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSamplingRequest;
import java.util.List;

public interface SamplingManager {

  List<VertexRecord> spgTypeSampling(SPGTypeSamplingRequest request);

  List<EdgeRecord> relationSampling(RelationSamplingRequest request);
}
