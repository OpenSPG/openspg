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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import java.util.List;

/**
 * @version : SchedulerService.java, v 0.1 2023-11-30 13:50 $
 */
public interface SchedulerJobService {

  /**
   * insert
   *
   * @param record
   * @return
   */
  Long insert(SchedulerJob record);

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
  Long update(SchedulerJob record);

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
  Page<List<SchedulerJob>> query(SchedulerJobQuery record);

  /**
   * get Count By Condition
   *
   * @param record
   * @return
   */
  Long getCount(SchedulerJobQuery record);

  /**
   * get By Id List
   *
   * @param ids
   * @return
   */
  List<SchedulerJob> getByIds(List<Long> ids);
}
