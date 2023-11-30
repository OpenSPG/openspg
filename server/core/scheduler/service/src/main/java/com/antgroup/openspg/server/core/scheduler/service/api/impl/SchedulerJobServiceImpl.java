/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.api.impl;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerJobService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangjin
 * @version : SchedulerJobServiceImpl.java, v 0.1 2023年11月30日 14:09 yangjin Exp $
 */
@Service
public class SchedulerJobServiceImpl implements SchedulerJobService {
    @Override
    public SchedulerJob submit(SchedulerJob job) {
        return null;
    }

    @Override
    public Boolean execute(Long id) {
        return null;
    }

    @Override
    public Boolean online(Long id) {
        return null;
    }

    @Override
    public Boolean offline(Long id) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public boolean update(SchedulerJob job) {
        return false;
    }

    @Override
    public Page<List<SchedulerJob>> search(SchedulerJob query) {
        return null;
    }
}
