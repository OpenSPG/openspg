/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.infra.dao.mapper;

import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerTaskDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SchedulerTaskDOMapper {

  /** insert Task */
  Long insert(SchedulerTaskDO record);

  /** delete By jobId */
  int deleteByJobId(Long jobId);

  /** update By Id */
  Long update(SchedulerTaskDO record);

  /** get By id */
  SchedulerTaskDO getById(Long id);

  /** query By Condition */
  List<SchedulerTaskDO> query(SchedulerTaskQuery record);

  int selectCountByQuery(SchedulerTaskQuery record);

  /** query By InstanceId And nodeId */
  SchedulerTaskDO queryByInstanceIdAndNodeId(
      @Param("instanceId") Long instanceId, @Param("nodeId") String nodeId);

  /** query By InstanceId */
  List<SchedulerTaskDO> queryByInstanceId(Long instanceId);

  /** set Status By InstanceId */
  int setStatusByInstanceId(@Param("instanceId") Long instanceId, @Param("status") String status);

  /** update Lock */
  int updateLock(Long id);

  /** update Unlock */
  int updateUnlock(Long id);
}
