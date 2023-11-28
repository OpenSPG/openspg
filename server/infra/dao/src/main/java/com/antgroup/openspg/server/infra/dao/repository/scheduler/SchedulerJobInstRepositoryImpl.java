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

package com.antgroup.openspg.server.infra.dao.repository.scheduler;

import com.antgroup.openspg.cloudext.impl.jobscheduler.local.cmd.SchedulerJobInstQuery;
import com.antgroup.openspg.cloudext.impl.jobscheduler.local.repo.SchedulerJobInstRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.JobInstDO;
import com.antgroup.openspg.server.infra.dao.dataobject.JobInstDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.JobInstDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor.SchedulerJobInstConvertor;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerJobInstRepositoryImpl implements SchedulerJobInstRepository {

  @Autowired private JobInstDOMapper jobInstInfoDOMapper;

  @Override
  public String save(SchedulerJobInst jobInst) {
    JobInstDO jobInstDO = SchedulerJobInstConvertor.toDO(jobInst);
    jobInstInfoDOMapper.insert(jobInstDO);
    return String.valueOf(jobInstDO.getId());
  }

  @Override
  public List<SchedulerJobInst> query(SchedulerJobInstQuery query) {
    JobInstDOExample example = new JobInstDOExample();
    JobInstDOExample.Criteria criteria = example.createCriteria();

    if (query.getStatus() != null) {
      criteria.andStatusIn(CollectionsUtils.listMap(query.getStatus(), Enum::name));
    }
    if (query.getOrderBy() != null) {
      example.setOrderByClause(query.getOrderBy());
    }

    List<JobInstDO> jobInstDOS = jobInstInfoDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(jobInstDOS, SchedulerJobInstConvertor::toModel);
  }

  @Override
  public int updateStatus(String jobInstId, JobInstStatusEnum status) {
    JobInstDOExample example = new JobInstDOExample();
    example.createCriteria().andIdEqualTo(Long.valueOf(jobInstId));

    JobInstDO jobInstDO = new JobInstDO();
    jobInstDO.setStatus(status.name());
    return jobInstInfoDOMapper.updateByExampleSelective(jobInstDO, example);
  }
}
