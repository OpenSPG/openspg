/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.engine.impl;

import java.util.List;

import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.Status;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerEngineServiceImpl.java, v 0.1 2023年12月01日 11:29 yangjin Exp $
 */
@Service
public class SchedulerGenerateServiceImpl implements SchedulerGenerateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerGenerateServiceImpl.class);

    @Autowired
    SchedulerJobService    schedulerJobService;
    @Autowired
    SchedulerCommonService schedulerCommonService;

    @Override
    public void generateInstances() {
        List<SchedulerJob> allJob = getAllPeriodJob();
        LOGGER.info(String.format("getAllPeriodJob succeed size:%s", allJob.size()));
        if (CollectionUtils.isEmpty(allJob)) {
            return;
        }
        generatePeriodInstance(allJob);
    }

    public void generatePeriodInstance(List<SchedulerJob> allJob) {
        for (SchedulerJob job : allJob) {
            try {
                List<SchedulerInstance> instances = schedulerCommonService.generatePeriodInstance(job);
                LOGGER.info(String.format("generatePeriodInstance successful jobId:%s instances:%s", job.getId(), instances.size()));
            } catch (Exception e) {
                LOGGER.error(String.format("generatePeriodInstance error,job:%s", job.getId()), e);
            }
        }
    }

    private List<SchedulerJob> getAllPeriodJob() {
        SchedulerJobQuery record = new SchedulerJobQuery();
        record.setLifeCycle(LifeCycle.PERIOD.name());
        record.setStatus(Status.ONLINE.name());
        List<SchedulerJob> allJob = schedulerJobService.query(record).getData();
        return allJob;
    }
}
