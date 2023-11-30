/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.api.impl;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerInstanceService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangjin
 * @version : SchedulerInstanceServiceImpl.java, v 0.1 2023年11月30日 14:10 yangjin Exp $
 */
@Service
public class SchedulerInstanceServiceImpl implements SchedulerInstanceService {
    @Override
    public Boolean stop(Long id) {
        return null;
    }

    @Override
    public Boolean setFinish(Long id) {
        return null;
    }

    @Override
    public Boolean reRun(Long id) {
        return null;
    }

    @Override
    public Boolean trigger(Long id) {
        return null;
    }

    @Override
    public Page<List<SchedulerInstance>> search(SchedulerInstance query) {
        return null;
    }
}
