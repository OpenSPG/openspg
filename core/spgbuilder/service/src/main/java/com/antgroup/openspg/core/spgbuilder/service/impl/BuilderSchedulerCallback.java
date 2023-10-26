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

package com.antgroup.openspg.core.spgbuilder.service.impl;

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.SchedulerCallback;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.CallbackResult;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInst;
import com.antgroup.openspg.core.spgbuilder.model.service.FailureBuilderResult;
import com.antgroup.openspg.core.spgbuilder.service.BuilderJobInstService;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Slf4j
@Service
public class BuilderSchedulerCallback implements SchedulerCallback {

    @Autowired
    private BuilderJobInstService builderJobInstService;

    @Override
    public Set<JobTypeEnum> accept() {
        return Sets.newHashSet(JobTypeEnum.BUILDING);
    }

    @Override
    public CallbackResult polling(SchedulerJobInst jobInst) {
        BuilderJobInst builderJobInst = null;
        try {
            builderJobInst = builderJobInstService.pollingBuilderJob(jobInst);
        } catch (Throwable e) {
            log.warn("polling schedulerJobInstId={} for builder error", jobInst.getJobInstId(), e);
            builderJobInst = builderJobInstService
                .queryByExternalJobInstId(jobInst.getJobInstId());
            FailureBuilderResult result = new FailureBuilderResult(e.getMessage());
            if (builderJobInst != null) {
                builderJobInstService.updateToFailure(builderJobInst.getJobInstId(), result);
            }
            return new CallbackResult(JobInstStatusEnum.FAILURE, result);
        }
        return new CallbackResult(builderJobInst.getStatus(), builderJobInst.getResult());
    }
}
