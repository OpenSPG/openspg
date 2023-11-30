/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.api.impl;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerTaskService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangjin
 * @version : SchedulerTaskServiceImpl.java, v 0.1 2023年11月30日 14:11 yangjin Exp $
 */
@Service
public class SchedulerTaskServiceImpl implements SchedulerTaskService {
    @Override
    public Page<List<SchedulerTask>> search(SchedulerTask query) {
        return null;
    }
}
