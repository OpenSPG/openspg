package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.server.api.facade.dto.service.request.RelationSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.RelationInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import java.util.List;

public interface SamplingManager {

  List<SPGTypeInstance> spgTypeSampling(SPGTypeSamplingRequest request);

  List<RelationInstance> relationSampling(RelationSamplingRequest request);
}
