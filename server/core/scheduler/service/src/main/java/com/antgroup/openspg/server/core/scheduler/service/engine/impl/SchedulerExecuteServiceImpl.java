/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.engine.impl;

import java.util.Date;
import java.util.List;

import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yangjin
 * @version : SchedulerExecuteServiceImpl.java, v 0.1 2023年12月04日 14:18 yangjin Exp $
 */
public class SchedulerExecuteServiceImpl implements SchedulerExecuteService {

    @Autowired
    SchedulerValue           schedulerValue;
    @Autowired
    SchedulerInstanceService schedulerInstanceService;

    @Override
    public void executeInstances() {
        List<SchedulerInstance> allInstance = getAllNotFinishInstance();
        if (CollectionUtils.isEmpty(allInstance)) {
            return;
        }
    }

    private List<SchedulerInstance> getAllNotFinishInstance() {
        SchedulerInstanceQuery record = new SchedulerInstanceQuery();
        Integer maxDays = schedulerValue.getExecuteMaxDay() + 1;
        Date startDate = DateUtils.addDays(new Date(), -maxDays);
        record.setStartCreateTime(startDate);
        List<SchedulerInstance> allInstance = schedulerInstanceService.getNotFinishInstance(record);
        return allInstance;
    }
}
