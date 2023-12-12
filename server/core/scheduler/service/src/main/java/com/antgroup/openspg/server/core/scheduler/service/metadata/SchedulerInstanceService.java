/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.metadata;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import java.util.Date;
import java.util.List;

/**
 * @version : SchedulerService.java, v 0.1 2023-11-30 13:50 $
 */
public interface SchedulerInstanceService {

  /**
   * insert
   *
   * @param record
   * @return
   */
  Long insert(SchedulerInstance record);

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
   * get Max UniqueId By JobId
   *
   * @param jobId
   * @return
   */
  String getMaxUniqueIdByJobId(Long jobId);

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
  Long update(SchedulerInstance record);

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
  SchedulerInstance getByUniqueId(String instanceId);

  /**
   * query By Condition，query all if pageNo is null
   *
   * @param record
   * @return
   */
  Page<List<SchedulerInstance>> query(SchedulerInstanceQuery record);

  /**
   * get Count By Condition
   *
   * @param record
   * @return
   */
  Long getCount(SchedulerInstanceQuery record);

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
  List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record);

  /**
   * get Instance By task type,status,time
   *
   * @param taskType
   * @param status
   * @param startFinishTime
   * @param endFinishTime
   * @return
   */
  List<SchedulerInstance> getInstanceByTask(
      String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime);
}
