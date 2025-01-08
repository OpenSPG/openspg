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

import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerInfoDO;
import java.util.List;

public interface SchedulerInfoDOMapper {

  /** insert Info */
  Long insert(SchedulerInfoDO record);

  /** update By Id */
  Long update(SchedulerInfoDO record);

  /** delete By id */
  int deleteById(Long id);

  /** get By id */
  SchedulerInfoDO getById(Long id);

  /** get By id */
  SchedulerInfoDO getByName(String name);

  /** query By Condition */
  List<SchedulerInfoDO> query(SchedulerInfoQuery record);

  int selectCountByQuery(SchedulerInfoQuery record);

  /** update Lock */
  int updateLock(Long id);

  /** update Unlock */
  int updateUnlock(Long id);
}
