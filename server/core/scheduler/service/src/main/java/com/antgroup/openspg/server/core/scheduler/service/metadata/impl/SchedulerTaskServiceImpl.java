/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangjin
 * @version : SchedulerTaskServiceImpl.java, v 0.1 2023年11月30日 14:11 yangjin Exp $
 */
@Service
public class SchedulerTaskServiceImpl implements SchedulerTaskService {

    @Override
    public int insert(SchedulerTask record) {
        return 0;
    }

    @Override
    public int deleteById(Long id) {
        return 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return 0;
    }

    @Override
    public int update(SchedulerTask record) {
        return 0;
    }

    @Override
    public Long replace(SchedulerTask record) {
        return null;
    }

    @Override
    public SchedulerTask getById(Long id) {
        return null;
    }

    @Override
    public Page<List<SchedulerTask>> query(SchedulerTask record) {
        return null;
    }

    @Override
    public int getCount(SchedulerTask record) {
        return 0;
    }

    @Override
    public List<SchedulerTask> getByIds(List<Long> ids) {
        return null;
    }

    @Override
    public SchedulerTask queryByInstanceIdAndType(Long instanceId, String type) {
        return null;
    }

    @Override
    public List<SchedulerTask> queryByInstanceId(Long instanceId) {
        return null;
    }

    @Override
    public List<SchedulerTask> queryBaseColumnByInstanceId(Long instanceId) {
        return null;
    }

    @Override
    public int setStatusByInstanceId(Long instanceId, TaskStatus status) {
        return 0;
    }

    @Override
    public int updateExtensionByLock(SchedulerTask record, String extension) {
        return 0;
    }

    @Override
    public int updateLock(Long id) {
        return 0;
    }

    @Override
    public int updateUnlock(Long id) {
        return 0;
    }
}
