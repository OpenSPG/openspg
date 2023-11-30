/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata;

import java.util.Date;
import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;

/**
 * @author yangjin
 * @version : SchedulerService.java, v 0.1 2023年11月30日 13:50 yangjin Exp $
 */
public interface SchedulerInstanceService {

    /**
     * insert
     *
     * @param record
     * @return
     */
    int insert(SchedulerInstance record);

    /**
     * delete By Id
     *
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * delete By JobId
     *
     * @param jobId
     * @return
     */
    int deleteByJobId(Long jobId);

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
    int update(SchedulerInstance record);

    /**
     * get By Id
     *
     * @param id
     * @return
     */
    SchedulerInstance getById(Long id);

    /**
     * get By instanceId
     *
     * @param instanceId
     * @return
     */
    SchedulerInstance getByInstanceId(String instanceId);

    /**
     * query By Condition，query all if pageNo is null
     *
     * @param record
     * @return
     */
    Page<List<SchedulerInstance>> query(SchedulerInstance record);

    /**
     * get Count By Condition
     *
     * @param record
     * @return
     */
    int getCount(SchedulerInstance record);

    /**
     * get By id List
     *
     * @param ids
     * @return
     */
    List<SchedulerInstance> getByIds(List<Long> ids);

    /**
     * get Not Finish Instance
     *
     * @param record
     * @return
     */
    List<SchedulerInstance> getNotFinishInstance(SchedulerInstance record);

    /**
     * get Instance By task type,status,time
     *
     * @param taskType
     * @param status
     * @param startFinishTime
     * @param endFinishTime
     * @return
     */
    List<SchedulerInstance> getInstanceByTask(String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime);

}
