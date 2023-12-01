/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.handler;

/**
 * @author yangjin
 * @version : SchedulerHandler.java, v 0.1 2023年11月30日 18:33 yangjin Exp $
 */
public interface SchedulerHandler {

    /**
     * scheduler timer entrance. execute Instances
     *
     * @return
     */
    void executeInstances();

    /**
     * scheduler generate Instances timer
     *
     * @return
     */
    void generateInstances();
}