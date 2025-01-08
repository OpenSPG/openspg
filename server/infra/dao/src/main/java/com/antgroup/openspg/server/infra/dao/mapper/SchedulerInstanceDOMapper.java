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

import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerInstanceDO;
import java.util.List;

public interface SchedulerInstanceDOMapper {
  /** insert Instance */
  Long insert(SchedulerInstanceDO record);

  /** delete By JobId */
  int deleteByJobId(Long jobId);

  /** update */
  Long update(SchedulerInstanceDO record);

  /** get By id */
  SchedulerInstanceDO getById(Long id);

  /** get By instanceId */
  SchedulerInstanceDO getByUniqueId(String uniqueId);

  /** query By Condition */
  List<SchedulerInstanceDO> query(SchedulerInstanceQuery record);

  /** query By Condition Count */
  int selectCountByQuery(SchedulerInstanceQuery record);

  /** get Not Finish Instance */
  List<SchedulerInstanceDO> getNotFinishInstance(SchedulerInstanceQuery record);
}
