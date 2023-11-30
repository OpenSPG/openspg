/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.api;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;

/**
 * @author yangjin
 * @version : SchedulerService.java, v 0.1 2023年11月30日 13:50 yangjin Exp $
 */
public interface SchedulerInstanceService {

    /**
     * stop Instance
     *
     * @param id
     * @return
     */
    Boolean stop(Long id);

    /**
     * set Finish Instance
     *
     * @param id
     * @return
     */
    Boolean setFinish(Long id);

    /**
     * reRun Instance
     *
     * @param id
     * @return
     */
    Boolean reRun(Long id);

    /**
     * trigger Instance
     *
     * @param id
     * @return
     */
    Boolean trigger(Long id);

    /**
     * search Instances
     *
     * @param query
     * @return
     */
    Page<List<SchedulerInstance>> search(SchedulerInstance query);

}
