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

package com.antgroup.openspg.cloudext.impl.jobscheduler.local;

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClient;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClientDriver;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClientDriverManager;
import com.antgroup.openspg.common.model.datasource.connection.JobSchedulerConnectionInfo;

public class LocalJobSchedulerClientDriver implements JobSchedulerClientDriver {

  private static final JobSchedulerClient INSTANCE =
      new LocalJobSchedulerClient(new JobSchedulerConnectionInfo().setScheme("local"));

  static {
    JobSchedulerClientDriverManager.registerDriver(new LocalJobSchedulerClientDriver());
  }

  @Override
  public String driverScheme() {
    return "local";
  }

  @Override
  public JobSchedulerClient connect(JobSchedulerConnectionInfo connInfo) {
    return INSTANCE;
  }
}
