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
package com.antgroup.openspg.server.core.scheduler.service.api;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import java.util.List;

/** Scheduler Service:submit,execute,delete and other scheduler interfaces */
public interface SchedulerService {
  /** submit job */
  SchedulerJob submitJob(SchedulerJob job);

  /** execute Job */
  Boolean executeJob(Long jobId);

  /** enable Job */
  Boolean enableJob(Long jobId);

  /** disable Job */
  Boolean disableJob(Long jobId);

  /** delete Job */
  Boolean deleteJob(Long jobId);

  /** update Job */
  boolean updateJob(SchedulerJob job);

  /** get Job By id */
  SchedulerJob getJobById(Long jobId);

  /** search Jobs */
  List<SchedulerJob> searchJobs(SchedulerJob query);

  /** get Instance By id */
  SchedulerInstance getInstanceById(Long instanceId);

  /** stop Instance */
  Boolean stopInstance(Long instanceId);

  /** set Instance To Finish */
  Boolean setFinishInstance(Long instanceId);

  /** restart Instance */
  Boolean restartInstance(Long instanceId);

  /** trigger Instance */
  Boolean triggerInstance(Long instanceId);

  /** search Instances */
  List<SchedulerInstance> searchInstances(SchedulerInstance query);

  /** search Tasks */
  List<SchedulerTask> searchTasks(SchedulerTask query);
}
