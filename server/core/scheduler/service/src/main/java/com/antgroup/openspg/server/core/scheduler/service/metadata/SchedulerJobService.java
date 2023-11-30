/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;

/**
 * @author yangjin
 * @version : SchedulerService.java, v 0.1 2023年11月30日 13:50 yangjin Exp $
 */
public interface SchedulerJobService {

    /**
     * insert
     *
     * @param record
     * @return
     */
    int insert(SchedulerJob record);

    /**
     * delete By Id
     *
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * delete By Id List
     *
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * update
     *
     * @param record
     * @return
     */
    int update(SchedulerJob record);

    /**
     * get By Id
     *
     * @param id
     * @return
     */
    SchedulerJob getById(Long id);

    /**
     * query By Condition，query all if pageNo is null
     *
     * @param record
     * @return
     */
    Page<List<SchedulerJob>> query(SchedulerJob record);

    /**
     * get Count By Condition
     *
     * @param record
     * @return
     */
    int getCount(SchedulerJob record);

    /**
     * get By Id List
     *
     * @param ids
     * @return
     */
    List<SchedulerJob> getByIds(List<Long> ids);
}
