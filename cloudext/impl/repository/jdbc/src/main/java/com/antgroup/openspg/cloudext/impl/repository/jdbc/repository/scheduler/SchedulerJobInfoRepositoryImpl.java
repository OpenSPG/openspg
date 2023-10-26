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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.scheduler;

import com.antgroup.openspg.cloudext.impl.jobscheduler.local.repo.SchedulerJobInfoRepository;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.JobInfoDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper.JobInfoDOMapper;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.scheduler.convertor.SchedulerJobInfoConvertor;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class SchedulerJobInfoRepositoryImpl implements SchedulerJobInfoRepository {

    @Autowired
    private JobInfoDOMapper jobInfoDOMapper;

    @Override
    public String save(SchedulerJobInfo jobInfo) {
        JobInfoDO jobInfoDO = SchedulerJobInfoConvertor.toDO(jobInfo);
        jobInfoDOMapper.insert(jobInfoDO);
        return String.valueOf(jobInfoDO.getId());
    }
}
