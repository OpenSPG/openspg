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

package com.antgroup.openspg.cloudext.impl.jobscheduler.local.service.impl;

import com.antgroup.openspg.cloudext.impl.jobscheduler.local.cmd.SchedulerJobInstQuery;
import com.antgroup.openspg.cloudext.impl.jobscheduler.local.repo.SchedulerJobInstRepository;
import com.antgroup.openspg.cloudext.impl.jobscheduler.local.service.SchedulerJobInstService;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class SchedulerJobInstServiceImpl implements SchedulerJobInstService {

    @Autowired
    private SchedulerJobInstRepository schedulerJobInstRepository;

    @Override
    public String create(SchedulerJobInst jobInst) {
        return schedulerJobInstRepository.save(jobInst);
    }

    @Override
    public List<SchedulerJobInst> queryRunningJobInsts() {
        SchedulerJobInstQuery jobInstQuery = new SchedulerJobInstQuery()
            .setStatus(Sets.newHashSet(JobInstStatusEnum.RUNNING_STATUS))
            .setOrderBy("id asc");
        return schedulerJobInstRepository.query(jobInstQuery);
    }

    @Override
    public List<SchedulerJobInst> queryToRunJobInsts() {
        SchedulerJobInstQuery jobInstQuery = new SchedulerJobInstQuery()
            .setStatus(Sets.newHashSet(JobInstStatusEnum.QUEUE, JobInstStatusEnum.INIT))
            .setOrderBy("id asc");
        return schedulerJobInstRepository.query(jobInstQuery);
    }

    @Override
    public void updateStatus(String jobInstId, JobInstStatusEnum status) {
        schedulerJobInstRepository.updateStatus(jobInstId, status);
    }
}
