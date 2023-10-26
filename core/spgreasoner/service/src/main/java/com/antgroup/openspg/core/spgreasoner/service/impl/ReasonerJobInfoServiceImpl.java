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

package com.antgroup.openspg.core.spgreasoner.service.impl;

import com.antgroup.openspg.api.facade.dto.reasoner.request.ReasonerJobInfoQuery;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClient;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInfo;
import com.antgroup.openspg.common.service.datasource.DataSourceService;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerJobInfo;
import com.antgroup.openspg.core.spgreasoner.service.ReasonerJobInfoService;
import com.antgroup.openspg.core.spgreasoner.service.repo.ReasonerJobInfoRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReasonerJobInfoServiceImpl implements ReasonerJobInfoService {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ReasonerJobInfoRepository reasonerJobInfoRepository;

    @Override
    public Long create(ReasonerJobInfo reasonerJobInfo) {
        JobSchedulerClient jobSchedulerClient =
            dataSourceService.buildSharedJobSchedulerClient();
        Long reasonerJobInfoId = reasonerJobInfoRepository.save(reasonerJobInfo);

        SchedulerJobInfo schedulerJobInfo = new SchedulerJobInfo(
            null,
            reasonerJobInfo.getJobName(),
            JobTypeEnum.REASONING.name(),
            reasonerJobInfo.getCron(),
            reasonerJobInfo.getStatus(),
            String.valueOf(reasonerJobInfoId)
        );
        String schedulerJobInfoId = jobSchedulerClient.createJobInfo(schedulerJobInfo);

        reasonerJobInfo.setExternalJobInfoId(schedulerJobInfoId);
        reasonerJobInfoRepository.updateExternalJobId(reasonerJobInfoId, schedulerJobInfoId);
        return reasonerJobInfoId;
    }

    @Override
    public ReasonerJobInfo queryById(Long jobId) {
        List<ReasonerJobInfo> reasonerJobInfos = query(new ReasonerJobInfoQuery().setReasonerJobInfoId(jobId));
        if (CollectionUtils.isNotEmpty(reasonerJobInfos)) {
            return reasonerJobInfos.get(0);
        }
        return null;
    }

    @Override
    public List<ReasonerJobInfo> query(ReasonerJobInfoQuery query) {
        return reasonerJobInfoRepository.query(query);
    }
}
