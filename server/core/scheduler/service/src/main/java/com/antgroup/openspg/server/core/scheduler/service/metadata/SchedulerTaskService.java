/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;

/**
 * @author yangjin
 * @version : SchedulerService.java, v 0.1 2023年11月30日 13:50 yangjin Exp $
 */
public interface SchedulerTaskService {

    /**
     * insert Task
     * @param record
     * @return
     */
    Long insert(SchedulerTask record);

    /**
     * delete Task By Id
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * delete By Id List
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * update By Id
     * @param record
     * @return
     */
    Long update(SchedulerTask record);

    /**
     * insert Or Update，id is null to Update
     * @param record
     * @return
     */
    Long replace(SchedulerTask record);

    /**
     * get By Id
     * @param id
     * @return
     */
    SchedulerTask getById(Long id);

    /**
     * query By Condition，query all if pageNo is null
     * @param record
     * @return
     */
    Page<List<SchedulerTask>> query(SchedulerTaskQuery record);

    /**
     * get Count By Condition
     * @param record
     * @return
     */
    Long getCount(SchedulerTaskQuery record);

    /**
     * get By Id List
     * @param ids
     * @return
     */
    List<SchedulerTask> getByIds(List<Long> ids);

    /**
     * query By InstanceId And Type
     *
     * @param instanceId
     * @param type
     * @return
     */
    SchedulerTask queryByInstanceIdAndType(Long instanceId, String type);

    /**
     * query By InstanceId
     *
     * @param instanceId
     * @return
     */
    List<SchedulerTask> queryByInstanceId(Long instanceId);

    /**
     * query Base Column By InstanceId
     *
     * @param instanceId
     * @return
     */
    List<SchedulerTask> queryBaseColumnByInstanceId(Long instanceId);

    /**
     * Status By InstanceId
     *
     * @param instanceId
     * @param status
     * @return
     */
    int setStatusByInstanceId(Long instanceId, TaskStatus status);

    /**
     * update Extension By Lock
     *
     * @param record
     * @param extension
     * @return
     */
    int updateExtensionByLock(SchedulerTask record, String extension);

    /**
     * update Lock
     *
     * @param id
     * @return
     */
    int updateLock(Long id);

    /**
     * update Unlock
     *
     * @param id
     * @return
     */
    int updateUnlock(Long id);
}
