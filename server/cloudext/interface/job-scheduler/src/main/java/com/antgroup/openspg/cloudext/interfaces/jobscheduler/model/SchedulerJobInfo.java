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

package com.antgroup.openspg.cloudext.interfaces.jobscheduler.model;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.antgroup.openspg.server.common.model.job.JobInfoStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SchedulerJobInfo extends BaseValObj {

  @Setter private String jobId;

  private final String jobName;

  private final String jobType;

  private final String cron;

  private final JobInfoStateEnum status;

  private final String idempotentId;
}
