/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.handler.impl.local;

import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : GenerateInstances.java, v 0.1 2023年12月01日 10:42 yangjin Exp $
 */
@Service
public class GenerateInstances implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteInstances.class);

    @Autowired
    SchedulerGenerateService schedulerGenerateService;

    @Override
    public void run() {
        try {
            Long startTime = System.currentTimeMillis();
            LOGGER.info("====== run GenerateInstances start %s======");
            schedulerGenerateService.generateInstances();
            Long time = System.currentTimeMillis() - startTime;
            LOGGER.info(String.format("====== run GenerateInstances end time:%s======", time));
        } catch (Exception e) {
            LOGGER.error("run GenerateInstances Exception", e);
        }
    }
}
