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

import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerJobInfoQuery;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInfo;
import com.antgroup.openspg.server.core.reasoner.service.repo.ReasonerJobInfoRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInfoDO;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInfoDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.SPGJobInfoDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgreasoner.convertor.ReasonerJobInfoConvertor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReasonerJobInfoRepositoryImpl implements ReasonerJobInfoRepository {

  @Autowired private SPGJobInfoDOMapper spgJobInfoDOMapper;

  @Override
  public Long save(ReasonerJobInfo jobInfo) {
    SPGJobInfoDO jobInfoDO = ReasonerJobInfoConvertor.toDO(jobInfo);
    spgJobInfoDOMapper.insert(jobInfoDO);
    return jobInfoDO.getId();
  }

  @Override
  public int updateExternalJobId(Long reasonerJobInfoId, String externalJobInfoId) {
    SPGJobInfoDOExample example = new SPGJobInfoDOExample();
    example.createCriteria().andIdEqualTo(reasonerJobInfoId);

    SPGJobInfoDO jobInfoDO = new SPGJobInfoDO();
    jobInfoDO.setExternalJobInfoId(externalJobInfoId);
    return spgJobInfoDOMapper.updateByExampleSelective(jobInfoDO, example);
  }

  @Override
  public List<ReasonerJobInfo> query(ReasonerJobInfoQuery query) {
    SPGJobInfoDOExample example = new SPGJobInfoDOExample();
    SPGJobInfoDOExample.Criteria criteria = example.createCriteria();
    if (query.getReasonerJobInfoId() != null) {
      criteria.andIdEqualTo(query.getReasonerJobInfoId());
    }
    if (query.getExternalJobInfoId() != null) {
      criteria.andExternalJobInfoIdEqualTo(query.getExternalJobInfoId());
    }
    example.setOrderByClause("id desc");
    List<SPGJobInfoDO> spgJobInfoDOS = spgJobInfoDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(spgJobInfoDOS, ReasonerJobInfoConvertor::toModel);
  }
}
