/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.api.impl;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerJobServiceImpl.java, v 0.1 2023年11月30日 14:09 yangjin Exp $
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Override
    public SchedulerJob submitJob(SchedulerJob job) {
        return null;
    }

    @Override
    public Boolean executeJob(Long id) {
        return null;
    }

    @Override
    public Boolean onlineJob(Long id) {
        return null;
    }

    @Override
    public Boolean offlineJob(Long id) {
        return null;
    }

    @Override
    public Boolean deleteJob(Long id) {
        return null;
    }

    @Override
    public boolean updateJob(SchedulerJob job) {
        return false;
    }

    @Override
    public Page<List<SchedulerJob>> searchJobs(SchedulerJob query) {
        return null;
    }

    @Override
    public Boolean stopInstance(Long id) {
        return null;
    }

    @Override
    public Boolean setFinishInstance(Long id) {
        return null;
    }

    @Override
    public Boolean reRunInstance(Long id) {
        return null;
    }

    @Override
    public Boolean triggerInstance(Long id) {
        return null;
    }

    @Override
    public Page<List<SchedulerInstance>> searchInstances(SchedulerInstance query) {
        return null;
    }

    @Override
    public Page<List<SchedulerTask>> searchTasks(SchedulerTask query) {
        return null;
    }
}
