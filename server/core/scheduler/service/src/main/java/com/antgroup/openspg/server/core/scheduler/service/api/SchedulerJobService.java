/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.api;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;

/**
 * @author yangjin
 * @version : SchedulerService.java, v 0.1 2023年11月30日 13:50 yangjin Exp $
 */
public interface SchedulerJobService {
    /**
     * submit job
     *
     * @param job
     * @return
     */
    SchedulerJob submit(SchedulerJob job);

    /**
     * execute Job
     *
     * @param id
     * @return
     */
    Boolean execute(Long id);

    /**
     * online Job
     *
     * @param id
     * @return
     */
    Boolean online(Long id);

    /**
     * offline Job
     *
     * @param id
     * @return
     */
    Boolean offline(Long id);

    /**
     * delete Job
     *
     * @param id
     * @return
     */
    Boolean delete(Long id);

    /**
     * update Job
     *
     * @param job
     * @return
     */
    boolean update(SchedulerJob job);

    /**
     * search Jobs
     *
     * @param query
     * @return
     */
    Page<List<SchedulerJob>> search(SchedulerJob query);
}
