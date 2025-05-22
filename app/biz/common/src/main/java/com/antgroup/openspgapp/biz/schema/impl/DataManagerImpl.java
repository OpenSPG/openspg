package com.antgroup.openspgapp.biz.schema.impl;

import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import com.antgroup.openspg.server.biz.service.SamplingManager;
import com.antgroup.openspg.server.common.model.data.EntitySampleData;
import com.antgroup.openspgapp.biz.schema.DataManager;
import com.antgroup.openspgapp.biz.schema.convertor.DataConvertor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/impl/DataManagerImpl.class */
public class DataManagerImpl implements DataManager {

  @Autowired private SamplingManager samplingManager;

  @Override // com.antgroup.openspgapp.biz.schema.DataManager
  public List<EntitySampleData> getTypeSampleData(Long projectId, String name, Integer limit) {
    if (limit.intValue() <= 0) {
      limit = 10;
    }
    SPGTypeSamplingRequest spgTypeSamplingRequest = new SPGTypeSamplingRequest();
    spgTypeSamplingRequest.setSpgType(name);
    spgTypeSamplingRequest.setLimit(limit);
    spgTypeSamplingRequest.setProjectId(projectId);
    List<SPGTypeInstance> spgTypeInstances =
        this.samplingManager.spgTypeSampling(spgTypeSamplingRequest);
    if (CollectionUtils.isEmpty(spgTypeInstances)) {
      return Collections.emptyList();
    }
    return (List)
        spgTypeInstances.stream()
            .filter(
                instance -> {
                  return !"__ROOT__".equals(instance.getId());
                })
            .map(DataConvertor::toEntitySampleData)
            .collect(Collectors.toList());
  }
}
