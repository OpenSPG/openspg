/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.server.infra.dao.repository.spgreasoner;

import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOWithBLOBs;
import com.antgroup.openspg.server.infra.dao.mapper.SPGJobInstDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgreasoner.convertor.ReasonerJobInstConvertor;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.spgreasoner.service.repo.ReasonerJobInstRepository;
import com.antgroup.openspg.server.api.facade.JSON;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerJobInstQuery;
import com.antgroup.openspg.server.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.server.core.reasoner.model.service.FailureReasonerResult;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerStatusWithProgress;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReasonerJobInstRepositoryImpl implements ReasonerJobInstRepository {

  @Autowired private SPGJobInstDOMapper spgJobInstDOMapper;

  @Override
  public Long save(ReasonerJobInst jobInst) {
    SPGJobInstDOWithBLOBs jobInstDO = ReasonerJobInstConvertor.toDO(jobInst);
    spgJobInstDOMapper.insert(jobInstDO);
    return jobInstDO.getId();
  }

  @Override
  public int updateExternalJobId(Long reasonerJobInstId, String externalJobInstId) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    example.createCriteria().andIdEqualTo(reasonerJobInstId);

    SPGJobInstDOWithBLOBs jobInstDO = new SPGJobInstDOWithBLOBs();
    jobInstDO.setExternalJobInstId(externalJobInstId);
    return spgJobInstDOMapper.updateByExampleSelective(jobInstDO, example);
  }

  @Override
  public List<ReasonerJobInst> query(ReasonerJobInstQuery query) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    SPGJobInstDOExample.Criteria criteria = example.createCriteria();
    if (query.getReasonerJobInstId() != null) {
      criteria.andIdEqualTo(query.getReasonerJobInstId());
    }
    if (query.getExternalJobInstId() != null) {
      criteria.andExternalJobInstIdEqualTo(query.getExternalJobInstId());
    }

    List<SPGJobInstDOWithBLOBs> spgJobInstDOS =
        spgJobInstDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(spgJobInstDOS, ReasonerJobInstConvertor::toModel);
  }

  @Override
  public int updateStatus(Long jobInstId, ReasonerStatusWithProgress process) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    example.createCriteria().andIdEqualTo(jobInstId);

    SPGJobInstDOWithBLOBs jobInstDO = new SPGJobInstDOWithBLOBs();
    jobInstDO.setStatus(process.getStatus().name());
    jobInstDO.setResult(JSON.serialize(process.getResult()));
    jobInstDO.setProgress(JSON.serialize(process.getProgress()));
    return spgJobInstDOMapper.updateByExampleSelective(jobInstDO, example);
  }

  @Override
  public int updateToFailure(Long jobInstId, FailureReasonerResult result) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    example.createCriteria().andIdEqualTo(jobInstId);

    SPGJobInstDOWithBLOBs jobInstDO = new SPGJobInstDOWithBLOBs();
    jobInstDO.setStatus(JobInstStatusEnum.FAILURE.name());
    jobInstDO.setResult(JSON.serialize(result));
    return spgJobInstDOMapper.updateByExampleSelective(jobInstDO, example);
  }
}
