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

package com.antgroup.openspg.server.core.builder.service;

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.server.api.facade.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInfo;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInst;
import com.antgroup.openspg.server.core.builder.model.service.FailureBuilderResult;
import java.util.List;

public interface BuilderJobInstService {

  Long create(BuilderJobInfo builderJobInfo, BuilderJobInst builderJobInst);

  List<BuilderJobInst> query(BuilderJobInstQuery query);

  /* ----------------------- *
  |      For Scheduler      |
  * ----------------------- */

  /**
   * Triggered by the scheduling system to poll the status of the builder job, the main process
   * includes:
   *
   * <p>1. If the job is in the final state, return directly; 2. If the job is running, query the
   * computing pool to determine whether the task is completed; 3. If the job is in waiting state,
   * try to submit the task;
   *
   * @param jobInst scheduling job instance
   * @return builder job instance
   */
  BuilderJobInst pollingBuilderJob(SchedulerJobInst jobInst);

  BuilderJobInst queryByExternalJobInstId(String externalJobInstId);

  int updateToFailure(Long jobInstId, FailureBuilderResult result);
}
