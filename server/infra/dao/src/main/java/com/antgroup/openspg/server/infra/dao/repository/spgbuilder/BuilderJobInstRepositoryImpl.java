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

package com.antgroup.openspg.server.infra.dao.repository.spgbuilder;

import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.api.facade.JSON;
import com.antgroup.openspg.server.api.facade.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInst;
import com.antgroup.openspg.server.core.builder.model.service.BuilderStatusWithProgress;
import com.antgroup.openspg.server.core.builder.service.repo.BuilderJobInstRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOWithBLOBs;
import com.antgroup.openspg.server.infra.dao.mapper.SPGJobInstDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgbuilder.convertor.BuilderJobInstConvertor;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BuilderJobInstRepositoryImpl implements BuilderJobInstRepository {

  @Autowired private SPGJobInstDOMapper spgJobInstDOMapper;

  @Override
  public Long save(BuilderJobInst jobInst) {
    SPGJobInstDOWithBLOBs jobInstDO = BuilderJobInstConvertor.toDO(jobInst);
    spgJobInstDOMapper.insert(jobInstDO);
    return jobInstDO.getId();
  }

  @Override
  public int updateExternalJobId(Long builderJobInstId, String externalJobInstId) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    example.createCriteria().andIdEqualTo(builderJobInstId);

    SPGJobInstDOWithBLOBs jobInstDO = new SPGJobInstDOWithBLOBs();
    jobInstDO.setExternalJobInstId(externalJobInstId);
    return spgJobInstDOMapper.updateByExampleSelective(jobInstDO, example);
  }

  @Override
  public List<BuilderJobInst> query(BuilderJobInstQuery query) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    SPGJobInstDOExample.Criteria criteria = example.createCriteria();
    if (query.getBuildingJobInstId() != null) {
      criteria.andIdEqualTo(query.getBuildingJobInstId());
    }
    if (query.getExternalJobInstId() != null) {
      criteria.andExternalJobInstIdEqualTo(query.getExternalJobInstId());
    }

    List<SPGJobInstDOWithBLOBs> spgJobInstDOS =
        spgJobInstDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(spgJobInstDOS, BuilderJobInstConvertor::toModel);
  }

  @Override
  public int start(Long jobInstId, BuilderStatusWithProgress progress) {
    return updateStatus(jobInstId, progress, x -> x.setStartTime(new Date()));
  }

  @Override
  public int running(Long jobInstId, BuilderStatusWithProgress progress) {
    return updateStatus(jobInstId, progress, x -> {});
  }

  @Override
  public int finish(Long jobInstId, BuilderStatusWithProgress progress) {
    return updateStatus(jobInstId, progress, x -> x.setEndTime(new Date()));
  }

  @Override
  public int queue(Long jobInstId, BuilderStatusWithProgress progress) {
    return updateStatus(jobInstId, progress, x -> {});
  }

  private int updateStatus(
      Long jobInstId, BuilderStatusWithProgress process, Consumer<SPGJobInstDOWithBLOBs> setter) {
    SPGJobInstDOExample example = new SPGJobInstDOExample();
    example.createCriteria().andIdEqualTo(jobInstId);

    SPGJobInstDOWithBLOBs jobInstDO = new SPGJobInstDOWithBLOBs();
    jobInstDO.setStatus(process.getStatus().name());
    jobInstDO.setResult(JSON.serialize(process.getResult()));
    jobInstDO.setProgress(JSON.serialize(process.getProgress()));
    setter.accept(jobInstDO);
    return spgJobInstDOMapper.updateByExampleSelective(jobInstDO, example);
  }
}
