/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.engine;

/**
 * @author yangjin
 * @version : SchedulerEngineService.java, v 0.1 2023年12月01日 11:25 yangjin Exp $
 */
public interface SchedulerExecuteService {

    /**
     * execute all Instances
     */
    void executeInstances();

    /**
     * execute Instance by id
     * @param id
     */
    void executeInstance(Long id);

}