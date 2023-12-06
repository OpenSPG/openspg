/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.common;

import java.util.concurrent.TimeUnit;

import com.antgroup.openspg.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author yangjin
 */
@Component
public class SchedulerValue {

    @Value("${scheduler.handler.type:}")
    private String handlerType;

    @Value("${scheduler.execute.instances.period:}")
    private String executeInstancesPeriod;

    @Value("${scheduler.execute.instances.unit:}")
    private String executeInstancesUnit;

    @Value("${scheduler.generate.instances.period:}")
    private String generateInstancesPeriod;

    @Value("${scheduler.generate.instances.unit:}")
    private String generateInstancesUnit;

    @Value("${scheduler.execute.max.day:}")
    private String executeMaxDay;

    @Value("${scheduler.execute.env:}")
    private String executeEnv;

    public String getHandlerType() {
        return handlerType;
    }

    public Long getExecuteInstancesPeriod() {
        if (StringUtils.isBlank(executeInstancesPeriod)) {
            return null;
        }
        return Long.valueOf(executeInstancesPeriod);
    }

    public TimeUnit getExecuteInstancesUnit() {
        if (StringUtils.isBlank(executeInstancesPeriod)) {
            return null;
        }
        return TimeUnit.valueOf(executeInstancesUnit);
    }

    public Long getGenerateInstancesPeriod() {
        if (StringUtils.isBlank(generateInstancesPeriod)) {
            return null;
        }
        return Long.valueOf(generateInstancesPeriod);
    }

    public TimeUnit getGenerateInstancesUnit() {
        if (StringUtils.isBlank(generateInstancesUnit)) {
            return null;
        }
        return TimeUnit.valueOf(generateInstancesUnit);
    }

    public Integer getExecuteMaxDay() {
        if (StringUtils.isBlank(executeMaxDay)) {
            return 10;
        }
        return Integer.valueOf(executeMaxDay);
    }

    public String getExecuteEnv() {
        return executeEnv;
    }
}