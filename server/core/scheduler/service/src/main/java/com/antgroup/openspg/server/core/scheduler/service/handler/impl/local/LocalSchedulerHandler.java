/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.handler.impl.local;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PostConstruct;

import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.handler.SchedulerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yangjin
 * @version : QuartzSchedulerHandler.java, v 0.1 2023年11月30日 19:05 yangjin Exp $
 */
public class LocalSchedulerHandler implements SchedulerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalSchedulerHandler.class);

    private static final String HANDLER_TYPE = "local";

    private static ScheduledExecutorService SCHEDULER_EXECUTOR          = new ScheduledThreadPoolExecutor(1);
    private static ScheduledExecutorService GENERATE_INSTANCES_EXECUTOR = new ScheduledThreadPoolExecutor(1);

    @Autowired
    SchedulerValue schedulerValue;

    @Override
    @PostConstruct
    public void executeInstances() {
        if (!HANDLER_TYPE.equalsIgnoreCase(schedulerValue.getHandlerType())) {
            LOGGER.warn(String.format("=== ignore executeInstances inconsistent handlerType:%s ===", schedulerValue.getHandlerType()));
            return;
        }
        try {
            LOGGER.info("====== start executeInstances ======");
            SCHEDULER_EXECUTOR.scheduleAtFixedRate(new ExecuteInstances(), 0, schedulerValue.getExecuteInstancesPeriod(),
                    schedulerValue.getExecuteInstancesUnit());
            LOGGER.info("====== end executeInstances ======");
        } catch (Exception e) {
            LOGGER.error("executeInstances Exception", e);
        }
    }

    @Override
    @PostConstruct
    public void generateInstances() {
        if (!HANDLER_TYPE.equalsIgnoreCase(schedulerValue.getHandlerType())) {
            LOGGER.warn(String.format("=== ignore generateInstances inconsistent handlerType:%s ===", schedulerValue.getHandlerType()));
            return;
        }
        try {
            LOGGER.info("====== start generateInstances ======");
            GENERATE_INSTANCES_EXECUTOR.scheduleAtFixedRate(new GenerateInstances(), 0, schedulerValue.getGenerateInstancesPeriod(),
                    schedulerValue.getGenerateInstancesUnit());
            LOGGER.info("====== end generateInstances ======");
        } catch (Exception e) {
            LOGGER.error("generateInstances Exception", e);
        }
    }
}
