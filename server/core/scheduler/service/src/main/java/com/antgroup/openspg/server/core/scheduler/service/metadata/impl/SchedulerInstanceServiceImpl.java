/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl;

import java.util.Date;
import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangjin
 * @version : SchedulerInstanceServiceImpl.java, v 0.1 2023年11月30日 14:10 yangjin Exp $
 */
@Service
public class SchedulerInstanceServiceImpl implements SchedulerInstanceService {

    @Override
    public int insert(SchedulerInstance record) {
        return 0;
    }

    @Override
    public int deleteById(Long id) {
        return 0;
    }

    @Override
    public int deleteByJobId(Long jobId) {
        return 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return 0;
    }

    @Override
    public int update(SchedulerInstance record) {
        return 0;
    }

    @Override
    public SchedulerInstance getById(Long id) {
        return null;
    }

    @Override
    public SchedulerInstance getByInstanceId(String instanceId) {
        return null;
    }

    @Override
    public Page<List<SchedulerInstance>> query(SchedulerInstance record) {
        return null;
    }

    @Override
    public int getCount(SchedulerInstance record) {
        return 0;
    }

    @Override
    public List<SchedulerInstance> getByIds(List<Long> ids) {
        return null;
    }

    @Override
    public List<SchedulerInstance> getNotFinishInstance(SchedulerInstance record) {
        return null;
    }

    @Override
    public List<SchedulerInstance> getInstanceByTask(String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime) {
        return null;
    }
}
