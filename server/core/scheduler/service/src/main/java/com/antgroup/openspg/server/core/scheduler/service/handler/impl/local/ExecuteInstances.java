/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.handler.impl.local;

import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yangjin
 * @version : ExecuteInstances.java, v 0.1 2023年12月01日 10:42 yangjin Exp $
 */
public class ExecuteInstances implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteInstances.class);

    SchedulerExecuteService schedulerExecuteService;

    public ExecuteInstances(SchedulerExecuteService schedulerExecuteService) {
        this.schedulerExecuteService = schedulerExecuteService;
    }

    @Override
    public void run() {
        try {
            Long startTime = System.currentTimeMillis();
            LOGGER.info("====== run ExecuteInstances start ======");
            schedulerExecuteService.executeInstances();
            Long time = System.currentTimeMillis() - startTime;
            LOGGER.info(String.format("====== run ExecuteInstances end time:%s ======", time));
        } catch (Exception e) {
            LOGGER.error("run ExecuteInstances Exception", e);
        }
    }
}
